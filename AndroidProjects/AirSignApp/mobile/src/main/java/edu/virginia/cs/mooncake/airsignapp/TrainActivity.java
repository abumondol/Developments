package edu.virginia.cs.mooncake.airsignapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import edu.virginia.cs.mooncake.airsignapp.utils.MyConstants;
import edu.virginia.cs.mooncake.airsignapp.utils.ServiceUtil;
import edu.virginia.cs.mooncake.airsignapp.utils.SharedPrefUtil;

public class TrainActivity extends AppCompatActivity {

    String user;
    TextView tvNotification, tvUserName;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        SharedPrefUtil.putSharedPref(MyConstants.TYPE, MyConstants.TRAIN, this.getApplicationContext());
        SharedPrefUtil.putSharedPref(MyConstants.USER, getIntent().getStringExtra(MyConstants.USER), this.getApplicationContext());
        SharedPrefUtil.putSharedPrefLong(MyConstants.TIME, System.currentTimeMillis(), this.getApplicationContext());

        user = getIntent().getStringExtra(MyConstants.USER);
        tvNotification = (TextView) findViewById(R.id.tvNotificationTrain);
        tvUserName = (TextView) findViewById(R.id.tvUserNameTrain);

        tvNotification.setText(" Welcome! Five Signatures are reqiured. Please start providing signatures.");
        tvUserName.setText("User Name: "+user);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(MyConstants.BCAST_TRAIN));
        context = this.getApplicationContext();

    }

    @Override
    protected void onDestroy() {

        SharedPrefUtil.removeAll(this.getApplicationContext());
        if (ServiceUtil.isMySensorServiceRunning(this.getApplicationContext(), TrainService.class.getName())) {
            Intent i = new Intent(this.getApplicationContext(), TrainService.class);
            i.putExtra("stop", "stop");
            startService(i);
        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    public void btnClick(View v) {
        if (v.getId() == R.id.btnCancelTrain) {
            this.finish();
        }

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra(MyConstants.MESSAGE);
            String type = intent.getStringExtra(MyConstants.TYPE);
            //Log.i("receiver", "Got message: " + message);

            if(type.equals(MyConstants.TRAIN))
                tvNotification.setText(message);
            else
                finishAndGoToMessage(message);
        }
    };

    void finishAndGoToMessage(String message){
        startActivity(new Intent(context, MessageActivity.class).putExtra(MyConstants.MESSAGE, message));
        this.finish();
    }
}
