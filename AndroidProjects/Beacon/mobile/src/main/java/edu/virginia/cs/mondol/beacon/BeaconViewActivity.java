package edu.virginia.cs.mondol.beacon;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.virginia.cs.mondol.beacon.myutils.BeaconUtils;
import edu.virginia.cs.mondol.beacon.myutils.DateTimeUtils;
import edu.virginia.cs.mondol.beacon.myutils.FileUtils;

public class BeaconViewActivity extends AppCompatActivity {

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    Handler handler;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    private ScanSettings scanSettings;
    private ArrayList<ScanFilter> scanFilters;

    String[] macs = {"F1:B5:33:9B:FB:A0"};
    String mac, fileName;
    int filter, mode, save;
    boolean bleStatus = false;
    Beacon[] beacons;
    StringBuilder sbData, sbShow;
    int index, count = 0, maxCountSave = 1500;
    long currentTime, bleRemoveLimit = 30 * 1000;
    long timeLogIndex = 0;

    TextView tvScanResult, tvTimeLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_view);

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakeLockTag");
        wakeLock.acquire();
        tvScanResult = (TextView) findViewById(R.id.tvScanResult);
        tvTimeLog = (TextView) findViewById(R.id.tvTimeLog);

        handler = new Handler();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        filter = intent.getIntExtra("filter", MyConstants.FILTER_CODE_ALL);
        mode = intent.getIntExtra("mode", ScanSettings.SCAN_MODE_LOW_POWER);
        save = intent.getIntExtra("save", MyConstants.SAVE_CODE_NO);
        mac = intent.getStringExtra("mac");

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(this.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        bleScanner = mBluetoothAdapter.getBluetoothLeScanner();

        scanSettings = new ScanSettings.Builder()
                .setScanMode(mode)
                .build();

        if (filter == MyConstants.FILTER_CODE_FILE) {
            macs = FileUtils.getMacList();
        } else if (filter == MyConstants.FILTER_CODE_EMBED) {

        } else if (filter == MyConstants.FILTER_CODE_INPUT) {
            macs = new String[1];
            macs[0] = mac;

        } else {
            macs = null;
        }

        if (macs != null) {
            scanFilters = new ArrayList<>();
            beacons = new Beacon[macs.length];

            for (int i = 0; i < macs.length; i++) {
                ScanFilter sf = new ScanFilter.Builder().setDeviceAddress(macs[i].toUpperCase()).build();
                scanFilters.add(sf);
                beacons[i] = new Beacon();
                beacons[i].mac = macs[i].toUpperCase();
            }
        }

        sbData = new StringBuilder();
        sbShow = new StringBuilder();
        startBLE(true);
        handler.postDelayed(runnable, 3 * 1000);

        fileName = "PhoneBeacon_" + Build.SERIAL + "_" + DateTimeUtils.getDateTimeString(System.currentTimeMillis()).replace(":", "-").replace(" ", "_") + ".csv";
        logTime();

    }

    @Override
    protected void onDestroy() {
        logTime();
        saveData();
        wakeLock.release();
        startBLE(false);
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    public void btnClick(View v) {
        if (v.getId() == R.id.btnTimeLog) {
            logTime();

        } else if (v.getId() == R.id.btnClose)
            this.finish();

    }

    void logTime() {
        long t = System.currentTimeMillis();
        sbData.append(timeLogIndex + "," + t + "\n");
        tvTimeLog.setText(timeLogIndex + " : " + DateTimeUtils.getDateTimeString(t));
        timeLogIndex++;
    }

    void startBLE(boolean flag) {
        if (bleScanner == null) {
            return;
        }

        if (flag != bleStatus) {
            if (flag == false) {
                bleScanner.stopScan(mScanCallBack);
                bleStatus = false;

            } else {
                bleScanner.startScan(scanFilters, scanSettings, mScanCallBack);
                bleStatus = true;
            }
        }
    }

    ScanCallback mScanCallBack = new ScanCallback() {

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            //Log.i(FedConstants.MYTAG, "BLE batch result is " + results.size());
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            //super.onScanResult(callbackType, result);
            mac = result.getDevice().getAddress();
            index = searchBeaconMac(mac);
            if (index == -1) {
                if (filter == MyConstants.FILTER_CODE_ALL)
                    index = addBeacon(mac);
                else {
                    Log.i("MyTAG", "Filter is not All, but beacon found not in list: " + mac);
                    return;
                }
            }

            currentTime = System.currentTimeMillis();
            beacons[index].lastTimeFound = currentTime;
            beacons[index].counts++;
            beacons[index].totalRssi += result.getRssi();
            Log.i("MyTAG", "BLE onScanResult MAC: " + mac);

            sbData.append(mac);
            sbData.append(",");
            sbData.append(currentTime);
            sbData.append(",");
            sbData.append(result.getRssi());
            sbData.append(",");
            sbData.append(result.getScanRecord().getBytes()[29]); //txPower for iBeacon
            sbData.append(", ");

            String s = BeaconUtils.bytesToHex(result.getScanRecord().getBytes());
            Log.i("MyTAG", result.getScanRecord().getBytes().length+", "+s.length()+", "+s);
            sbData.append(s);
            sbData.append("\n");

            count++;
            if (count > maxCountSave) {
                saveData();
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.i("MyTAG", "BLE errorcode " + errorCode);
        }

    };

    void saveData() {
        if (save == 0)
            return;
        FileUtils.appendStringToFile(fileName, sbData.toString());
        sbData = new StringBuilder();
        count = 0;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            show();
            handler.postDelayed(runnable, 1000);
        }
    };

    public int searchBeaconMac(String mac) {

        if (beacons == null)
            return -1;

        for (int i = 0; i < beacons.length; i++) {
            if (beacons[i].mac.equals(mac))
                return i;
        }

        return -1;
    }

    int addBeacon(String mac) {
        if (beacons == null) {
            beacons = new Beacon[1];
            beacons[0] = new Beacon();
            beacons[0].mac = mac;
            return 0;
        }

        Beacon[] bs = new Beacon[beacons.length + 1];
        for (int i = 0; i < beacons.length; i++)
            bs[i] = beacons[i];

        bs[beacons.length] = new Beacon();
        bs[beacons.length].mac = mac;
        beacons = bs;
        return beacons.length - 1;
    }

    void removeBeacon(int index) {
        if (index < 0)
            return;
        if (beacons.length == 1) {
            beacons = null;
            return;
        }

        Beacon[] bs = new Beacon[beacons.length];
        for (int i = 0, j = 0; i < beacons.length; i++) {
            if (i != index) {
                bs[j] = beacons[i];
                j++;
            }
        }

        beacons = bs;
    }

    void show() {
        if (beacons == null) {
            tvScanResult.setText("Scanning... No beacon found yet.");
            return;
        }

        sbShow = new StringBuilder();
        sbShow.append("Beacon Count: ");
        sbShow.append(beacons.length);
        sbShow.append("\n");

        currentTime = System.currentTimeMillis();
        for (int i = 0; i < beacons.length; i++) {
            sbShow.append(i + 1);
            sbShow.append(" : ");
            sbShow.append(beacons[i].mac);
            sbShow.append(", ");
            sbShow.append(beacons[i].counts);
            sbShow.append(", ");

            if (beacons[i].counts != 0) {
                beacons[i].lastMeanRssi = beacons[i].totalRssi / beacons[i].counts;
                beacons[i].counts = 0;
                beacons[i].totalRssi = 0;
            }

            sbShow.append(beacons[i].lastMeanRssi);
            sbShow.append(", ");
            if (beacons[i].lastTimeFound == 0)
                sbShow.append("Not found yet");
            else
                sbShow.append((currentTime - beacons[i].lastTimeFound) / 1000);

            sbShow.append("\n");
        }

        tvScanResult.setText(sbShow.toString());
    }

}
