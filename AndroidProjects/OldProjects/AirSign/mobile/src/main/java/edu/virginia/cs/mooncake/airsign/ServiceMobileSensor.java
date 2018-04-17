package edu.virginia.cs.mooncake.airsign;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.os.Vibrator;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;

import edu.virginia.cs.mooncake.airsign.myclasses.SensorSample;
import edu.virginia.cs.mooncake.airsign.utils.ConstantsUtil;
import edu.virginia.cs.mooncake.airsign.utils.DistanceMeasure;
import edu.virginia.cs.mooncake.airsign.utils.FileUtil;
import edu.virginia.cs.mooncake.airsign.utils.SharedPrefUtil;

public class ServiceMobileSensor extends Service implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometerLinear, mAccelerometer, mGyroscope, mMagnetometer, mRotation;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    ArrayList<SensorSample> sampleList;
    Context context;
    SensorSample sample;
    ResultReceiver myResultReceiver;

    int rate;
    long time, startTime;
    String type = "";
    Vibrator v;

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
        mAccelerometerLinear = mSensorManager
                .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGyroscope = mSensorManager
                .getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mMagnetometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mRotation = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        rate = SensorManager.SENSOR_DELAY_GAME;

        mSensorManager.registerListener(this, mAccelerometerLinear, rate);
        mSensorManager.registerListener(this, mAccelerometer, rate);
        mSensorManager.registerListener(this, mGyroscope, rate);
        mSensorManager.registerListener(this, mMagnetometer, rate);
        mSensorManager.registerListener(this, mRotation, rate);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        sampleList = new ArrayList<SensorSample>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this.getApplicationContext();

        if (intent != null && intent.hasExtra("stop")) {
            stopSelf(startId);
        } else if (intent != null && intent.hasExtra("start")) {
            type = intent.getStringExtra("type");
            time = intent.getLongExtra("time", 0);
            myResultReceiver = intent.getParcelableExtra(ConstantsUtil.INTENT_RESULT_RECEIVER);
            startTime = System.currentTimeMillis();

            if (v != null)
                v.vibrate(500);
        }

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        wakeLock.release();
        if (v != null)
            v.vibrate(500);

        int user_id = SharedPrefUtil.getSharedPrefInt("user_id", this.getApplicationContext());
        int tth = SharedPrefUtil.getSharedPrefInt("tth", this.getApplicationContext());
        //int tth = 200;

        if (type.equals("practice")) {
            String file_name = user_id + "-p_" + time + "_" + startTime;
            FileUtil.saveSampleListToFileAsText(context, sampleList, file_name, false);
            myResultReceiver.send(0, null);

        } else if (type.equals("train")) {
            String file_name = user_id + "-t_" + time + "_" + startTime;
            //FileUtil.saveSampleListToFileAsText(context, sampleList, file_name, true);
            FileUtil.serializeTemplate(context, sampleList, file_name);
            myResultReceiver.send(100, null);

        } else if (type.equals("login")) {
            String file_name = user_id + "-s_" + time + "_" + startTime;
            int d = signDistance(sampleList);
            if (d < tth) {
                myResultReceiver.send(201, null);
            } else {
                file_name += "_f";
                myResultReceiver.send(200, null);
            }

            SharedPrefUtil.putSharedPrefInt("th", d, this.getApplicationContext());
            FileUtil.saveSampleListToFileAsText(context, sampleList, file_name, false);
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sample = new SensorSample();
        sample.sensorType = event.sensor.getType();
        sample.timeStamp = event.timestamp;
        sample.values = event.values.clone();
        sample.accuracy = event.accuracy;
        sampleList.add(sample);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * ***************************************
     */
    public int signDistance(ArrayList<SensorSample> l) {
        ArrayList<ArrayList<SensorSample>> templates = FileUtil.getTemplateSigns(this.getApplicationContext());

        float d = 0;
        for (int i = 0; templates != null && i < templates.size(); i++) {
            d += DistanceMeasure.DTW_dps(templates.get(i), l);
        }

        return (int) d/3;
    }

}
