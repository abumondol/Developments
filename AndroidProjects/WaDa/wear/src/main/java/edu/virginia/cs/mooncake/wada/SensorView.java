package edu.virginia.cs.mooncake.wada;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import edu.virginia.cs.mooncake.wada.utils.ConstantsUtil;
import edu.virginia.cs.mooncake.wada.utils.SharedPrefUtil;

public class SensorView extends Activity implements SensorEventListener {

    TextView tvTitle, tv1;
    private SensorManager mSensorManager;
    private Sensor mRotationVector, mMagnetoMeter, mAccelerometer, mGyroscope, mGravity, mLinAccel;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private float[][] rotationMatrix;
    float[] orientation, magneticField;

    long displayUpateInterval = 500, lastTime = 0, currentTime;
    int sensorType=0;
    float theta1, theta2, theta3;
    int quatAccuracy = -1, orientationAccuracy = -1, magnetometerAccuracy = -1;
    String msg;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_view);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tv1 = (TextView) findViewById(R.id.tv1);


        rotationMatrix = new float[3][3];
        orientation = new float[3];
        magneticField = new float[3];

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakeLockTag");
        wakeLock.acquire();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 1.0f;
        getWindow().setAttributes(lp);

        context = this.getApplicationContext();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager
                .getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mGravity = mSensorManager
                .getDefaultSensor(Sensor.TYPE_GRAVITY);
        mLinAccel = mSensorManager
                .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mMagnetoMeter = mSensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mRotationVector = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);



        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mLinAccel, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mRotationVector, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagnetoMeter, SensorManager.SENSOR_DELAY_UI);

        SharedPrefUtil.removeEntry(ConstantsUtil.ACCURACY_ACL, this.getApplicationContext());
        SharedPrefUtil.removeEntry(ConstantsUtil.ACCURACY_GYRO, this.getApplicationContext());
        SharedPrefUtil.removeEntry(ConstantsUtil.ACCURACY_MAGNET, this.getApplicationContext());
        SharedPrefUtil.removeEntry(ConstantsUtil.ACCURACY_QUAT, this.getApplicationContext());

    }

    @Override
    protected void onDestroy() {
        mSensorManager.unregisterListener(this);
        wakeLock.release();
        //SharedPrefUtil.removeEntry(ConstantsUtil.ACCURACY_ACL, this.getApplicationContext());
        //SharedPrefUtil.removeEntry(ConstantsUtil.ACCURACY_GYRO, this.getApplicationContext());
        //SharedPrefUtil.removeEntry(ConstantsUtil.ACCURACY_MAGNET, this.getApplicationContext());
        //SharedPrefUtil.removeEntry(ConstantsUtil.ACCURACY_QUAT, this.getApplicationContext());
        super.onDestroy();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sensorType = event.sensor.getType();
        if(sensorType==Sensor.TYPE_ROTATION_VECTOR) {
            quat2data(event.values[3], event.values[0], event.values[1], event.values[2]);
            /*msg =  event.timestamp + ", " + sensorType + ", "+event.accuracy+"\n"
                    + "Rx: "+ rotationMatrix[0][0] + ", " + rotationMatrix[0][1] + ", " + rotationMatrix[0][2]
                    + "\nRy: "+ rotationMatrix[1][0] + ", " + rotationMatrix[1][1] + ", " + rotationMatrix[1][2]
                    + "\nRz: "+ rotationMatrix[2][0] + ", " + rotationMatrix[2][1] + ", " + rotationMatrix[2][2];
            Log.i("Sensor data", msg);*/

        }else{
            /*msg =  event.timestamp + ", " + sensorType + ", "+event.accuracy+", "
                    + String.format("%f, %f, %f", event.values[0], event.values[1], event.values[2]);
            Log.i("Sensor data", msg);*/

            if(sensorType == Sensor.TYPE_ORIENTATION){
                orientation[0] = event.values[0];
                orientation[1] = event.values[1];
                orientation[2] = event.values[2];
            }else if(sensorType == Sensor.TYPE_MAGNETIC_FIELD){
                magneticField[0] = event.values[0];
                magneticField[1] = event.values[1];
                magneticField[2] = event.values[2];
            }

        }

        currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= displayUpateInterval) {
            lastTime = currentTime;
            showData();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if(sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
            quatAccuracy = accuracy;
        else if(sensor.getType() == Sensor.TYPE_ORIENTATION)
            orientationAccuracy = accuracy;
        else if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            magnetometerAccuracy = accuracy;

        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            SharedPrefUtil.putSharedPrefInt(ConstantsUtil.ACCURACY_ACL, accuracy, this.getApplicationContext());
        else if(sensor.getType() == Sensor.TYPE_GYROSCOPE)
            SharedPrefUtil.putSharedPrefInt(ConstantsUtil.ACCURACY_GYRO, accuracy, this.getApplicationContext());
        else if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            SharedPrefUtil.putSharedPrefInt(ConstantsUtil.ACCURACY_MAGNET, accuracy, this.getApplicationContext());
        else if(sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
            SharedPrefUtil.putSharedPrefInt(ConstantsUtil.ACCURACY_QUAT, accuracy, this.getApplicationContext());

        Log.i("Accuracy change","****************************** "+ sensor.getType()+", "+accuracy+" **********************************");
    }

    public void showData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                msg = "";
                //msg += String.format("Rx: %.2f, %.2f, %.2f\n", rotationMatrix[0][0], rotationMatrix[0][1], rotationMatrix[0][2] );
                //msg += String.format("Ry: %.2f, %.2f, %.2f\n", rotationMatrix[1][0], rotationMatrix[1][1], rotationMatrix[1][2] );
                //msg += String.format("Rz: %.2f, %.2f, %.2f\n", rotationMatrix[2][0], rotationMatrix[2][1], rotationMatrix[2][2] );


                theta1 = (float)Math.toDegrees(Math.atan2(rotationMatrix[0][0], rotationMatrix[1][0]));
                theta2 = (float)Math.toDegrees(Math.atan2(rotationMatrix[0][1], rotationMatrix[1][1]));
                theta3 = (float)Math.toDegrees(Math.atan2(rotationMatrix[0][2], rotationMatrix[1][2]));
                //msg += String.format("T: %.0f, %.0f, %.0f, %d\n", theta1, theta2, theta3, quatAccuracy );

                //msg += String.format("O: %.1f, %.1f, %.1f, %d\n", orientation[0], orientation[1], orientation[2], orientationAccuracy );
                msg += String.format("M: %.0f, %.0f, %.0f, %d\n", magneticField[0], magneticField[1], magneticField[2], magnetometerAccuracy );

                String accu = "Accu: " + SharedPrefUtil.getSharedPrefInt(ConstantsUtil.ACCURACY_ACL,context)
                        + ", " + SharedPrefUtil.getSharedPrefInt(ConstantsUtil.ACCURACY_GYRO, context)
                        + ", " + SharedPrefUtil.getSharedPrefInt(ConstantsUtil.ACCURACY_MAGNET, context)
                        + ", " + SharedPrefUtil.getSharedPrefInt(ConstantsUtil.ACCURACY_QUAT, context);

                msg = accu +"\n"+ msg;
                tv1.setText(msg);
            }
        });
    }

    private void quat2data(float q0, float q1, float q2, float q3){
        rotationMatrix[0][0] = q0*q0+q1*q1-q2*q2-q3*q3;
        rotationMatrix[0][1] = 2*(q1*q2 - q0*q3);
        rotationMatrix[0][2] = 2*(q1*q3 + q0*q2);
        rotationMatrix[1][0] = 2*(q0*q3 + q1*q2);
        rotationMatrix[1][1] = q0*q0-q1*q1+q2*q2-q3*q3;
        rotationMatrix[1][2] = 2*(q2*q3 - q0*q1);
        rotationMatrix[2][0] = 2*(q1*q3 - q0*q2);
        rotationMatrix[2][1] = 2*(q2*q3+q0*q1);
        rotationMatrix[2][2] = q0*q0-q1*q1-q2*q2+q3*q3;

    }
}
