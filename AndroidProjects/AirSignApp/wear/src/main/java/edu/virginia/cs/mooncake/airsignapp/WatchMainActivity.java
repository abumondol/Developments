package edu.virginia.cs.mooncake.airsignapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import edu.virginia.cs.mooncake.airsignapp.utils.ServiceUtil;


public class WatchMainActivity extends Activity {

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    final static String folderName = Environment.getExternalStorageDirectory() + "/WatchData";
    private TextView tvStatus;
    Button btn, btnCancel;
    Handler handler;
    boolean layoutInflated = false;
    String serial;

    private static final int REQUEST_CODE = 1;
    private static String[] PERMISSIONS_LIST = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BODY_SENSORS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                tvStatus = (TextView) stub.findViewById(R.id.tvStatus);
                btn = (Button) stub.findViewById(R.id.btnStartStop);
                btnCancel = (Button) stub.findViewById(R.id.btnCancel);
                refreshStatus();
                layoutInflated = true;
            }
        });
        handler = new Handler();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        serial = Build.SERIAL;

        //Check if storage read-write permission is enabled
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
            verifyPermissions(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (layoutInflated)
            refreshStatus();

    }


    public void btnClick(View v) {
        if (v.getId() == R.id.btnStartStop) {
            boolean status = ServiceUtil.isMySensorServiceRunning(this.getApplicationContext(), WatchSensorService.class.getName());
            if (btn.getText().equals("Start") && status == false) {
                Intent i = new Intent(this, WatchSensorService.class);
                i.putExtra("start", "x");
                startService(i);
                btn.setBackgroundResource(R.color.white);
                btn.setEnabled(false);
                handler.postDelayed(runnable, 2000);
            } else if (btn.getText().equals("Stop") && status == true) {
                Intent i = new Intent(this, WatchSensorService.class);
                i.putExtra("stop", "" + System.currentTimeMillis());
                startService(i);
                btn.setEnabled(false);
                btn.setText("Stopped... Press Refresh");
                btn.setBackgroundResource(R.color.grey);
            }
        } else if (v.getId() == R.id.btnRefresh) {
            refreshStatus();

        } else if (v.getId() == R.id.btnCancel) {
            Intent i = new Intent(this, WatchSensorService.class);
            i.putExtra("cancel", "" + System.currentTimeMillis());
            startService(i);
            btn.setEnabled(false);
            btn.setText("Cancelled... Press Refresh");
            btn.setBackgroundResource(R.color.grey);
            btnCancel.setEnabled(false);

        }


    }

    public void refreshStatus() {
        boolean status = ServiceUtil.isMySensorServiceRunning(this.getApplicationContext(), WatchSensorService.class.getName());

        if (status == true) {
            btn.setText("Stop");
            btn.setBackgroundResource(R.color.red);
            btnCancel.setEnabled(true);
        } else {
            btn.setText("Start");
            btn.setBackgroundResource(R.color.green);
            btnCancel.setEnabled(false);
        }

        btn.setEnabled(true);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refreshStatus();
        }
    };


    public static void verifyPermissions(Activity activity) {
        int permission1 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int permission3 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.BODY_SENSORS);
        if (permission1 != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED || permission3 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_LIST,
                    REQUEST_CODE
            );
        }
    }

}