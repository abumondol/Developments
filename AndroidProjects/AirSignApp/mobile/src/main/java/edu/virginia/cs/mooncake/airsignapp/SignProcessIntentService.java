package edu.virginia.cs.mooncake.airsignapp;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class SignProcessIntentService extends IntentService {

    public SignProcessIntentService() {
        super("SignProcessIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            int sign_count = intent.getIntExtra("sign_count", 0);
            int index = intent.getIntExtra("index", 0);
            String type = intent.getStringExtra("type");
            String user = intent.getStringExtra("user");
            String fileName = intent.getStringExtra("fileName");

            try {
                byte[] byteData = (byte[]) intent.getSerializableExtra("byteData");
                if (type.equals("train")) {


                } else if (type.equals("test")) {

                } else if (type.equals("false")) {

                } else {
                    return;
                }


                Intent i = new Intent("wearasset");
                intent.putExtra("message", sign_count);
                LocalBroadcastManager.getInstance(this).sendBroadcast(i);
            } catch (Exception ex) {
                Log.i("Inside Intent Service", ex.toString());
            }

        }
    }

}
