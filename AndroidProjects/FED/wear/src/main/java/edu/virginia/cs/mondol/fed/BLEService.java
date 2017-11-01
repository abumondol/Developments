package edu.virginia.cs.mondol.fed;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

import edu.virginia.cs.mondol.fed.config.FEDConfig;
import edu.virginia.cs.mondol.fed.config.FEDConfigWrapper;
import edu.virginia.cs.mondol.fed.config.MyNetConfig;
import edu.virginia.cs.mondol.fed.utils.DateTimeUtils;
import edu.virginia.cs.mondol.fed.utils.FEDUtils;
import edu.virginia.cs.mondol.fed.utils.FedConstants;
import edu.virginia.cs.mondol.fed.utils.FileUtils;
import edu.virginia.cs.mondol.fed.utils.ServiceUtils;
import edu.virginia.cs.mondol.fed.utils.SharedPrefUtil;

public class BLEService extends Service {
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    Context context;
    FEDConfig fc;

    boolean bleStatus = false;
    StringBuilder sbBle;
    long fileStartTime, currentTime, serviceStartTime, lastDataSentTime = 0, lastTimeBleFound = 0; // dataSaveInterval = 10 * 60 * 1000, scanInterval = 30, scanDuration = 10;
    String fileName, mac;
    Handler handler;
    boolean bleFound = false, timeSyncRequire = false, writtenToFile = false;


    String[] ble_mac_list;
    int ble_mac_count = 0, fileIndex = 1;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;


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

        if (SharedPrefUtil.getSharedPrefLong(FedConstants.TIME_SYNC_WATCH, context) >= currentTime || Calendar.getInstance().get(Calendar.YEAR) < 2017) {
            SharedPrefUtil.putSharedPrefLong(FedConstants.TIME_SYNC_WATCH, 0, context);
        }

        if (SharedPrefUtil.getSharedPrefLong(FedConstants.TIME_SYNC_WATCH, context) == 0) {
            timeSyncRequire = true;
        } else {
            startSensorService(true);
        }

        serviceStartTime = fileStartTime = currentTime;
        fileName = FEDUtils.getFileName(context, fileStartTime, "ble", serviceStartTime, fileIndex);
        SharedPrefUtil.putSharedPref(FedConstants.RUNNING_BLE_FILENAME, fileName, context);

        MyNetConfig nc = FEDConfigWrapper.getNetConfig(context);
        if (nc.beacon_indices != null) {
            ble_mac_count = nc.beacon_indices.length;
            ble_mac_list = new String[ble_mac_count];
            for (int i = 0; i < ble_mac_count; i++) {
                int ix = nc.beacon_indices[i];
                if (ix > 0 && ix <= FedConstants.BLE_MAC_LIST_ALL.length)
                    ble_mac_list[i] = FedConstants.BLE_MAC_LIST_ALL[ix - 1];
                else
                    ble_mac_list[i] = "xx";
            }
        }

        SharedPrefUtil.putSharedPrefBoolean(FedConstants.AT_HOME, true, context);

        fc = FEDConfigWrapper.getFEDConfig();
        sbBle = new StringBuilder();
        handler = new Handler();
        handler.postDelayed(beaconRunnable, 5 * 1000);

