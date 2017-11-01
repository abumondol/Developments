package edu.virginia.cs.mondol.fed;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import edu.virginia.cs.mondol.fed.utils.DateTimeUtils;
import edu.virginia.cs.mondol.fed.utils.FedConstants;
import edu.virginia.cs.mondol.fed.utils.FileUtils;
import edu.virginia.cs.mondol.fed.utils.SharedPrefUtil;

public class DataActivity extends Activity {

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private TextView tvStatus, tvStatus2;
    private Button btnDelete, btnDelete2;
    int deleteType = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakeLockTag");
        wakeLock.acquire();

        setContentView(R.layout.activity_data);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                tvStatus = (TextView) stub.findViewById(R.id.statusData);
                btnDelete = (Button) stub.findViewById(R.id.btnDeleteFiles);
                tvStatus2 = (TextView) stub.findViewById(R.id.statusData2);

                refresh();
            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onDestroy() {
        wakeLock.release();
        super.onDestroy();
    }

    public void btnClick(View v) {
        if (v.getId() == R.id.btnDeleteFiles) {
            deleteType = 1;
            AlertDialog alert = getAlertDialogBuilder().create();
            alert.show();

        } else if (v.getId() == R.id.btnRefreshData) {
            refresh();
        }
    }


    private void refresh() {
        int fileCount = FileUtils.fileCount();
        tvStatus.setText("File Count: " + fileCount);
        if (fileCount == 0)
            btnDelete.setEnabled(false);
        else
            btnDelete.setEnabled(true);

        int bc = SharedPrefUtil.getSharedPrefInt(FedConstants.BITE_COUNT, this.getApplicationContext());
        long t = SharedPrefUtil.getSharedPrefLong(FedConstants.LAST_UPLOAD_ATTEMPT_TIME, this.getApplicationContext());
        String res = SharedPrefUtil.getSharedPref(FedConstants.LAST_UPLOAD_ATTEMPT_RESULT, this.getApplicationContext());
        long led = SharedPrefUtil.getSharedPrefLong(FedConstants.LAST_EPISODE_DURATION, this.getApplicationContext());
        long inid = SharedPrefUtil.getSharedPrefLong(FedConstants.DISCARD_DURATION, this.getApplicationContext());
        long bt = SharedPrefUtil.getSharedPrefLong(FedConstants.LAST_BITE_TIME, this.getApplicationContext());

        String s = "Alerm Time: " + SharedPrefUtil.getSharedPref(FedConstants.BATTERY_ALARM_TIME, this.getApplicationContext());
        s += ", " + DateTimeUtils.getDateTimeString(SharedPrefUtil.getSharedPrefLong(FedConstants.LAST_ALARM_TIME, this.getApplicationContext()) );
        s += "\nEpisode(" + (inid / 1000) + "):" + led + "\nBite: " + bc + ",  " + DateTimeUtils.getDateTimeString(t) + "\nUpTry: ";
        if (t > 0)
            s += DateTimeUtils.getDateTimeString(t);//(System.currentTimeMillis()-t)/1000;
        else
            s += "-1";

        s += ", " + res;

        tvStatus2.setText(s);


    }


    public AlertDialog.Builder getAlertDialogBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure to delete all files?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (deleteType == 1)
                    FileUtils.deleteAllFiles();
                else
                    FileUtils.deleteAllFiles2();

                refresh();
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
