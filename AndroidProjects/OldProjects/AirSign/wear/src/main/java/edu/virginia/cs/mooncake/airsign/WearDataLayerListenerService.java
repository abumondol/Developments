package edu.virginia.cs.mooncake.airsign;


import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

public class WearDataLayerListenerService extends WearableListenerService {
    private static final String TAG = "MyTAG";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        for (DataEvent event : dataEvents) {

            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.d(TAG, "DataItem deleted watch: " + event.getDataItem().getUri().getPath());

            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.d(TAG, "DataItem changed watch: " + event.getDataItem().getUri().getPath());

                if (event.getDataItem().getUri().getPath().startsWith("/mobiledata")) {
                    WearDataTransfer dt = new WearDataTransfer(getApplicationContext());
                    dt.connect();
                    Wearable.DataApi.deleteDataItems(dt.getGoogleApiClient(), event.getDataItem().getUri());
                    dt.disconnect();
                }

            }
        }

    }

}
