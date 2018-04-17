package edu.virginia.cs.mooncake.airsign;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class ServiceAirSign extends Service {

    BroadcastReceiver mReceiver;
    String code;
    KeyguardManager.KeyguardLock k1;

    @Override
    public void onCreate() {
        super.onCreate();

        KeyguardManager km =(KeyguardManager)getSystemService(KEYGUARD_SERVICE);
        k1= km.newKeyguardLock("IN");
        k1.disableKeyguard();

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        mReceiver = new ReceiverAirSign();
        registerReceiver(mReceiver, filter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("stop")) {
            stopSelf(startId);
        } else if (intent != null && intent.hasExtra("start")) {
            code = intent.getStringExtra("start");
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        k1.reenableKeyguard();
        unregisterReceiver(mReceiver);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
