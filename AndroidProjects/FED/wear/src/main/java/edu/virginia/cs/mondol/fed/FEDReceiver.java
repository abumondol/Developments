package edu.virginia.cs.mondol.fed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import edu.virginia.cs.mondol.fed.utils.FedConstants;

public class FEDReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.i("MyTAG", "BOOT receiver called");
            context.startService(new Intent(context, BLEService.class).putExtra(FedConstants.BOOT, FedConstants.BOOT));

        }else  if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            Log.i("MyTAG", "Power Connected receiver called");
            context.startService(new Intent(context, BLEService.class).putExtra(FedConstants.PCON, FedConstants.PCON));

        }else  if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
            Log.i("MyTAG", "Power Disconnected receiver called");
            context.startService(new Intent(context, BLEService.class).putExtra(FedConstants.DCON, FedConstants.DCON));
        }
    }
}
