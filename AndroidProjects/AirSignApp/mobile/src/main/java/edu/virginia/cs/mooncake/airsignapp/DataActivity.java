package edu.virginia.cs.mooncake.airsignapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.virginia.cs.mooncake.airsignapp.utils.FileUtil;
import edu.virginia.cs.mooncake.airsignapp.utils.MyConstants;

public class DataActivity extends AppCompatActivity {

    TextView tvDataStat, tvUploadMessage;
    Button btnUpload, btnExit;
    boolean uploadCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(MyConstants.BCAST_DATA_UPLOAD));

        tvDataStat = (TextView) findViewById(R.id.tvDataStat);
        tvUploadMessage = (TextView) findViewById(R.id.tvUploadMessage);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        btnExit = (Button) findViewById(R.id.btnExit);
        refresh();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    public void btnClick(View v) {
        if (v.getId() == R.id.btnUpload) {
            tvUploadMessage.setText("Upload Message: Uploading data ...");
            uploadCalled = true;
            startService(new Intent(this.getApplicationContext(), NetIntentService.class).putExtra(MyConstants.UPLOAD, 1));
            refresh();

        } else if (v.getId() == R.id.btnExit) {
            this.finish();
        }

    }

    void refresh() {
        setDataStat();
        if (uploadCalled) {
            btnUpload.setEnabled(false);
            btnExit.setEnabled(false);

        } else {
            btnUpload.setEnabled(true);
            btnExit.setEnabled(true);
        }

    }

    void setDataStat() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String s = FileUtil.getDataStat();
                if (s.length() == 0)
                    tvDataStat.setText("Data Stat:: There is no data for upload");
                else
                    tvDataStat.setText("Data Stat:: " + s);

            }
        });
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int uploadCount = intent.getIntExtra(MyConstants.UPLOAD_RESULT, -2);

            if(uploadCount<=-2)
                tvUploadMessage.setText("Upload Message: intent problem");
            else if(uploadCount==-1)
                tvUploadMessage.setText("Upload Message: server connection failed");
            else{
                tvUploadMessage.setText("Upload Message: Uploaded file count "+uploadCount);
            }

            uploadCalled = false;
            refresh();
        }
    };

}
