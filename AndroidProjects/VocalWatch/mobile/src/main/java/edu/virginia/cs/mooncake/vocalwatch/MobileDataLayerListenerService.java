package edu.virginia.cs.mooncake.vocalwatch;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Set;


public class MobileDataLayerListenerService extends WearableListenerService {

    private static final String TAG = "MyTAG";
    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";

    void broadcastMsg(String data) {
        Log.i("Broadcasting", "data: " + data);
        Intent i = new Intent("vocal_data");
        i.putExtra("data", data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);

    }


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        for (DataEvent event : dataEvents) {
            //Log.i(TAG, "DataItemPath: " + event.getDataItem().getUri().getPath() + " " + event.getType());

            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.i(TAG, "DataItem deleted: " + event.getDataItem().getUri().getPath());

            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.i(TAG, " DataItem changed : " + event.getDataItem().getUri().getPath());


                if (event.getDataItem().getUri().getPath().startsWith("/weardata")) {
                    Log.i(TAG, "Data Recieved");

                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    Set<String> keyList = dataMapItem.getDataMap().keySet();

                    String d;
                    for (String key : keyList) {
                        d = dataMapItem.getDataMap().getString(key);
                        broadcastMsg(d);
                    }
                }

                MobileDataTransfer dt = new MobileDataTransfer(getApplicationContext());
                dt.connect();
                Wearable.DataApi.deleteDataItems(dt.getGoogleApiClient(), event.getDataItem().getUri());
                dt.disconnect();

            }
        }


    }

}
