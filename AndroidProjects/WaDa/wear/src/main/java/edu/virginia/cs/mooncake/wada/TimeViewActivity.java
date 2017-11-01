package edu.virginia.cs.mooncake.wada;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class TimeViewActivity extends Activity {

    private TextView tvTimeType, tvTime;
    boolean clockTime, showFlag;
    Button btnStartStop;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_view);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                tvTimeType = (TextView) stub.findViewById(R.id.tvTimeType);
                tvTime = (TextView) stub.findViewById(R.id.tvTime);
                btnStartStop = (Button) stub.findViewById(R.id.btnStartStop);
                clockTime = true;
                showFlag = true;

                showTimeType();
                handler = new Handler();
                handler.postDelayed(runnable, 100);

            }
        });


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void btnClick(View v) {
        if (v.getId() == R.id.btnToggle) {
            clockTime = !clockTime;
            showTimeType();

        } else if (v.getId() == R.id.btnStartStop) {
            showFlag = false;

            /*if(btnStartStop.getText().equals("Start")){
                showFlag = true;
                btnStartStop.setText("Stop");
                showTime();
            }else{
                showFlag = false;
                btnStartStop.setText("Start");
            }*/

        }
    }

    void showTimeType() {
        if (clockTime)
            tvTimeType.setText("Clock Time");
        else
            tvTimeType.setText("Up time");

    }

    void showTime() {
        long t, t2;
        if (clockTime) {
            t = System.currentTimeMillis();
            tvTime.setText((t / 1000) + "\n" + (t % 1000));
        }else {
            t = System.nanoTime() / 1000000L;
            //t2 = SystemClock.uptimeMillis();
            //tvTime.setText((t / 1000) + "\n" + (t % 1000)+"\n"+(t2 / 1000) + "\n" + (t2 % 1000));
            tvTime.setText((t / 1000) + "\n" + (t % 1000));
        }


    }

    void closeView(){
        this.finish();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            showTime();
            if(showFlag)
                handler.postDelayed(runnable, 100);
            else
                closeView();
        }
    };
}
