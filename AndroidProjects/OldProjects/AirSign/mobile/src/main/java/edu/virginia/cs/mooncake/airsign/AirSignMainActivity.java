package edu.virginia.cs.mooncake.airsign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import edu.virginia.cs.mooncake.airsign.utils.ConstantsUtil;
import edu.virginia.cs.mooncake.airsign.utils.SharedPrefUtil;


public class AirSignMainActivity extends ActionBarActivity {

    Button trainBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_sign_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String config_json = SharedPrefUtil.getSharedPref(ConstantsUtil.SPK_CONFIG,
                this.getApplicationContext());
        if (config_json == null) {
            Intent i = new Intent(this.getApplicationContext(),
                    LoginActivity.class);
            startActivity(i);
            this.finish();
        }

        trainBtn = (Button) findViewById(R.id.btnTrain);
        //if (SharedPrefUtil.getSharedPrefBoolean("train", this.getApplicationContext()) == true)
        //    trainBtn.setEnabled(false);
    }

    public void btnClick(View v) {
        Intent i;
        if (v.getId() == R.id.btnPractice) {
            i = new Intent(this.getApplicationContext(), SignActivity.class);
            i.putExtra("type", "practice");
            startActivity(i);

        } else if (v.getId() == R.id.btnTrain) {
            i = new Intent(this.getApplicationContext(), SignActivity.class);
            i.putExtra("type", "train");
            startActivity(i);

        } else if (v.getId() == R.id.btnStatistics){
            i = new Intent(this.getApplicationContext(), HelpActivity.class);
            startActivity(i);

        } else if (v.getId() == R.id.btnSettings) {
            i = new Intent(this.getApplicationContext(), SettingsActivity.class);
            startActivity(i);

        } else if (v.getId() == R.id.btnExit) {
            this.finish();
        }

    }

}
