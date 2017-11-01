package edu.virginia.cs.mooncake.airsignapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import edu.virginia.cs.mooncake.airsignapp.utils.UtilFunctions;


public class MobileIntentService extends IntentService {
    public MobileIntentService() {
        super("MobileIntentService");
    }

    String TAG = "MyTAG";

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            //Log.i(TAG, "mobile intent started");
            //Log.i(TAG, "Request Action from mobile outside if"+ intent.getStringExtra("action"));
            if (intent.hasExtra("type") && intent.getStringExtra("type").equals("action")) {
                Log.i(TAG, "Request Action from mobile: " + intent.getStringExtra("action"));
                MobileDataTransfer dt = new MobileDataTransfer(getApplicationContext());
                dt.connect();
                dt.sendData("/mobiledata", "mobiledata_"+System.currentTimeMillis(), intent.getStringExtra("action"));
                dt.disconnect();
            }
        }
    }
}
