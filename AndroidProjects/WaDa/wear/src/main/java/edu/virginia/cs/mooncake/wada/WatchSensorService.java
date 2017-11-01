package edu.virginia.cs.mooncake.wada;

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

import edu.virginia.cs.mooncake.wada.utils.ConstantsUtil;
import edu.virginia.cs.mooncake.wada.utils.FileUtil;
import edu.virginia.cs.mooncake.wada.utils.SharedPrefUtil;
import edu.virginia.cs.mooncake.wada.utils.WadaUtils;


public class WatchSensorService extends Service implements SensorEventListener {
    Context context;
    int count, maxCount, rate;
    StringBuilder strBuilder;
    long startTime;
    String file_name = null, bcastMsg;
    int sensorType;
    boolean bcastStart = true;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer, mGyroscope, mRotationVector, mMagnetometer, mGravity, mStepCounter, mStepDetector, mOrientation, mHeartRate, mLinearAcceleration;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        super.onCreate();

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakeLockTag");
        wakeLock.acquire();

        startTime = System.currentTimeMillis();
        strBuilder = new StringBuilder();
        startTime = System.currentTimeMillis();
        strBuilder.append(startTime + "\n");
        count = 0;
        maxCount = 5 * 60 * 250;


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager
                .getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mRotationVector = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mMagnetometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mGravity = mSensorManager
                .getDefaultSensor(Sensor.TYPE_GRAVITY);
        mLinearAcceleration = mSensorManager
                .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mOrientation = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ORIENTATION);
        /*mHeartRate = mSensorManager
                .getDefaultSensor(Sensor.TYPE_HEART_RATE);*/
        /*mStepCounter = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetector = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);*/

        rate = SensorManager.SENSOR_DELAY_GAME;

        mSensorManager.registerListener(this, mAccelerometer, rate);
        mSensorManager.registerListener(this, mGyroscope, rate);

        if (mGravity != null)
            mSensorManager.registerListener(this, mGravity, rate);
        else
            Log.i("My Sensor Service", "Gravity doesn't exist");

        if (mRotationVector != null)
            mSensorManager.registerListener(this, mRotationVector, rate);
        else
            Log.i("My Sensor Service", "Rotation vector doesn't exist");

        if (mMagnetometer != null)
            mSensorManager.registerListener(this, mMagnetometer, rate);
        else
            Log.i("My Sensor Service", "Magnetometer doesn't exist");

        if (mOrientation != null)
            mSensorManager.registerListener(this, mOrientation, rate);
        else
            Log.i("My Sensor Service", "Orientation doesn't exist");

        if (mLinearAcceleration != null)
            mSensorManager.registerListener(this, mLinearAcceleration, rate);
        else
            Log.i("My Sensor Service", "Gravity doesn't exist");

        /*if (mHeartRate != null)
            mSensorManager.registerListener(this, mHeartRate, rate);
        else
            Log.i("My Sensor Service", "Heart Rate doesn't exist");

        if (mStepCounter != null)
            mSensorManager.registerListener(this, mStepCounter, rate);
        else
            Log.i("My Sensor Service", "Step Counter doesn't exist");

        if (mStepDetector != null)
            mSensorManager.registerListener(this, mStepDetector, rate);
        else
            Log.i("My Sensor Service", "Step Detector doesn't exist");*/

        SharedPrefUtil.removeEntry(ConstantsUtil.ACCURACY_ACL, this.getApplicationContext());
        SharedPrefUtil.removeEntry(ConstantsUtil.ACCURACY_GYRO, this.getApplicationContext());
        SharedPrefUtil.removeEntry(ConstantsUtil.ACCURACY_MAGNET, this.getApplicationContext());
        SharedPrefUtil.removeEntry(ConstantsUtil.ACCURACY_QUAT, this.getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.hasExtra("stop")) {
            stopSelf(startId);
        } else if (intent != null && intent.hasExtra("start")) {
            file_name = intent.getStringExtra("start") + ".wada";
            bcastMsg = WadaUtils.getTag(this.getApplicationContext());
            sendBCast(true);
        }

        context = this.getApplicationContext();
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
        wakeLock.release();
        saveData();
        sendBCast(false);
        SharedPrefUtil.removeEntry(ConstantsUtil.ACCURACY_ACL, this.getApplicationContext());
        SharedPrefUtil.removeEntry(ConstantsUtil.ACCURACY_GYRO, this.getApplicationContext());
        SharedPrefUtil.removeEntry(ConstantsUtil.ACCURACY_MAGNET, this.getApplicationContext());
        SharedPrefUtil.removeEntry(ConstantsUtil.ACCURACY_QUAT, this.getApplicationContext());

        super.onDestroy();
    }

    void sendBCast(Boolean flag) {
        String msg1 = " ";
        String msg2 = " ";
        if (flag) {
            String[] d = bcastMsg.split("-");
            msg1 = d[0] + "-" + d[1] + "-" + d[2];
            msg2 = d[3] + "-" + d[4] + "-" + d[5];

        }

        Intent i = new Intent("edu.virginia.cs.mooncake.m2feddata.SERVICE_MSG");
        i.putExtra("msg1", msg1);
        i.putExtra("msg2", msg2);
        sendBroadcast(i);
    }

    public void saveData() {
        if (strBuilder != null) {
            Log.i("MyTAG", "Saving sensor data. Duration: " + (System.currentTimeMillis() - startTime));
            new fileSaveThread(startTime, strBuilder).start();
            strBuilder = new StringBuilder();
            startTime = System.currentTimeMillis();
            count = 0;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (bcastStart) {
            if (event.sensor.getType() == 1) {
                bcastStart = false;
                Intent i = new Intent("edu.virginia.cs.mooncake.wada.sensor_start");
                i.putExtra("msg1", "started");
                LocalBroadcastManager.getInstance(this).sendBroadcast(i);
            }

        }
        sensorType = event.sensor.getType();
        strBuilder.append(event.timestamp);
        strBuilder.append(",");
        strBuilder.append(sensorType);
        strBuilder.append(",");
        strBuilder.append(event.accuracy);
        strBuilder.append(",");
        strBuilder.append(event.values[0]);
        if (event.values.length >= 2) {
            strBuilder.append(",");
            strBuilder.append(event.values[1]);
            strBuilder.append(",");
            strBuilder.append(event.values[2]);
        }

        if (event.values.length >= 4) {
            strBuilder.append(",");
            strBuilder.append(event.values[3]);
            strBuilder.append(",");
            strBuilder.append(event.values[4]);
        }


        strBuilder.append("\n");

        count++;
        //Log.i("MyTAG", "Count: " + count);
        if (count >= maxCount)
            saveData();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            SharedPrefUtil.putSharedPrefInt(ConstantsUtil.ACCURACY_ACL, accuracy, this.getApplicationContext());
        else if (sensor.getType() == Sensor.TYPE_GYROSCOPE)
            SharedPrefUtil.putSharedPrefInt(ConstantsUtil.ACCURACY_GYRO, accuracy, this.getApplicationContext());
        else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            SharedPrefUtil.putSharedPrefInt(ConstantsUtil.ACCURACY_MAGNET, accuracy, this.getApplicationContext());
        else if (sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
            SharedPrefUtil.putSharedPrefInt(ConstantsUtil.ACCURACY_QUAT, accuracy, this.getApplicationContext());

        /*strBuilder.append(System.currentTimeMillis());
        strBuilder.append(",");
        strBuilder.append(100+sensorType);
        strBuilder.append(",");
        strBuilder.append(accuracy);
        strBuilder.append("\n");*/
    }

    /**
     * *****************************************
     */
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public class fileSaveThread extends Thread {
        long st;
        StringBuilder sb;

        public fileSaveThread(long st, StringBuilder sb) {
            this.st = st;
            this.sb = sb;
        }

        @Override
        public void run() {
            try {
                Log.i("Thread Called", "for saving sensor samples");
                String str = sb.toString();
                if (str.length() == 0)
                    return;
                FileUtil.saveStringToFile(file_name, str);

            } catch (Exception ex) {
                Log.i("Sensor File Save", ex.toString());
            }
        }

    }
}
