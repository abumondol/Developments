package edu.virginia.cs.mooncake.airsign;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import edu.virginia.cs.mooncake.airsign.utils.DateTimeUtil;
import edu.virginia.cs.mooncake.airsign.utils.FileUtil;
import edu.virginia.cs.mooncake.airsign.utils.ServiceUtil;


public class WatchMainActivity extends Activity {

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    final static String folderName = Environment.getExternalStorageDirectory() + "/WatchData";
    private TextView tvStatus;
    Button btn;
    Handler handler;
    boolean layoutInflated = false;
    String mac;

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
                refreshStatus();
                layoutInflated = true;
            }
        });
        handler = new Handler();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mac = getMac();
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
                String tag = mac + "-" + DateTimeUtil.getDateTimeString(System.currentTimeMillis(), 3).replace(' ','-');

                i.putExtra("start", tag);
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

        } else if (v.getId() == R.id.btnExit) {
            this.finish();
        }


    }

    public void refreshStatus() {
        boolean status = ServiceUtil.isMySensorServiceRunning(this.getApplicationContext(), WatchSensorService.class.getName());

        if (status == true) {
            btn.setText("Stop");
            btn.setBackgroundResource(R.color.red);
        } else {
            btn.setText("Start");
            btn.setBackgroundResource(R.color.green);
        }

        btn.setEnabled(true);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refreshStatus();
        }
    };


    public String getMac() {
        return Build.SERIAL;
    }


}