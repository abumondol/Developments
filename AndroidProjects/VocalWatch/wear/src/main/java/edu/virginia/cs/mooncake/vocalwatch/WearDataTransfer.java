package edu.virginia.cs.mooncake.vocalwatch;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by Md.AbuSayeed on 8/4/2014.
 */
public class WearDataTransfer {

    GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MyTAG";
    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";


    public WearDataTransfer(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        // Now you can use the Data Layer API
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public boolean connect() {
        ConnectionResult connectionResult =
                mGoogleApiClient.blockingConnect();

        if (!connectionResult.isSuccess()) {
            Log.e(TAG, "Failed to connect to GoogleApiClient.");
            return false;
        }

        return true;
    }

    public void disconnect() {
        mGoogleApiClient.disconnect();
    }

    public boolean isConnected() {
        return mGoogleApiClient.isConnected();
    }

    public void sendData(String path, String key, String data) {
        //Log.i(TAG, "Send data in WearDataTransfer. Path: " + path + "; Key:" + key + "; Data: " + data);
        if (!isConnected() && connect() == false) {
            Log.e(TAG, "Cannot send data, connection to googleAPIClient failed");
            return;
        }

        PutDataMapRequest dataMapRequest = PutDataMapRequest.create(path);
        dataMapRequest.getDataMap().putString(key, data);
        PutDataRequest dataRequest = dataMapRequest.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                .putDataItem(mGoogleApiClient, dataRequest);


        /*pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                Log.i(TAG, "Pending result Callback, " + dataItemResult.getDataItem().getUri().getPath());
            }
        });*/

        Log.i(TAG, "End of sendData in WearDataTransfer");
    }

    public void sendAsset(String path, String key, Asset asset) {
        if (!isConnected() && connect() == false) {
            Log.e(TAG, "Cannot send data, connection to googleAPIClient failed");
            return;
        }

        PutDataMapRequest dataMapRequest = PutDataMapRequest.create(path);
        dataMapRequest.getDataMap().putAsset(key, asset);
        PutDataRequest dataRequest = dataMapRequest.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                .putDataItem(mGoogleApiClient, dataRequest);

        /*pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                Log.i(TAG, "Pending result callback, " + dataItemResult.getDataItem().getUri().getPath());
            }
        });*/
        Log.i(TAG, "End of sendData in WearDataTransfer");
    }


}
