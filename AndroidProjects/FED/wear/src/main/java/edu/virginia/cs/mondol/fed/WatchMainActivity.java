package edu.virginia.cs.mondol.fed;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.virginia.cs.mondol.fed.utils.FedConstants;
import edu.virginia.cs.mondol.fed.utils.ServiceUtils;
import edu.virginia.cs.mondol.fed.utils.SharedPrefUtil;

public class WatchMainActivity extends Activity {

    private TextView tvStatus;
    private Button btnStartStop;
    int alertType;
    String alertMessage, alertTitle;
    Context context;

    private static final int REQUEST_CODE = 1;
    private static String[] PERMISSIONS_LIST = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
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
                btnStartStop = (Button) stub.findViewById(R.id.btnStartStop);
                refresh();

            }
        });

        context = this.getApplicationContext();
        //Check if storage read-write permission is enabled
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
            verifyPermissions(this);
    }

    public void btnClick(View v) {
        if (v.getId() == R.id.btnStartStop) {
            btnStartStop.setEnabled(false);

            if (btnStartStop.getText().equals(FedConstants.START)) {
                startStopService(true);
            } else {
                startStopService(false);
            }

        } else if (v.getId() == R.id.btnMonitor) {
            startActivity(new Intent(this.getApplicationContext(), MonitorActivity.class));

        } else if (v.getId() == R.id.btnTest) {
            startActivity(new Intent(this.getApplicationContext(), TestActivity.class));

        } else if (v.getId() == R.id.btnSettings) {
            startActivity(new Intent(this.getApplicationContext(), SettingsActivity.class));

        } else if (v.getId() == R.id.btnRefresh) {
            refresh();

        } else if (v.getId() == R.id.btnExit) {
            this.finish();

        }

    }


    private void startStopService(boolean startFlag) {
        boolean bleServiceStatus = ServiceUtils.isMySensorServiceRunning(this.getApplicationContext(), BLEService.class.getName());
        Intent i;
        if (startFlag && !bleServiceStatus) {
            i = new Intent(this, BLEService.class);
            i.putExtra(FedConstants.START, System.currentTimeMillis());
            startService(i);

        } else if (!startFlag && bleServiceStatus) {
            i = new Intent(this, BLEService.class);
            i.putExtra(FedConstants.STOP, System.currentTimeMillis());
            startService(i);
        }

    }

    private void refresh() {
        boolean sensorServiceStatus = ServiceUtils.isMySensorServiceRunning(this.getApplicationContext(), SensorService.class.getName());
        boolean bleServiceStatus = ServiceUtils.isMySensorServiceRunning(this.getApplicationContext(), BLEService.class.getName());
        long bt = SharedPrefUtil.getSharedPrefLong(FedConstants.LAST_TIME_BLE_FOUND, this.getApplicationContext());
        bt = (System.currentTimeMillis() - bt) / 1000;

        String str = "FED: ";
        str += bleServiceStatus ? "B," : "BX,";
        str += sensorServiceStatus ? " S, " : " SX, ";
        str += SharedPrefUtil.getSharedPrefInt(FedConstants.AT_HOME, this.getApplicationContext()) + "; " ;
        str += (bt / 3600) + ":" + ((bt % 3600) / 60) + ":" + bt % 60;

        tvStatus.setText(str);

        if (sensorServiceStatus || bleServiceStatus) {
            btnStartStop.setText(FedConstants.STOP);
            //btnReset.setEnabled(false);
        } else {
            btnStartStop.setText(FedConstants.START);
            //btnReset.setEnabled(true);
        }

        btnStartStop.setEnabled(true);

    }


    public static void verifyPermissions(Activity activity) {
        int permission1 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission1 != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_LIST,
                    REQUEST_CODE
            );
        }

    }

}
