package edu.virginia.cs.mooncake.m2feddata;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import edu.virginia.cs.mooncake.m2feddata.myutils.FileUtil;
import edu.virginia.cs.mooncake.m2feddata.myutils.M2FEDUtil;
import edu.virginia.cs.mooncake.m2feddata.myutils.MC;
import edu.virginia.cs.mooncake.m2feddata.myutils.SharedPrefUtil;

public class WatchSensorService extends Service implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer, mGyroscope, mRotationVector, mMagnetometer, mGravity;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    Context context;
    int count, maxCount, rate;
    StringBuilder strBuilder;
    long startTime;
    String file_name;
    int sensorType;

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
        maxCount = 60 * 375;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGravity = mSensorManager
                .getDefaultSensor(Sensor.TYPE_GRAVITY);
        mGyroscope = mSensorManager
                .getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mRotationVector = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mMagnetometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

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

        sendBCast(true);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.hasExtra("stop")) {
            stopSelf(startId);
        } else if (intent != null && intent.hasExtra("start")) {
            file_name = intent.getStringExtra("start");
        }

        context = this.getApplicationContext();
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        wakeLock.release();
        sendData();
        sendBCast(false);
    }

    void sendBCast(Boolean flag) {
        String msg1 = " ";
        String msg2 = " ";
        if (flag) {
            msg1 = "Collecing Data";

            int familyId = SharedPrefUtil.getSharedPrefInt(MC.FAMILY_ID, this.getApplicationContext());
            int memberId = SharedPrefUtil.getSharedPrefInt(MC.MEMBER_ID, this.getApplicationContext());
            int hand = SharedPrefUtil.getSharedPrefInt(MC.HAND, this.getApplicationContext());
            String handStr = "Undefined";
            if (hand == 1)
                handStr = "Right";
            else if (hand == 2)
                handStr = "Left";
            msg2 = "FID: " + M2FEDUtil.getFormattedNumber(familyId);
            msg2 += ", MID: " + M2FEDUtil.getFormattedNumber(memberId);
            msg2 += ", " + handStr;
        }

        Intent i = new Intent("edu.virginia.cs.mooncake.m2feddata.SERVICE_MSG");
        i.putExtra("msg1", msg1);
        i.putExtra("msg2", msg2);
        sendBroadcast(i);
    }

    public void sendData() {
        if (strBuilder != null) {
            Log.i("MyTAG", "Sending File. Duration: " + (System.currentTimeMillis() - startTime));
            new fileSaveThread(startTime, strBuilder).start();
            strBuilder = new StringBuilder();
            startTime = System.currentTimeMillis();
            count = 0;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sensorType = event.sensor.getType();
        strBuilder.append(event.timestamp);
        strBuilder.append(",");
        strBuilder.append(sensorType);
        strBuilder.append(",");
        strBuilder.append(event.accuracy);
        strBuilder.append(",");
        strBuilder.append(event.values[0]);
        strBuilder.append(",");
        strBuilder.append(event.values[1]);
        strBuilder.append(",");
        strBuilder.append(event.values[2]);
        strBuilder.append("\n");

        count++;
        //Log.i("MyTAG", "Count: " + count);
        if (count >= maxCount)
            sendData();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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


    /**
     * *****************************************
     */
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
