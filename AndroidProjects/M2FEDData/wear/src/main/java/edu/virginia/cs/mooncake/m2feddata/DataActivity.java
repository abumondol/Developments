package edu.virginia.cs.mooncake.m2feddata;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;

import edu.virginia.cs.mooncake.m2feddata.myutils.FileUtil;

public class DataActivity extends Activity {

    private TextView tvFileCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                tvFileCount = (TextView) stub.findViewById(R.id.tvFileCount);
                tvFileCount.setText("Files on watch: "+ FileUtil.fileCount());
            }
        });
    }

    public void btnClick(View v){
        switch(v.getId()){
            case R.id.btnYes:
                AlertDialog alert = getAlertDialogBuilder().create();
                alert.show();
                break;

            case R.id.btnNo:
                finishThisActivity();
                break;
        }
    }

    public void finishThisActivity(){
        startActivity(new Intent(this.getApplicationContext(), SettingsActivity.class));
        this.finish();
    }

    public AlertDialog.Builder getAlertDialogBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                FileUtil.deleteAllFiles();
                finishThisActivity();
                dialog.dismiss();
            }

        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishThisActivity();
                dialog.dismiss();
            }
        });

        return builder;

    }
}
