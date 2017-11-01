package edu.virginia.cs.mooncake.wada;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import edu.virginia.cs.mooncake.wada.utils.ConstantsUtil;
import edu.virginia.cs.mooncake.wada.utils.SharedPrefUtil;

public class SensorSelectActivity extends Activity {

    private TextView mTextView;
    CheckBox acl, gyro, grav, quat, lacc, step_detect, step_count, ble, wifi, gps, hr, light, audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_select);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.tvTimeType);
                acl = (CheckBox) stub.findViewById(R.id.checkBoxAcl);
                gyro = (CheckBox) stub.findViewById(R.id.checkBoxGyro);
                grav = (CheckBox) stub.findViewById(R.id.checkBoxGrav);
                quat = (CheckBox) stub.findViewById(R.id.checkBoxQuat);
                lacc = (CheckBox) stub.findViewById(R.id.checkBoxLacc);
                step_detect = (CheckBox) stub.findViewById(R.id.checkBoxStepDetect);
                step_count = (CheckBox) stub.findViewById(R.id.checkBoxStepCount);
                ble = (CheckBox) stub.findViewById(R.id.checkBoxBLE);
                wifi = (CheckBox) stub.findViewById(R.id.checkBoxWiFi);
                gps = (CheckBox) stub.findViewById(R.id.checkBoxGPS);
                hr = (CheckBox) stub.findViewById(R.id.checkBoxHR);
                light = (CheckBox) stub.findViewById(R.id.checkBoxLight);
                audio = (CheckBox) stub.findViewById(R.id.checkBoxAudio);
            }
        });
    }

    public void btnClick(View v) {
        if (v.getId() == R.id.btnSensorSelectOK) {
            Context context = this.getApplicationContext();
            SharedPrefUtil.putSharedPrefBoolean(ConstantsUtil.SENSOR_ACL, acl.isChecked(), context);
            SharedPrefUtil.putSharedPrefBoolean(ConstantsUtil.SENSOR_GYRO, gyro.isChecked(), context);
            SharedPrefUtil.putSharedPrefBoolean(ConstantsUtil.SENSOR_GRAV, grav.isChecked(), context);
            SharedPrefUtil.putSharedPrefBoolean(ConstantsUtil.SENSOR_QUAT, quat.isChecked(), context);
            SharedPrefUtil.putSharedPrefBoolean(ConstantsUtil.SENSOR_LACC, lacc.isChecked(), context);
            SharedPrefUtil.putSharedPrefBoolean(ConstantsUtil.SENSOR_STEP_DETECT, step_detect.isChecked(), context);
            SharedPrefUtil.putSharedPrefBoolean(ConstantsUtil.SENSOR_STEP_COUNT, step_count.isChecked(), context);
            SharedPrefUtil.putSharedPrefBoolean(ConstantsUtil.SENSOR_BLE, ble.isChecked(), context);
            SharedPrefUtil.putSharedPrefBoolean(ConstantsUtil.SENSOR_WIFI, wifi.isChecked(), context);
            SharedPrefUtil.putSharedPrefBoolean(ConstantsUtil.SENSOR_GPS, gps.isChecked(), context);
            SharedPrefUtil.putSharedPrefBoolean(ConstantsUtil.SENSOR_HR, hr.isChecked(), context);
            SharedPrefUtil.putSharedPrefBoolean(ConstantsUtil.SENSOR_LIGHT, light.isChecked(), context);
            SharedPrefUtil.putSharedPrefBoolean(ConstantsUtil.SENSOR_AUDIO, audio.isChecked(), context);

        }
    }

}
