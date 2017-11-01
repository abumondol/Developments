package edu.virginia.cs.mooncake.wada;

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

import java.nio.ByteBuffer;
import java.util.List;

import edu.virginia.cs.mooncake.wada.entities.BeaconReading;

public class BeaconViewActivity extends Activity {

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    private TextView mTextView;
    String mac;
    Context context;

    int ix, beaconCount = 0;
    String[] beaconMacList = {"984fee0f8605"};
    BeaconReading[] beaconList;
    byte[] timeBiteArray = new byte[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakeLockTag");
        wakeLock.acquire();
        context = getApplicationContext();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(this.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        bleScanner = mBluetoothAdapter.getBluetoothLeScanner();

        setContentView(R.layout.activity_beacon_view);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.tvTimeType);
                mTextView.setText("Scanning... ");

                beaconCount = beaconMacList.length;
                beaconList = new BeaconReading[beaconCount];
                for (int i = 0; i < beaconCount; i++) {
                    beaconList[i] = new BeaconReading();
                    beaconList[i].mac = beaconMacList[i];
                    beaconList[ix].scanTime = System.currentTimeMillis();
                }

                Log.i("Test Beacon count: ", beaconCount + "");
                bleScanner.startScan(mScanCallBack);

                refresh();
            }
        });

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
            //Log.i(FedConstants.MYTAG, "Test BLE batch result is " + results.size());
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            //super.onScanResult(callbackType, result);
            mac = result.getDevice().getAddress();
            mac = mac.toLowerCase().replace(":", "");
            ix = searchBLEMac(mac);
            if (ix < 0) {
                return;
            }

            if (result.getDevice().getUuids() != null && result.getDevice().getUuids().length > 0)
                beaconList[ix].uuid = result.getDevice().getUuids()[0].toString();

            ScanRecord scanRecord = result.getScanRecord();
            byte[] scanData = scanRecord.getBytes();
            beaconList[ix].txPower = scanData[11]; //txPower
            timeBiteArray[0] = scanData[15];
            timeBiteArray[1] = scanData[14];
            timeBiteArray[2] = scanData[13];
            timeBiteArray[3] = scanData[12];
            beaconList[ix].bcastTime = ByteBuffer.wrap(timeBiteArray).getInt();

            beaconList[ix].rssi = result.getRssi(); //txPower
            beaconList[ix].interval = (int) (System.currentTimeMillis() - beaconList[ix].scanTime);
            beaconList[ix].scanTime = System.currentTimeMillis();
            Log.i("MyTAG", "Test BLE onScanResult MAC: " + mac + "," + beaconList[ix].uuid + "," + result.getRssi()+","+beaconList[ix].interval);

            refresh();

        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.i("MyTAG", "Test BLE errorcode " + errorCode);
        }

    };

    public int searchBLEMac(String mac) {
        for (int i = 0; i < beaconCount; i++) {
            if (beaconMacList[i].equals(mac))
                return i;
        }
        return -1;
    }


    void refresh() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Log.i("MyTAG", "refresh called");
                String s = "BEACON Scan Results:\n";
                try {
                    for (int i = 0; i < beaconCount; i++) {
                        s += String.format("%s::%d\n%d, %d, %d\n", beaconList[i].mac, beaconList[i].bcastTime, beaconList[i].txPower, beaconList[i].rssi, beaconList[i].interval);
                    }
                    //Log.i("MyTAG", s);
                    mTextView.setText(s);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.i("MyTAG", "Exception in refresh\n" + ex.toString());
                }
            }
        });
    }


}
