package edu.virginia.cs.mooncake.vocalwatch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class DataTransferService extends Service {

    WearDataTransfer dt;

    @Override
    public void onCreate() {
        super.onCreate();
        dt = new WearDataTransfer(this.getApplicationContext());
        dt.connect();
        Log.i("Data Transfer Service", "Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("stop")) {
            stopSelf();

        } else if (intent.hasExtra("data")) {
            String data = intent.getStringExtra("data");
            Log.i("Data Transfer Service", "data received: " + data);
            dt.sendData("/weardata", "weardata", data);
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("Data Transfer Service", "Destroyed");
        dt.disconnect();
        super.onDestroy();

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
