package edu.virginia.cs.mondol.fed;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

import edu.virginia.cs.mondol.fed.config.FEDConfigWrapper;
import edu.virginia.cs.mondol.fed.config.MyNetConfig;
import edu.virginia.cs.mondol.fed.utils.FedConstants;

public class BLETestActivity extends Activity {

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    private TextView mTextView;
    String mac;
    Context context;

    int ble_mac_count = 0;
    String [] beaconUUID;
    String[] ble_mac_list;
    int[] ble_mac_indices;
    int[] ble_tx;
    int[] ble_rssi;
    long[] ble_last_scan_time;
    long[] ble_scan_interval;
    int ix;
    String status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakeLockTag");
        wakeLock.acquire();
        context = getApplicationContext();

        setContentView(R.layout.activity_bletest);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mTextView.setText("Scanning... ");
            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(this.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        bleScanner = mBluetoothAdapter.getBluetoothLeScanner();

        MyNetConfig nc = FEDConfigWrapper.getNetConfig(context);
        ble_mac_count = 0;
        if (nc.beacon_indices != null) {
            ble_mac_indices = nc.beacon_indices;
            ble_mac_count = nc.beacon_indices.length;
            ble_mac_list = new String[ble_mac_count];
            beaconUUID = new String[ble_mac_count];
            ble_tx = new int[ble_mac_count];
            ble_rssi = new int[ble_mac_count];
            ble_last_scan_time = new long[ble_mac_count];
            ble_scan_interval = new long[ble_mac_count];

            for (int i = 0; i < ble_mac_count; i++) {
                ble_last_scan_time[ix] = System.currentTimeMillis();
                int ix = nc.beacon_indices[i];
                if (ix > 0 && ix <= FedConstants.BLE_MAC_LIST_ALL.length)
                    ble_mac_list[i] = FedConstants.BLE_MAC_LIST_ALL[ix - 1];
                else
                    ble_mac_list[i] = "xx";
            }
        }

        Log.i("Test Beacon count: ", ble_mac_count + "");
        refresh();
        bleScanner.startScan(mScanCallBack);


    }

    @Override
    public void onDestroy() {
        bleScanner.stopScan(mScanCallBack);
        wakeLock.release();
        super.onDestroy();
    }



    ScanCallback mScanCallBack = new ScanCallback() {

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.i(FedConstants.MYTAG, "Test BLE batch result is " + results.size());
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            //super.onScanResult(callbackType, result);
            mac = result.getDevice().getAddress();
            mac = mac.toLowerCase().replace(":", "");
            ix = searchBLEMac(mac);
            status = ix+"/"+ble_mac_count+": "+mac;
            if (ix < 0) {
                return;
            }

            if(result.getDevice().getUuids()!=null && result.getDevice().getUuids().length>0)
                beaconUUID[ix] = result.getDevice().getUuids()[0].toString();
            Log.i(FedConstants.MYTAG, "Test BLE onScanResult MAC: " + mac);
            ScanRecord scanRecord = result.getScanRecord();
            byte[] scanData = scanRecord.getBytes();
            ble_tx[ix] = scanData[29]; //txPower
            ble_rssi[ix] = result.getRssi(); //txPower
            ble_scan_interval[ix] = System.currentTimeMillis() - ble_last_scan_time[ix];
            ble_last_scan_time[ix] = System.currentTimeMillis();
            refresh();

        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.i(FedConstants.MYTAG, "Test BLE errorcode " + errorCode);
        }

    };

    public int searchBLEMac(String mac) {

        for (int i = 0; i < ble_mac_count; i++) {
            if (ble_mac_list[i].equals(mac))
                return i;
        }

        return -1;
    }


    void refresh() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String s = "BEACON Scan Results:\n";
                try {
                    for (int i = 0; i < ble_mac_count; i++) {
                        s += String.format("%d:%s,%d,%d,%.2f\n", ble_mac_indices[i], ble_mac_list[i], ble_tx[i], ble_rssi[i], ble_scan_interval[i] / 1000.0+","+beaconUUID[ix]);
                    }
                    mTextView.setText(s);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }


}
