package edu.virginia.cs.mooncake.airsign;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverAirSign extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) || intent.getAction().equals(Intent.ACTION_SCREEN_ON) || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent intent2 = new Intent(context, SignActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent2.putExtra("type","login");
            context.startActivity(intent2);

        }
    }

}
