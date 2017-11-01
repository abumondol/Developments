package edu.virginia.cs.mooncake.wada;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import edu.virginia.cs.mooncake.wada.utils.ConstantsUtil;
import edu.virginia.cs.mooncake.wada.utils.DateTimeUtil;
import edu.virginia.cs.mooncake.wada.utils.ServiceUtil;
import edu.virginia.cs.mooncake.wada.utils.SharedPrefUtil;
import edu.virginia.cs.mooncake.wada.utils.WadaUtils;

public class WatchMainActivity extends Activity {

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    final static String folderName = Environment.getExternalStorageDirectory() + "/WatchData";
    private TextView tvStatus, tvAccuracy;
    Button btn, btnSensorView;
    Handler handler;
    boolean layoutInflated = false;
    String serial = Build.SERIAL;


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
                tvAccuracy = (TextView) stub.findViewById(R.id.tvAccuracy);
                btn = (Button) stub.findViewById(R.id.btnStartStop);
                btnSensorView = (Button) stub.findViewById(R.id.btnSensorView);
                refreshStatus();
                layoutInflated = true;
            }
        });

        handler = new Handler();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Check if storage read-write permission is enabled
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
            verifyPermissions(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("edu.virginia.cs.mooncake.wada.sensor_start"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (layoutInflated)
            refreshStatus();

    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("msg");
            Log.i("receiver", "Got message: " + message);
            refreshStatus();
        }
    };

    public void btnClick(View v) {
        if (v.getId() == R.id.btnStartStop) {
            boolean status = ServiceUtil.isMySensorServiceRunning(this.getApplicationContext(), WatchSensorService.class.getName());
            if (btn.getText().equals("Start") && status == false) {

                String tag = serial + "-" + WadaUtils.getWatchName(this.getApplicationContext()) + "-" + WadaUtils.getTag(this.getApplicationContext()) + "-" + DateTimeUtil.getDateTimeString(System.currentTimeMillis(), 3).replace(' ', '-');

                Intent i = new Intent(this, WatchSensorService.class);
                i.putExtra("start", "sensor-" + tag);
                startService(i);

                i = new Intent(this, BeaconService.class);
                i.putExtra("start", "beacon-" + tag);
                startService(i);

                btn.setBackgroundResource(R.color.white);
                btn.setEnabled(false);
                handler.postDelayed(runnable, 2000);

            } else if (btn.getText().equals("Stop") && status == true) {
                Intent i = new Intent(this, WatchSensorService.class);
                i.putExtra("stop", "" + System.currentTimeMillis());
                startService(i);

                i = new Intent(this, BeaconService.class);
                i.putExtra("stop", "" + System.currentTimeMillis());
                startService(i);

                btn.setEnabled(false);
                btn.setText("Stopped... Press Refresh");
                btn.setBackgroundResource(R.color.grey);
            }
        } else if (v.getId() == R.id.btnRefresh) {
            refreshStatus();

        } else if (v.getId() == R.id.btnData) {
            startActivity(new Intent(this, DataActivity.class));
        } else if (v.getId() == R.id.btnTag) {
            startActivity(new Intent(this, TagActivity.class));
        } else if (v.getId() == R.id.btnSensorView) {
            //startActivity(new Intent(this, SensorView.class));
            startActivity(new Intent(this, BeaconViewActivity.class));
        }


    }

    public void refreshStatus() {
        boolean status = ServiceUtil.isMySensorServiceRunning(this.getApplicationContext(), WatchSensorService.class.getName());
        boolean status2 = ServiceUtil.isMySensorServiceRunning(this.getApplicationContext(), BeaconService.class.getName());

        if (status == true) {
            btn.setText("Stop");
            btn.setBackgroundResource(R.color.red);
            btnSensorView.setEnabled(false);
        } else {
            btn.setText("Start");
            btn.setBackgroundResource(R.color.green);
            btnSensorView.setEnabled(true);
        }

        tvStatus.setText(WadaUtils.getTag(this.getApplicationContext())+": "+status + ", " + status2);
        String accu = "Accu: " + SharedPrefUtil.getSharedPrefInt(ConstantsUtil.ACCURACY_ACL, this.getApplicationContext())
                + ", " + SharedPrefUtil.getSharedPrefInt(ConstantsUtil.ACCURACY_GYRO, this.getApplicationContext())
                + ", " + SharedPrefUtil.getSharedPrefInt(ConstantsUtil.ACCURACY_MAGNET, this.getApplicationContext())
                + ", " + SharedPrefUtil.getSharedPrefInt(ConstantsUtil.ACCURACY_QUAT, this.getApplicationContext());
        tvAccuracy.setText(accu);
        btn.setEnabled(true);
        //tvStatus.setText(status + ", " + status2);

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