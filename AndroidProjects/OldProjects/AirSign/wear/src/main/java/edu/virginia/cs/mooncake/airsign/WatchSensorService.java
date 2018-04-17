package edu.virginia.cs.mooncake.airsign;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.google.android.gms.wearable.Asset;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import edu.virginia.cs.mooncake.airsign.utils.FileUtil;


public class WatchSensorService extends Service implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer, mGyroscope, mRotationVector, mMagnetometer, mGravity;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    Context context;
    int count, maxCount, rate;
    double[][] sampleList;
    long startTime;
    String file_tag;
    int sensorType;

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
        mGyroscope = mSensorManager
                .getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mRotationVector = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mMagnetometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        rate = SensorManager.SENSOR_DELAY_GAME;
        mSensorManager.registerListener(this, mAccelerometer, rate);
        mSensorManager.registerListener(this, mGravity, rate);
        mSensorManager.registerListener(this, mGyroscope, rate);
        mSensorManager.registerListener(this, mRotationVector, rate);
        mSensorManager.registerListener(this, mMagnetometer, rate);

        startTime = System.currentTimeMillis();
        startTime = System.currentTimeMillis();
        count = 0;
        maxCount = 5 * 60 * 375;
        sampleList = new double[maxCount][6];
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.hasExtra("stop")) {
            stopSelf(startId);
        } else if (intent != null && intent.hasExtra("start")) {
            file_tag = intent.getStringExtra("start");

            if(System.currentTimeMillis()>1463505000000L){
                stopSelf(startId);
            }
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
    }

    public void sendData() {

        if (count > 0) {
            double[][] data = new double[count][];
            for(int i=0;i<count;i++)
                data[i] = sampleList[i];

            try {
                byte[] bytes = serialize(data);
                WearDataTransfer dt = new WearDataTransfer(getApplicationContext());
                dt.connect();
                dt.sendAsset("/wearasset", file_tag, Asset.createFromBytes(bytes));
                dt.disconnect();
            }catch(Exception ex){
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
