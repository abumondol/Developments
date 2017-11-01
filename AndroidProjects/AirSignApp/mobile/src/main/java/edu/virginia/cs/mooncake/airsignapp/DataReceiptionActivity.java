package edu.virginia.cs.mooncake.airsignapp;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

import edu.virginia.cs.mooncake.airsignapp.utils.MyConstants;
import edu.virginia.cs.mooncake.airsignapp.utils.SharedPrefUtil;

public class DataReceiptionActivity extends AppCompatActivity {

    Context context;
    TextView tvInfo, tvCount, tvMessage;
    Button btnDeleteLast, btnDeleteSession;
    String type, user, imposter=null, password;
    long sessionStartTime, lastFileStartTime = 0;
    int deleteType = 0, count = 0;
    String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_receiption);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(MyConstants.BCAST_DATA_RECEIVED));
        context = this.getApplicationContext();

        tvInfo = (TextView) findViewById(R.id.tvInfo);
        tvCount = (TextView) findViewById(R.id.tvCount);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        btnDeleteLast = (Button) findViewById(R.id.btnDeleteLast);
        btnDeleteSession = (Button) findViewById(R.id.btnDeleteSession);

        Intent intent = getIntent();
        type = intent.getStringExtra(MyConstants.TYPE);
        user = intent.getStringExtra(MyConstants.USER);
        password = intent.getStringExtra(MyConstants.PASSWORD);
        if(intent.hasExtra(MyConstants.IMPOSTER))
            imposter = intent.getStringExtra(MyConstants.IMPOSTER);

        sessionStartTime = System.currentTimeMillis();
        String info = "Type: " + type
                + "\nUser: " + user
                + "\nImposter: " + imposter
                + "\nPassword: " + password;
        tvInfo.setText(info);
        refresh();

        String[] str = password.split(" ");
        int passwordIndex = Integer.parseInt(str[0]);

        SharedPrefUtil.putSharedPref(MyConstants.TYPE, type, context);
        SharedPrefUtil.putSharedPref(MyConstants.USER, user, context);
        SharedPrefUtil.putSharedPref(MyConstants.IMPOSTER, imposter, context);
        SharedPrefUtil.putSharedPref(MyConstants.PASSWORD, password, context);
        SharedPrefUtil.putSharedPrefInt(MyConstants.PASSWORD_INDEX, passwordIndex, context);
        SharedPrefUtil.putSharedPrefLong(MyConstants.SESSION_START_TIME, sessionStartTime, context);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPrefUtil.removeAll(context);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    public void btnClick(View v) {
        if (v.getId() == R.id.btnDeleteLast) {
            deleteType = 0;
            dialogMessage("last data?");

        } else if (v.getId() == R.id.btnDeleteSession) {
            deleteType = 1;
            dialogMessage("all data of this session?");

        } else if (v.getId() == R.id.btnExit) {
            this.finish();
        }
    }

    public void deleteData() {
        setMessage("Deleting data ...");

        String t = "" + lastFileStartTime;
        if (deleteType == 1) {
            t = "" + sessionStartTime;
        }

        File folder = new File(MyConstants.PATH_DATA_FOLDER + "/" + user);
        File[] files = folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().contains(t)) {
                files[i].delete();
                count = count - 1;
            }
        }

        lastFileStartTime = 0;
        setMessage("Data deleted.");
        refresh();

    }

    public void refresh() {
        tvCount.setText("Count: " + count);
        if (count == 0) {
            btnDeleteSession.setEnabled(false);
        } else {
            btnDeleteSession.setEnabled(true);
        }

        if (lastFileStartTime == 0) {
            btnDeleteLast.setEnabled(false);
        } else {
            btnDeleteLast.setEnabled(true);
        }
    }

    public void setMessage(String msg) {
        tvMessage.setText("Message: " + msg);
    }

    void dialogMessage(String msg) {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure to delete " + msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                deleteData();
                            }
                        });

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean flag = intent.getBooleanExtra(MyConstants.MESSAGE, false);
            String file_name = intent.getStringExtra(MyConstants.FILE_NAME);
            long startTime = intent.getLongExtra(MyConstants.START_TIME, 0);

            if(flag) {
                count = count + 1;
                lastFileStartTime = startTime;
            }

            setMessage("Data Received. Save flag:"+flag+"\n File names:\n"+file_name);
            refresh();
        }
    };

}
