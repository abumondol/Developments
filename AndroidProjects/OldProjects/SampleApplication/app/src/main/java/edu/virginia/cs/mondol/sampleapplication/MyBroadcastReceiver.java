package edu.virginia.cs.mondol.sampleapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastReceiver extends BroadcastReceiver {
    public MyBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) { // when the device boots, it starts the sensor service
            context.startService(new Intent(context, SensorService.class).putExtra("action", "start"));

        }else  if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) { // when the device is disconnected from power, it starts the sensor service
            context.startService(new Intent(context, SensorService.class).putExtra("action", "stop"));

        }
    }
}
