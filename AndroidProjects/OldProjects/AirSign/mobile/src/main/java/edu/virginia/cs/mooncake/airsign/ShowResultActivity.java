package edu.virginia.cs.mooncake.airsign;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import edu.virginia.cs.mooncake.airsign.myclasses.SensorSample;
import edu.virginia.cs.mooncake.airsign.utils.DistanceMeasure;
import edu.virginia.cs.mooncake.airsign.utils.FileUtil;
import edu.virginia.cs.mooncake.airsign.utils.SharedPrefUtil;


public class ShowResultActivity extends ActionBarActivity {

    Button btnTry, btnHome;
    TextView notification;
    String type;
    int action;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);
        btnTry = (Button) findViewById(R.id.btnTry);
        btnHome = (Button) findViewById(R.id.btnHome);
        notification = (TextView) findViewById(R.id.tvNotification);
        btnTry.setVisibility(View.GONE);
        btnHome.setVisibility(View.GONE);
        context = this.getApplicationContext();


        Intent intent = getIntent();
        if (intent != null) {
            type = intent.getStringExtra("type");
            if (type.equals("train")) {
                notification.setText("Processing... Please wait.");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean success = checkTemplates();
                        if (success) {
                            notification.setText("Successful training. Airsign is activated.");
                            SharedPrefUtil.putSharedPrefBoolean("train", true, context);
                            btnHome.setVisibility(View.VISIBLE);
                            FileUtil.copyTemplateFiles(context, true);
                            startAirSign();

                        } else {
                            btnTry.setVisibility(View.VISIBLE);
                            btnHome.setVisibility(View.VISIBLE);
                            SharedPrefUtil.putSharedPrefBoolean("train", false, context);
                            notification.setText("Training failed. Please practice more or try again.");
                            FileUtil.copyTemplateFiles(context, false);
                            FileUtil.deleteTemplateFiles(context);
                        }

                    }
                });

            } else if (type.equals("login")) {
                notification.setText("Login by signature failed. Press exit to login to your phone.");

            }

        }
    }

    public boolean checkTemplates() {

        int tth = SharedPrefUtil.getSharedPrefInt("tth", this.getApplicationContext());
        Log.i("tth",""+tth);
        ArrayList<ArrayList<SensorSample>> templateList = FileUtil.getTemplateSigns(this.getApplicationContext());

        ArrayList<SensorSample> s1 = templateList.get(0);
        ArrayList<SensorSample> s2 = templateList.get(1);
        ArrayList<SensorSample> s3 = templateList.get(2);

        Log.i("lengths of signatures", s1.size() + " , " + s2.size() + " , " + s3.size());
        int th = (int) (DistanceMeasure.DTW_dps(s1, s2) + DistanceMeasure.DTW_dps(s1, s3) + DistanceMeasure.DTW_dps(s2, s3)) / 3;

        Log.i("tth of train signatures", th+"");

        if (th <= tth) {
            SharedPrefUtil.putSharedPrefInt("th", th, this.getApplicationContext());
            return true;
        }
        return false;
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    public void btnClick(View v) {
        if (v.getId() == R.id.btnTry) {
            startActivity(new Intent(this, SignActivity.class).putExtra("type", "train"));
        } else if (v.getId() == R.id.btnHome) {
            startActivity(new Intent(this, AirSignMainActivity.class));
        }

    }

    public void startAirSign(){
        Intent i = new Intent(this.getApplicationContext(), ServiceAirSign.class);
        i.putExtra("start", "start");
        startService(i);
    }
}
