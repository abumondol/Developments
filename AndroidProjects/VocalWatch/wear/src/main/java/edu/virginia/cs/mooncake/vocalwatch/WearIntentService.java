package edu.virginia.cs.mooncake.vocalwatch;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.Asset;

import edu.virginia.cs.mooncake.vocalwatch.utils.MC;


public class WearIntentService extends IntentService {
    public WearIntentService() {
        super("SaveDataIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            if (intent.hasExtra("data")) {
                String data = intent.getStringExtra("data");
                WearDataTransfer dt = new WearDataTransfer(getApplicationContext());
                dt.connect();
                dt.sendData("/weardata", "weardata", data);
                dt.disconnect();
                Log.i(MC.TTS, "Intent service: data sent, size:" + data);
            }
        }
    }
}
