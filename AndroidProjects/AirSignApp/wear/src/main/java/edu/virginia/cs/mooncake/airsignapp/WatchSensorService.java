package edu.virginia.cs.mooncake.airsignapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


public class WatchSensorService extends Service implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer, mGyroscope, mRotationVector, mMagnetometer, mGravity, mLinAccel;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    Context context;
    int count, maxCount, rate;
    double[][] sampleList;
    long startTime;
    String file_tag;
    boolean cancel = false;


    @Override
    public void onCreate() {
        super.onCreate();

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakeLockTag");
        wakeLock.acquire();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGravity = mSensorManager
                .getDefaultSensor(Sensor.TYPE_GRAVITY);
        mLinAccel = mSensorManager
                .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGyroscope = mSensorManager
                .getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mRotationVector = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mMagnetometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        rate = SensorManager.SENSOR_DELAY_GAME;
        mSensorManager.registerListener(this, mAccelerometer, rate);
        mSensorManager.registerListener(this, mGravity, rate);
        mSensorManager.registerListener(this, mLinAccel, rate);
        mSensorManager.registerListener(this, mGyroscope, rate);
        mSensorManager.registerListener(this, mRotationVector, rate);
        mSensorManager.registerListener(this, mMagnetometer, rate);

        context = this.getApplicationContext();
        startTime = System.currentTimeMillis();
        count = 0;
        maxCount = 2 * 60 * 6 * 50;
        sampleList = new double[maxCount + 10 * 6 * 50][6];
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.hasExtra("stop")) {
            cancel = false;
            stopSelf();
        } else if (intent != null && intent.hasExtra("cancel")) {
            cancel = true;
            stopSelf();
        }

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        wakeLock.release();
        if (cancel == false)
            sendData();
    }

    public void sendData() {

        if (count > 0) {
            double[][] data = new double[count][];
            for (int i = 0; i < count; i++)
                data[i] = sampleList[i];

            Log.i("sendData", "sample count: " + count);

            try {
                byte[] bytes = serialize(data);
                Intent i = new Intent(this.getApplicationContext(), WearIntentService.class);
                i.putExtra("action", "send");
                i.putExtra("data", bytes);
                i.putExtra("file_tag", Build.SERIAL + "-" + startTime + "-" + System.currentTimeMillis());
                startService(i);
            } catch (Exception ex) {
                Log.i("Error in sendData", ex.toString());
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sampleList[count][0] = event.timestamp;
        sampleList[count][1] = event.sensor.getType();
        sampleList[count][2] = event.accuracy;
        sampleList[count][3] = event.values[0];
        sampleList[count][4] = event.values[1];
        sampleList[count][5] = event.values[2];
        count++;

        if (count >= maxCount)
            this.stopSelf();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(obj);
        return b.toByteArray();
    }


    /**
     * *****************************************
     */
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
