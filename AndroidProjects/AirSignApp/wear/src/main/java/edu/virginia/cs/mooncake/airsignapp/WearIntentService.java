package edu.virginia.cs.mooncake.airsignapp;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.Asset;


public class WearIntentService extends IntentService {
    private static final String TAG = "MyTAG";

    public WearIntentService() {
        super("SaveDataIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            if (intent.getStringExtra("action").equals("send")) {

                String file_tag = intent.getStringExtra("file_tag");
                byte[] bytes = (byte[]) intent.getSerializableExtra("data");

                WearDataTransfer dt = new WearDataTransfer(getApplicationContext());
                dt.connect();
                dt.sendAsset("/wearasset", file_tag, Asset.createFromBytes(bytes));
                dt.disconnect();

                Log.i(TAG, "Intent service: File sent, size:" + bytes.length);
            }
        }
    }
}
