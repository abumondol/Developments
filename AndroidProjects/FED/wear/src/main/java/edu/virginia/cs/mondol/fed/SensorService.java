package edu.virginia.cs.mondol.fed;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import edu.virginia.cs.mondol.fed.bites.BiteDetector;
import edu.virginia.cs.mondol.fed.utils.FEDUtils;
import edu.virginia.cs.mondol.fed.utils.FedConstants;
import edu.virginia.cs.mondol.fed.utils.FileUtils;
import edu.virginia.cs.mondol.fed.utils.SharedPrefUtil;

public class SensorService extends Service implements SensorEventListener {
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    Context context;
    BiteDetector bc;
    StringBuilder sb, sbInitialRepeat;
    //Vibrator v;

    int arrayIndex = 0, arrayLen = 80, sbCount = 0, maxSbCount = 12;
    long fileStartTime, sensorTimeReference, myTimeReference, serviceStartTime;
    float[][][] dataArr;
    long[][] timeArr;

    String fileName;
    int currentIndex = 0, fileIndex = 1;

    String comboDataFileName;
    boolean sensorTimeFlag = true;


    @Override
    public void onCreate() {
        super.onCreate();
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakeLockTag");
        wakeLock.acquire();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        context = this.getApplicationContext();
        //v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);

        serviceStartTime = fileStartTime = System.currentTimeMillis();
        fileName = FEDUtils.getFileName(context, fileStartTime, "sensor", serviceStartTime, fileIndex);
        SharedPrefUtil.putSharedPref(FedConstants.RUNNING_SENSOR_FILENAME, fileName, context);

        dataArr = new float[4][3][arrayLen];
        timeArr = new long[4][arrayLen];

        sb = new StringBuilder();
        bc = new BiteDetector(arrayLen, context);

        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        int rate = 100 * 1000;
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra(FedConstants.STOP)) {
            stopSelf();

        } else if (intent != null && intent.hasExtra(FedConstants.START)) {

        }

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        SharedPrefUtil.putSharedPref(FedConstants.RUNNING_SENSOR_FILENAME, null, context);
        mSensorManager.unregisterListener(this);
        wakeLock.release();
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (sensorTimeFlag) {
            sensorTimeFlag = false;
            sensorTimeReference = event.timestamp;
            myTimeReference = System.currentTimeMillis();
        }

        dataArr[currentIndex][0][arrayIndex] = event.values[0];
        dataArr[currentIndex][1][arrayIndex] = event.values[1];
        dataArr[currentIndex][2][arrayIndex] = event.values[2];

        timeArr[currentIndex][arrayIndex] = myTimeReference + (event.timestamp - sensorTimeReference) / FedConstants.NANO_MILLI_FACTOR;

        arrayIndex++;
        if (arrayIndex == arrayLen) {
            arrayIndex = 0;
            new BufferThread(dataArr[currentIndex], timeArr[currentIndex]).start();
            currentIndex++;
            if (currentIndex == dataArr.length)
                currentIndex = 0;
        }
    }

    public class BufferThread extends Thread {
        float[][] d_arr;
        long[] t_arr;

        public BufferThread(float[][] d, long[] t) {
            d_arr = d;
            t_arr = t;
        }

        public void run() {
            putArrayToSringBuilder(d_arr, t_arr);

            //This code is for appending tail data from last file to the beginning of first file
            /*if(sendDataFlag) {
                sendData();
                sb.append("0,100,100,");
                sb.append(arrayLen);
                sb.append("\n");
                putArrayToSringBuilder(d_arr, t_arr);
                sendDataFlag = false;
            }

            if (bc.addData(d_arr, t_arr)){
                sendDataFlag = true;
            }*/

            if (bc.addData(d_arr, t_arr)) {
                sendData();
            }

        }
    }

    void sendData() {
        Log.i(FedConstants.MYTAG, "Sending data...");
        StringBuilder sb2 = sb;
        sb = new StringBuilder();
        sbCount = 0;

        String str = sb2.toString();
        fileStartTime = System.currentTimeMillis();

        FileUtils.appendStringToFile(fileName, str, true);
        fileIndex++;
        fileName = FEDUtils.getFileName(context, fileStartTime, "sensor", serviceStartTime, fileIndex);
        SharedPrefUtil.putSharedPref(FedConstants.RUNNING_SENSOR_FILENAME, fileName, context);

        sendMessageBleSave();
        //startService(new Intent(context, NetService.class));
    }

    void putArrayToSringBuilder(float[][] d_arr, long[] t_arr) {
        int i;
        for (i = 0; i < arrayLen; i++) {
            sb.append(t_arr[i]);
            sb.append(",");
            sb.append(d_arr[0][i]);
            sb.append(",");
            sb.append(d_arr[1][i]);
            sb.append(",");
            sb.append(d_arr[2][i]);
            sb.append("\n");
        }

        sbCount++;
        if (sbCount == maxSbCount) {
            String str = sb.toString();
            FileUtils.appendStringToFile(fileName, str, true);
            //FileUtils.appendStringToFile2(comboDataFileName, str, true);
            sb = new StringBuilder();
            //sb.setLength(0);
            sbCount = 0;
        }
    }

    private void sendMessageBleSave() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent(FedConstants.BROADCAST_FOR_BLE);
        intent.putExtra(FedConstants.CODE, FedConstants.CODE_SAVE_BLE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

   /* public long getCurrentTime(){
        return System.currentTimeMillis() + time_diff;
    }*/

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
