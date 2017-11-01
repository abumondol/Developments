package edu.virginia.cs.mondol.medwatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;

import edu.virginia.cs.mondol.medwatch.utils.FedConstants;
import edu.virginia.cs.mondol.medwatch.utils.FileUtils;
import edu.virginia.cs.mondol.medwatch.utils.SharedPrefUtil;

public class TestActivity extends Activity {

    private TextView mTextView;
    int alertType;
    String alertMessage, alertTitle;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.statusNet);
            }
        });

        context = this.getApplicationContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void btnClick(View v) {
        if (v.getId() == R.id.btnTestConfig) {
            startActivity(new Intent(this.getApplicationContext(), MonitorOutputActivity.class).putExtra(FedConstants.MONITOR, FedConstants.MONITOR_TYPE_NET_CONFIG));
        } else if (v.getId() == R.id.btnTestBeacon) {
            startActivity(new Intent(this.getApplicationContext(), BLETestActivity.class));
        } else if (v.getId() == R.id.btnTestNet) {
            startActivity(new Intent(this.getApplicationContext(), NetTestActivity.class));
        } else if (v.getId() == R.id.btnTestServerSync) {
            startActivity(new Intent(this.getApplicationContext(), MonitorOutputActivity.class).putExtra(FedConstants.MONITOR, FedConstants.MONITOR_TYPE_SERVER_SYNC));
        } else if(v.getId()==R.id.btnTestReset){
            alertTitle = "RESET";
            alertMessage = "Are you sure to reset app?";
            alertType = 1;
            AlertDialog alert = getAlertDialogBuilder().create();
            alert.show();

        }else if (v.getId() == R.id.btnTestExit) {
            this.finish();
        }
    }

    public AlertDialog.Builder getAlertDialogBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(alertTitle);
        builder.setMessage(alertMessage);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (alertType == 1){
                    SharedPrefUtil.removeAll(context);
                    FileUtils.deleteAllFiles();
                }

                dialog.dismiss();
            }

        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        return builder;

    }
}
