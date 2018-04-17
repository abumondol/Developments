package edu.virginia.cs.mondol.sampleapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class SensorService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope, magnetometer;
    StringBuilder sb;
    int sampleCount;

    final int SAMPLE_COUNT_FOR_SAVE = 500;

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sb = new StringBuilder();
        sampleCount = 0;

        accelerometer = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME); //registers the acceleromter

        gyroscope = sensorManager
                .getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME); //registers the gyroscope

        magnetometer = sensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME); //registers the gyroscope

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("action") && intent.getStringExtra("action").equals("stop")) {
            stopSelf(); //stop this service
        }

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this); //unregister all registered sensor
        saveData(sb.toString()); //save the data from string builder to file
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Each event contain a sensor sample. Sensor samples are temporarily stored in string builder
        sb.append(event.timestamp); //System up time in nano seconds when this sensor value was sampled
        sb.append(",");
        sb.append(event.sensor.getType()); //Type, 1: accelerometer, 4:gyroscope
        sb.append(",");
        sb.append(event.values[0]); // X-axis value
        sb.append(",");
        sb.append(event.values[1]); // Y-axis value
        sb.append(",");
        sb.append(event.values[2]); // Z-axis value
        sb.append("\n");

        sampleCount++;
        if (sampleCount == SAMPLE_COUNT_FOR_SAVE) { //save data stored in the string builder to file periodically
            saveData(sb.toString());
            sampleCount = 0;
            sb.setLength(0); //reset the string builder
        }
    }


    void saveData(String data) {
        String folderName = Environment.getExternalStorageDirectory().toString() + "/Sample App";
        File file = new File(folderName);
        if (file.exists() == false) {
            if (file.mkdirs())
                Log.i("File read"," directory create successful");
            else
                Log.i("File read"," directory create unsuccessful");
        }else{
            Log.i("File read"," directory exist");
        }

        String fileName =  "sensor_data.csv";
        file = new File (folderName, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(data.getBytes());
            fos.flush();
            fos.close();
        } catch (Exception ex) {
            Log.e("File save error", ex.toString());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
