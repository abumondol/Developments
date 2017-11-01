package edu.virginia.cs.mooncake.airsignapp;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;

import edu.virginia.cs.mooncake.airsignapp.utils.MyConstants;
import edu.virginia.cs.mooncake.airsignapp.utils.ServerUtils;


public class NetIntentService extends IntentService {

    public NetIntentService() {
        super("NetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        if(intent.hasExtra(MyConstants.UPLOAD)){
            Intent intent2 = new Intent(MyConstants.BCAST_DATA_UPLOAD);
            if(!ServerUtils.isConnected(getApplicationContext())){
                intent2.putExtra(MyConstants.UPLOAD_RESULT, -1);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent2);
                return;
            }

            int uploadCount = 0;
            File usersFolder = new File(MyConstants.PATH_DATA_FOLDER);
            if (usersFolder.exists()) {
                int uploadCode;
                File[] folders = usersFolder.listFiles();
                for (int i = 0; i < folders.length; i++) {
                    File[] files = folders[i].listFiles();
                    for (int j = 0; j < files.length; j++) {

                        Log.i(MyConstants.MYTAG, "Uploadling file (" + (j + 1) + "/" + files.length + "): " + files[j].getName());
                        uploadCode = ServerUtils.uploadFile(files[j], MyConstants.SERVER_URL_UPLOAD_FILE);
                        if (uploadCode > 0) {
                            files[j].delete();
                            uploadCount++;
                        }
                        Log.i(MyConstants.MYTAG, "Upload Code: " + uploadCode);

                    }

                }

            }

            intent2.putExtra(MyConstants.UPLOAD_RESULT, uploadCount);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent2);
        }
    }
}
