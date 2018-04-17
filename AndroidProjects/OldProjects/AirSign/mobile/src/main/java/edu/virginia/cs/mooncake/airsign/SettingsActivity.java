package edu.virginia.cs.mooncake.airsign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import edu.virginia.cs.mooncake.airsign.utils.FileUtil;
import edu.virginia.cs.mooncake.airsign.utils.ServiceUtil;
import edu.virginia.cs.mooncake.airsign.utils.SharedPrefUtil;


public class SettingsActivity extends ActionBarActivity {

    Button btnUpload, btnDisable, btnLogout;
    CheckBox cbAutoUpload;
    TextView tvNotification;
    boolean status, isTrained;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        btnDisable = (Button) findViewById(R.id.btnEnableDisable);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        //cbAutoUpload = (CheckBox) findViewById(R.id.cbAutoUpload);
        tvNotification = (TextView) findViewById(R.id.tvNotification);
        showSummary();

    }

    public void btnClick(View v) {
        if (v.getId() == R.id.btnUpload) {
            startService(new Intent(this, IntentServiceNetwork.class).putExtra(IntentServiceNetwork.ACTION, IntentServiceNetwork.UPLOAD));
            btnUpload.setEnabled(false);

        } else if (v.getId() == R.id.btnEnableDisable) {
            status = ServiceUtil.isMySensorServiceRunning(this.getApplicationContext(), ServiceAirSign.class.getName());
            Intent i = new Intent(this.getApplicationContext(), ServiceAirSign.class);
            if (status) {
                i.putExtra("stop", "stop");
                btnDisable.setText("ENABLE AIRSIGN");
            } else {
                i.putExtra("start", "start");
                btnDisable.setText("DISABLE AIRSIGN");
            }
            startService(i);
            startService(new Intent(this, IntentServiceNetwork.class).putExtra(IntentServiceNetwork.ACTION, IntentServiceNetwork.UPLOAD));
            btnDisable.setEnabled(false);

        }else if (v.getId() == R.id.btnLogout) {

            SharedPrefUtil.removeAll(this.getApplicationContext());
            Intent i = new Intent(this.getApplicationContext(),
                    AirSignMainActivity.class);
            startActivity(i);
            this.finish();

        } else if (v.getId() == R.id.btnExit) {
            this.finish();

        }

        /*else if (v.getId() == R.id.cbAutoUpload) {
            boolean isChecked = ((CheckBox) v).isChecked();
            if (isChecked)
                SharedPrefUtil.putSharedPrefBoolean("auto_upload", true, this.getApplicationContext());
            else
                SharedPrefUtil.putSharedPrefBoolean("auto_upload", false, this.getApplicationContext());
        }*/

    }


    public void showSummary() {
        String s = "";

        isTrained = SharedPrefUtil.getSharedPrefBoolean("train", this.getApplicationContext());
        if (isTrained) {
            btnDisable.setEnabled(true);
            status = ServiceUtil.isMySensorServiceRunning(this.getApplicationContext(), ServiceAirSign.class.getName());
            if (status) {
                btnDisable.setText("DISABLE AIRSIGN");
            } else {
                btnDisable.setText("ENABLE AIRSIGN");
            }
            s += "Training: Done";

        } else {
            btnDisable.setEnabled(false);
            s += "Training: Not done yet";
        }

        int fileCount =  FileUtil.getFileCount(this.getApplicationContext());

        s += "\nTraining Signature : " + FileUtil.getTemplateCount(this.getApplicationContext());
        s += "\nSignature to upload : " + fileCount;
        s += "\nTraining DPS Threshold: "+SharedPrefUtil.getSharedPrefInt("tth", this.getApplicationContext());
        s += "\nLast DPS: "+SharedPrefUtil.getSharedPrefInt("th", this.getApplicationContext());

        if (fileCount > 0) {
            btnUpload.setEnabled(true);
            btnLogout.setEnabled(false);
        }
        else {
            btnUpload.setEnabled(false);
            btnLogout.setEnabled(true);
        }

        tvNotification.setText(s);
    }
}
