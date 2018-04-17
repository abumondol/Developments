package edu.virginia.cs.mooncake.airsign;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import edu.virginia.cs.mooncake.airsign.utils.ConstantsUtil;
import edu.virginia.cs.mooncake.airsign.utils.SharedPrefUtil;


public class LoginActivity extends Activity implements MyResultReceiver.Receiver {

    MyResultReceiver mResultReceiver;
    TextView tvNotification;
    EditText phone, pin;
    Context context;
    Button btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tvNotification = (TextView) findViewById(R.id.tvNotification);
        phone = (EditText) findViewById(R.id.etPhoneNo);
        pin = (EditText) findViewById(R.id.etPin);
        context = this.getApplicationContext();
        btnLogin = (Button) findViewById(R.id.btnLogin);
        mResultReceiver = new MyResultReceiver(new Handler());
        mResultReceiver.setReceiver(this);
    }

    public void btnClick(View v) {
        Intent i;
        if (v.getId() == R.id.btnLogin) {
            if (phone.getText().length() < 1 || pin.getText().length() < 1) {
                tvNotification.setText("Please enter both phone no and pin");
                return;
            }

            btnLogin.setEnabled(false);
            tvNotification.setText("Please wait ...");

            i = new Intent(this, IntentServiceNetwork.class);
            i.putExtra(IntentServiceNetwork.ACTION, IntentServiceNetwork.LOGIN);
            i.putExtra(ConstantsUtil.INTENT_EMAIL, phone.getText().toString());
            i.putExtra(ConstantsUtil.INTENT_PASSWORD, pin.getText().toString());
            i.putExtra(ConstantsUtil.INTENT_RESULT_RECEIVER, mResultReceiver);
            startService(i);

        } else if (v.getId() == R.id.btnExit) {
            this.finish();
        }

    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode == 0) {
            tvNotification.setText("Wrong email or password");
            btnLogin.setEnabled(true);

        } else if (resultCode < 0) {
            tvNotification.setText("Connection error or exception: "+resultCode);
            btnLogin.setEnabled(true);

        } else {
            tvNotification.setText("Login ok");
            Log.i("onResultReceive", "Login ok");
            try {
                String jsonStr = resultData
                        .getString(ConstantsUtil.BUNDLE_CONFIG);
                JSONObject json = new JSONObject(jsonStr);

                SharedPrefUtil.putSharedPref(ConstantsUtil.SPK_CONFIG,
                        json.toString(), this.getApplicationContext());

                SharedPrefUtil.putSharedPrefInt("user_id",
                        json.getInt("user_id"), this.getApplicationContext());

                SharedPrefUtil.putSharedPrefBoolean("train",
                        json.getBoolean("train"), this.getApplicationContext());

                SharedPrefUtil.putSharedPrefInt("tth",
                        json.getInt("tth"), this.getApplicationContext());


                Intent i = new Intent(this.getApplicationContext(),
                        AirSignMainActivity.class);
                startActivity(i);
                this.finish();
            } catch (Exception ex) {
                Log.i("onResultReceive", ex.toString());
            }
        }
    }


}