        setAlarm();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(FedConstants.BROADCAST_FOR_BLE));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && (intent.hasExtra(FedConstants.STOP) || intent.hasExtra(FedConstants.PCON))) {
            if (intent.hasExtra(FedConstants.PCON)) {
                putBatteryInfo(FedConstants.WATCH_INFO_CODE_PCON);
            } else
                putBatteryInfo(FedConstants.WATCH_INFO_CODE_STOP);

            stopSelf(startId);

        } else if (intent != null && (intent.hasExtra(FedConstants.BOOT) || intent.hasExtra(FedConstants.DCON) || intent.hasExtra(FedConstants.START))) {

            if (intent.hasExtra(FedConstants.BOOT)) {
                putBatteryInfo(FedConstants.WATCH_INFO_CODE_BOOT);
                saveData(true, "BOOT");
            } else if (intent.hasExtra(FedConstants.DCON)) {
                putBatteryInfo(FedConstants.WATCH_INFO_CODE_DCON);
                saveData(true, "DCON");
            } else {
                putBatteryInfo(FedConstants.WATCH_INFO_CODE_START);
            }

            SharedPrefUtil.putSharedPrefInt(FedConstants.CONFIG_VERSION, fc.version, context);

        } else {
            stopSelf();
        }

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        handler.removeCallbacks(beaconRunnable);
        startBLE(false);
        startSensorService(false);

        if (isPlugged())
            saveData(true, "DESTROY PLUGGED");
        else
            saveData(false, "DESTROY UNPLUGGED");

        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }

        SharedPrefUtil.putSharedPref(FedConstants.RUNNING_BLE_FILENAME, null, context);

        wakeLock.release();
        super.onDestroy();

    }

    void startBLE(boolean flag) {
        if (bleScanner == null) {
            bleScanner = mBluetoothAdapter.getBluetoothLeScanner();
            if (bleScanner == null)
                return;
        }

        if (flag != bleStatus) {
            currentTime = System.currentTimeMillis();
            if (flag == false) {
                bleScanner.stopScan(mScanCallBack);
                bleStatus = false;
                Log.i(FedConstants.MYTAG, "BLE Stopped at:" + DateTimeUtils.getDateTimeString(currentTime) + ", BLE found: " + bleFound);

                SharedPrefUtil.putSharedPrefLong(FedConstants.LAST_TIME_BLE_FOUND, lastTimeBleFound, context);
                SharedPrefUtil.putSharedPrefLong(FedConstants.LAST_TIME_BLE_STOPPED, currentTime, context);

            } else {
                Log.i(FedConstants.MYTAG, "BLE Started at: " + DateTimeUtils.getDateTimeString(currentTime));
                SharedPrefUtil.putSharedPrefLong(FedConstants.LAST_TIME_BLE_STARTED, currentTime, context);
                bleFound = false;
                bleScanner.startScan(mScanCallBack);
                bleStatus = true;
            }
        }
    }

    ScanCallback mScanCallBack = new ScanCallback() {

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.i(FedConstants.MYTAG, "BLE batch result is " + results.size());
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            //super.onScanResult(callbackType, result);
            mac = result.getDevice().getAddress();
            mac = mac.toLowerCase().replace(":", "");
            if (!searchBLEMac(mac)) {
                return;
            }

            bleFound = true;
            lastTimeBleFound = System.currentTimeMillis();
            Log.i(FedConstants.MYTAG, "BLE onScanResult MAC: " + mac);
            ScanRecord scanRecord = result.getScanRecord();
            byte[] scanData = scanRecord.getBytes();

            sbBle.append(lastTimeBleFound);
            sbBle.append(",");
            sbBle.append(FedConstants.WATCH_INFO_CODE_BEACON);
            sbBle.append(",");
            sbBle.append(mac);
            sbBle.append(",");
            sbBle.append(scanData[29]); //txPower
            sbBle.append(",");
            sbBle.append(result.getRssi());
            sbBle.append("\n");
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
            Log.i(FedConstants.MYTAG, "BLE errorcode " + errorCode);
        }

    };

    public void saveData(boolean newFile, String msg) {
        Log.i(FedConstants.MYTAG, "BLE Save Data Called: " + newFile + ", " + msg);

        if (sbBle.length() > 0) {
            FileUtils.appendStringToFile(fileName, sbBle.toString(), true);
            sbBle.setLength(0);
            writtenToFile = true;
            //new fileSaveThread(sb, newFile).start();
        }

        if (newFile) {
            if (writtenToFile) {
                fileIndex++;
                fileStartTime = System.currentTimeMillis();
                fileName = FEDUtils.getFileName(context, fileStartTime, "ble", serviceStartTime, fileIndex);
                SharedPrefUtil.putSharedPref(FedConstants.RUNNING_BLE_FILENAME, fileName, context);
                writtenToFile = false;
            }

            SharedPrefUtil.putSharedPrefLong(FedConstants.LAST_UPLOAD_ATTEMPT_TIME, System.currentTimeMillis(), context);
            SharedPrefUtil.putSharedPref(FedConstants.LAST_UPLOAD_ATTEMPT_RESULT, "Called", context);
            startService(new Intent(context, NetService.class));
            lastDataSentTime = System.currentTimeMillis();
        }
    }

    public void setAlarm() {
        long diff = SharedPrefUtil.getSharedPrefLong(FedConstants.TIME_SYNC_SERVER, context) - SharedPrefUtil.getSharedPrefLong(FedConstants.TIME_SYNC_WATCH, context);
        int t = fc.alarm_hour * 60 + fc.alarm_minute;
        t = t - (int) diff / (60 * 1000);
        t = t % (24 * 60);

        int h = t / 60;
        int m = t % 60;

        SharedPrefUtil.putSharedPref(FedConstants.BATTERY_ALARM_TIME, h + ":" + m, context);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, h);
        calendar.set(Calendar.MINUTE, m);


        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    private Runnable beaconRunnable = new Runnable() {
        @Override
        public void run() {
            //Log.i(FedConstants.MYTAG, "BLE service runnable Handler called");

            if (timeSyncRequire) {
                if (SharedPrefUtil.getSharedPrefLong(FedConstants.TIME_SYNC_WATCH, context) > 0 && SharedPrefUtil.getSharedPrefLong(FedConstants.TIME_SYNC_RESPONSE_PERIOD, context) < fc.time_sync_min_response_time) {
                    timeSyncRequire = false;
                    startSensorService(true);

                } else {
                    boolean netServiceRunning = ServiceUtils.isMySensorServiceRunning(context, NetService.class.getName());
                    if (!netServiceRunning)
                        startService(new Intent(context, NetService.class).putExtra(FedConstants.DOWNLOAD_TIME, ""));

                    handler.postDelayed(beaconRunnable, fc.time_sync_repeat_try_interval);
                    return;
                }
            }

            if (bleStatus == true) {
                startBLE(false);
                putBatteryInfo(FedConstants.WATCH_INFO_CODE_BATTERY);

                currentTime = System.currentTimeMillis();
                if (currentTime - lastDataSentTime > fc.max_battery_send_interval) {
                    saveData(true, "Max Data Send interval");
                } else
                    saveData(false, "Regular Interval");

                handler.postDelayed(beaconRunnable, fc.ble_scan_interval);


            } else {
                startBLE(true);
                handler.postDelayed(beaconRunnable, fc.ble_scan_duration);
            }
        }
    };


    void putBatteryInfo(int code) {
        sbBle.append(System.currentTimeMillis());
        sbBle.append(",");
        sbBle.append(code);
        sbBle.append(",");
        if (isPlugged())
            sbBle.append(FedConstants.WATCH_INFO_CODE_PLUGGED);
        else
            sbBle.append(FedConstants.WATCH_INFO_CODE_NOT_PLUGGED);

        sbBle.append(",");
        sbBle.append(batteryStatus());
        sbBle.append("\n");
    }

    public void startSensorService(boolean start_flag) {

        boolean sensorServiceStatus = ServiceUtils.isMySensorServiceRunning(this.getApplicationContext(), SensorService.class.getName());
        if (start_flag && !sensorServiceStatus) {
            startService(new Intent(this, SensorService.class).putExtra(FedConstants.START, ""));
        } else if (!start_flag && sensorServiceStatus) {
            startService(new Intent(this, SensorService.class).putExtra(FedConstants.STOP, ""));
        }
    }

    public boolean isPlugged() {
        Intent chargeIntent = this.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (chargeIntent == null)
            return false;
        int plugged = chargeIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }

    public float batteryStatus() {
        Intent chargeIntent = this.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (chargeIntent == null)
            return 0;
        int level = chargeIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = chargeIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float) scale;
        //Log.i(FedConstants.MYTAG, "Battery Level: " + level + ",  Scale: " + scale + ",  Percentage: " + batteryPct);
        return batteryPct;
    }

    public boolean searchBLEMac(String mac) {

        for (int i = 0; i < ble_mac_count; i++) {
            if (ble_mac_list[i].equals(mac))
                return true;
        }

        return false;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            int code = intent.getIntExtra(FedConstants.CODE, 0);
            if (code == FedConstants.CODE_SAVE_BLE)
                saveData(true, "From Sensor");
        }
    };

    public class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(FedConstants.MYTAG, "Alrm Received: " + DateTimeUtils.getDateTimeString(System.currentTimeMillis()));
            SharedPrefUtil.putSharedPrefLong(FedConstants.LAST_ALARM_TIME, System.currentTimeMillis(), context);
            putBatteryInfo(FedConstants.WATCH_INFO_CODE_BATTERY_ALARM);
            saveData(true, "Alarm manager");
        }
    }

    /*public class fileSaveThread extends Thread {
        StringBuilder sb;
        boolean newFile;

        public fileSaveThread(StringBuilder sb, boolean newFile) {
            this.sb = sb;
            this.newFile = newFile;
        }

        @Override
        public void run() {
            try {
                Log.i("Thread Called", "for saving ble samples");
                FileUtils.appendStringToFile(fileName, sb.toString(), true);
                if (newFile) {
                    fileStartTime = System.currentTimeMillis();
                    fileIndex++;
                    fileName = FEDUtils.getFileName(context, fileStartTime, "ble", serviceStartTime, fileIndex);
                    SharedPrefUtil.putSharedPref(FedConstants.RUNNING_BLE_FILENAME, fileName, context);
                    writtenToFile = false;
                }

            } catch (Exception ex) {
                Log.i(FedConstants.MYTAG, "BLE File Save Error: " + ex.toString());
            }
        }

    }*/

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
