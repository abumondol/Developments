package edu.virginia.cs.mooncake.vocalwatch;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by Md.AbuSayeed on 8/4/2014.
 */
public class MobileDataTransfer {

    GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MyTAG";
    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";

    public MobileDataTransfer(Context context){
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    public GoogleApiClient getGoogleApiClient(){
        return mGoogleApiClient;
    }

    public boolean connect(){
        ConnectionResult connectionResult =
                mGoogleApiClient.blockingConnect();

        if (!connectionResult.isSuccess()) {
            Log.e(TAG, "Failed to connect to GoogleApiClient.");
            return false;
        }

        return true;
    }

    public void disconnect(){
        mGoogleApiClient.disconnect();
    }

    public boolean isConnected(){
        return mGoogleApiClient.isConnected();
    }

    public void sendData(String path, String tag, String data){
        if(!isConnected() && connect()==false){
            Log.e(TAG, "Cannot send data, connection to googleAPIClient failed");
            return;
        }

        PutDataMapRequest dataMapRequest = PutDataMapRequest.create(path);
        dataMapRequest.getDataMap().putString(tag, data);
        PutDataRequest dataRequest = dataMapRequest.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                .putDataItem(mGoogleApiClient, dataRequest);
    }

    public void sendAsset(String path, String key, Asset asset){
        if(!isConnected() && connect()==false){
            Log.e(TAG, "Cannot send data, connection to googleAPIClient failed");
            return;
        }

        PutDataMapRequest dataMapRequest = PutDataMapRequest.create(path);
        dataMapRequest.getDataMap().putAsset(key, asset);
        PutDataRequest dataRequest = dataMapRequest.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                .putDataItem(mGoogleApiClient, dataRequest);
    }
}
