package edu.virginia.cs.mooncake.airsign;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import edu.virginia.cs.mooncake.airsign.utils.ConstantsUtil;
import edu.virginia.cs.mooncake.airsign.utils.FileUtil;
import edu.virginia.cs.mooncake.airsign.utils.SharedPrefUtil;


public class SignActivity extends ActionBarActivity implements View.OnTouchListener, MyResultReceiver.Receiver {

    Button btn;
    TextView tvTitle;
    String type;
    int count;
    long time;
    MyResultReceiver myResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        Intent i = getIntent();
        if (i.hasExtra("type")) {
            type = getIntent().getStringExtra("type");
        } else
            type = "na";

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        btn = (Button) findViewById(R.id.btnSign);
        btn.setOnTouchListener(this);
        btn.setEnabled(true);

        if (type.equals("login")) {
            tvTitle.setText("AirSign LockScreen");
        } else if (type.equals("train")) {
            tvTitle.setText("AirSign Train");
            FileUtil.deleteTemplateFiles(this.getApplicationContext());
            SharedPrefUtil.putSharedPrefBoolean("train", false, this.getApplicationContext());
            SharedPrefUtil.putSharedPrefInt("tth", 200, this.getApplicationContext());
        } else if (type.equals("practice")) {
            tvTitle.setText("AirSign Practice");
        }

        count = 0;
        time = System.currentTimeMillis();
        myResultReceiver = new MyResultReceiver(new Handler());
        myResultReceiver.setReceiver(this);

    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (type.equals("train")) {
            Log.i("Training Files", FileUtil.getTemplateCount(this.getApplicationContext())+"");
            if (FileUtil.getTemplateCount(this.getApplicationContext()) < 3) {
                FileUtil.copyTemplateFiles(this.getApplicationContext(), false);
                FileUtil.deleteTemplateFiles(this.getApplicationContext());
            }
        }

        startService(new Intent(this, IntentServiceNetwork.class).putExtra(IntentServiceNetwork.ACTION, IntentServiceNetwork.UPLOAD));
    }

    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            Log.i("Button", "Pressed");
            Intent i = new Intent(this, ServiceMobileSensor.class);
            i.putExtra("start", "start");
            i.putExtra("type", type);
            i.putExtra("time", time);
            i.putExtra(ConstantsUtil.INTENT_RESULT_RECEIVER, myResultReceiver);
            this.startService(i);
            Log.i("Button", "Pressed Done");
        } else if (action == MotionEvent.ACTION_UP) {
            Log.i("Button", "Released");
            Intent i = new Intent(this, ServiceMobileSensor.class);
            i.putExtra("stop", "stop");
            this.startService(i);
            Log.i("Button", "Released Done");
            btn.setEnabled(false);
            if(type.equals("login") || type.equals("train"))
                Toast.makeText(this, "Processing... Please wait.", Toast.LENGTH_SHORT).show();

        }
        return false;
    }

    public void btnClick(View v) {
        if (v.getId() == R.id.btnExit) {
            this.finish();
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        //resultCode: 0 - practice; 100 - train; 200 - login failed; 201 - login passed
        Log.i("Result received", "Code: "+resultCode);
        btn.setEnabled(true);

        if (resultCode == 100) {
            count++;
            if (count < 3) {
                Toast.makeText(this, "Signature " + count + " of 3 captured. Please sign again.", Toast.LENGTH_SHORT).show();
            } else {
                Intent i = new Intent(this, ShowResultActivity.class);
                i.putExtra("type", "train");
                startActivity(i);
                this.finish();
            }
        } else if (resultCode == 200) {
//            count++;
//            if (count >= 5) {
//                Intent i = new Intent(this, ShowResultActivity.class);
//                i.putExtra("type", "login");
//                startActivity(i);
//                this.finish();
//            }

            Toast.makeText(this, "Failed. Please try again.", Toast.LENGTH_SHORT).show();

        } else if (resultCode == 201) {
            Toast.makeText(this, "Succesful Signature!", Toast.LENGTH_SHORT).show();
            this.finish();
        }

    }
}
