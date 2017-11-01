package edu.virginia.cs.mooncake.wada;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.List;

import edu.virginia.cs.mooncake.wada.utils.FileUtil;


public class BeaconService extends Service {
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    Context context;

    boolean bleStatus = false;
    StringBuilder sbBle;
    long currentTime, lastTimeBleFound = 0;
    String file_name, mac;
    boolean bleFound = false;

    String[] ble_mac_list={"984fee0f8605"};
    int ble_mac_count = 0, beaconReadingCount = 0;
    byte[] timeBiteArray = new byte[4];

    @Override
    public void onCreate() {
        super.onCreate();

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakeLockTag");
        wakeLock.acquire();

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(this.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        bleScanner = mBluetoothAdapter.getBluetoothLeScanner();

        context = this.getApplicationContext();
        currentTime = System.currentTimeMillis();

        sbBle = new StringBuilder();
        sbBle.append(System.currentTimeMillis()+"\n");
        if (ble_mac_list != null)
            ble_mac_count = ble_mac_list.length;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.hasExtra("stop")) {
            stopSelf(startId);
        } else if (intent != null && intent.hasExtra("start")) {
            file_name = intent.getStringExtra("start") + ".wada";
            startBLE(true);
        }

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        startBLE(false);
        FileUtil.saveStringToFile(file_name, sbBle.toString());
        wakeLock.release();
        super.onDestroy();

    }

    void startBLE(boolean flag) {
        if (bleScanner == null) {
            this.stopSelf();
            return;
            //bleScanner = mBluetoothAdapter.getBluetoothLeScanner();
            //if (bleScanner == null)
            //return;
        }

        if (flag != bleStatus) {
            currentTime = System.currentTimeMillis();
            if (flag == false) {
                bleScanner.stopScan(mScanCallBack);
                bleStatus = false;

            } else {
                bleScanner.startScan(mScanCallBack);
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
            mac = mac.toLowerCase().replace(":", "");
            /*if (!searchBLEMac(mac)) {
                return;
            }*/

            lastTimeBleFound = System.currentTimeMillis();
            Log.i("MyTAG", "BLE onScanResult MAC: " + mac);
            ScanRecord scanRecord = result.getScanRecord();
            byte[] scanData = scanRecord.getBytes();
            timeBiteArray[0] = scanData[15];
            timeBiteArray[1] = scanData[14];
            timeBiteArray[2] = scanData[13];
            timeBiteArray[3] = scanData[12];

            sbBle.append(lastTimeBleFound);
            sbBle.append(",");
            sbBle.append(mac);
            sbBle.append(",");
            sbBle.append(ByteBuffer.wrap(timeBiteArray).getInt());
            sbBle.append(",");
            sbBle.append(scanData[29]); //txPower
            sbBle.append(",");
            sbBle.append(result.getRssi());
            sbBle.append("\n");

            /*String s="";
            for(int i=0;i<scanData.length;i++){
                //sbBle.append(String.format("%02X ", scanData[i]));
                s+=String.format("%02X ", scanData[i]);
            }
            Log.i("MyTAG", s);*/

            beaconReadingCount++;
            if (beaconReadingCount >= 200) {
                FileUtil.saveStringToFile(file_name, sbBle.toString());
                sbBle.setLength(0);
                beaconReadingCount = 0;
            }

        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            /*sbBle.append(System.currentTimeMillis());
            sbBle.append(",");
            sbBle.append(-1);
            sbBle.append(",");
            sbBle.append(errorCode);
            sbBle.append("\n");*/
            Log.i("MyTAG", "BLE errorcode " + errorCode);
        }

    };

    public boolean searchBLEMac(String mac) {

        if (ble_mac_list == null)
            return true;

        for (int i = 0; i < ble_mac_count; i++) {
            if (ble_mac_list[i].equals(mac))
                return true;
        }

        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
