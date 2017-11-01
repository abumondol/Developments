package edu.virginia.cs.mooncake.airsignapp;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Set;

import edu.virginia.cs.mooncake.airsignapp.utils.FileUtil;
import edu.virginia.cs.mooncake.airsignapp.utils.MyConstants;
import edu.virginia.cs.mooncake.airsignapp.utils.SharedPrefUtil;

public class MobileDataLayerListenerService extends WearableListenerService {

    private static final String TAG = "MyTAG";
    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";

    byte[] assetToByteArray(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }

        MobileDataTransfer dt = new MobileDataTransfer(getApplicationContext());

        if (dt.connect() == false) {
            return null;
        }

        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                dt.getGoogleApiClient(), asset).await().getInputStream();
        dt.disconnect();

        if (assetInputStream == null) {
            Log.w(TAG, "Requested an unknown Asset.");
            return null;
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        try {

            while ((nRead = assetInputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        }

        return buffer.toByteArray();

    }

    public void sendDeleteMessage(String fileName) {
        Intent i = new Intent(this.getApplicationContext(), MobileIntentService.class);
        i.putExtra("type", "action");
        i.putExtra("action", "delete," + fileName);
        startService(i);


        //Toast.makeText(this.getApplicationContext(), "File Received: "+fileName, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        //Log.d(TAG, "onDataChanged: dataEvents count -" + dataEvents.getCount());


        for (DataEvent event : dataEvents) {
            //Log.i(TAG, "DataItemPath: " + event.getDataItem().getUri().getPath() + " " + event.getType());

            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.i(TAG, "DataItem deleted: " + event.getDataItem().getUri().getPath());

            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.i(TAG, " DataItem changed : " + event.getDataItem().getUri().getPath());


                if (event.getDataItem().getUri().getPath().startsWith("/wearasset")) {
                    Log.i(TAG, "Asset Recieved");
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                    String type = SharedPrefUtil.getSharedPref(MyConstants.TYPE, this.getApplicationContext());
                    String user = SharedPrefUtil.getSharedPref(MyConstants.USER, this.getApplicationContext());
                    String imposter = SharedPrefUtil.getSharedPref(MyConstants.IMPOSTER, this.getApplicationContext());
                    int passwordIndex = SharedPrefUtil.getSharedPrefInt(MyConstants.PASSWORD_INDEX, this.getApplicationContext());
                    long sessionStartTime = SharedPrefUtil.getSharedPrefLong(MyConstants.SESSION_START_TIME, this.getApplicationContext());

                    if (type == null || user == null) {
                        //Toast.makeText(this.getApplicationContext(), "Data received. But Data Receiption is not open. So deleted.", Toast.LENGTH_SHORT).show();

                        if(v!=null)
                            v.vibrate(500);

                    } else {
                        Log.i("type-user", type+"-"+user);
                        DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                        Set<String> keyList = dataMapItem.getDataMap().keySet();
                        Asset asset;
                        byte[] byteData;
                        for (String key : keyList) {
                            String[] str = key.split("-");
                            long startTime = Long.parseLong(str[1]);
                            long endTime = Long.parseLong(str[2]);
                            if(startTime < sessionStartTime) {
                                //Toast.makeText(this.getApplicationContext(), "Data Discarded for time issue.", Toast.LENGTH_SHORT).show();
                                if(v!=null)
                                    v.vibrate(500);
                                continue;
                            }

                            asset = dataMapItem.getDataMap().getAsset(key);
                            byteData = assetToByteArray(asset);

                            String fileName = "password-" +type+"-"+user+"-"+imposter+"-"+passwordIndex
                                    +"-"+sessionStartTime+"-"+startTime+"-"+endTime+"-"+str[0];

                            Boolean flag = FileUtil.saveByteArrayToFile(MyConstants.PATH_DATA_FOLDER+"/"+user, fileName, byteData);

                            Intent i = new Intent(MyConstants.BCAST_DATA_RECEIVED);
                            i.putExtra(MyConstants.FILE_NAME, key+"\n"+fileName);
                            i.putExtra(MyConstants.MESSAGE, flag);
                            i.putExtra(MyConstants.START_TIME, startTime);
                            LocalBroadcastManager.getInstance(this).sendBroadcast(i);

                            //Toast.makeText(this.getApplicationContext(), "Data received. Save Result: "+flag, Toast.LENGTH_SHORT).show();
                            if(v!=null)
                                v.vibrate(1000);

                            /*if(type.equals(MyConstants.TRAIN)){
                                Log.i("Starting", "Train Service");
                                Intent i = new Intent(this.getApplicationContext(), TrainService.class);
                                i.putExtra(MyConstants.TRAIN, MyConstants.TRAIN);
                                i.putExtra(MyConstants.USER, user);
                                i.putExtra(MyConstants.FILE_NAME, time+"-"+key);
                                i.putExtra(MyConstants.BYTE_DATA,byteData);
                                startService(i);
                            }else{
                                if(index<keyList.size())
                                    continue;

                                if (!ServiceUtil.isMySensorServiceRunning(this.getApplicationContext(), TestService.class.getName())) {
                                    Toast.makeText(this.getApplicationContext(), "Data received. But no test service running. So discarded ", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Intent i = new Intent(this.getApplicationContext(), TestService.class);
                                i.putExtra(MyConstants.TEST, MyConstants.TEST);
                                i.putExtra(MyConstants.TYPE, type);
                                i.putExtra(MyConstants.USER, user);
                                i.putExtra(MyConstants.FILE_NAME, time+"-"+key);
                                i.putExtra(MyConstants.BYTE_DATA,byteData);
                                startService(i);

                            }*/

                            Log.i(TAG, "Asset Length: " + byteData.length);
                        }

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
