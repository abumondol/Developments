package edu.virginia.cs.mondol.fed;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import edu.virginia.cs.mondol.fed.config.FEDConfig;
import edu.virginia.cs.mondol.fed.config.FEDConfigWrapper;
import edu.virginia.cs.mondol.fed.utils.FEDUtils;
import edu.virginia.cs.mondol.fed.utils.FedConstants;
import edu.virginia.cs.mondol.fed.utils.FileUtils;
import edu.virginia.cs.mondol.fed.utils.NetUtils;
import edu.virginia.cs.mondol.fed.utils.ServerUtils;
import edu.virginia.cs.mondol.fed.utils.SharedPrefUtil;


public class NetService extends Service {
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    Context context;

    int netEnableTryCount = 0, routerTryCount = 0, ipTryCount = 0, serverTryCount = 0, download_type = 1;
    //int net_max_net_enable_try_count = 2, net_max_router_try_count = 2, net_max_ip_try_count = 3, net_max_server_try_count = 3;
    boolean netEnableSuccess, addConnectSuccess, isUploadWorking = false, isDownloadWorking = false, isAddConnectNetworkWorking = false;
    boolean downloadConfig = false, downloadTime = false;

    Handler handler;
    FEDConfig fc;
    File[] fileList;
    int fileCount;
    
    @Override
    public void onCreate() {
        super.onCreate();
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakeLockTag");
        wakeLock.acquire();

        fc = FEDConfigWrapper.getFEDConfig();
        context = this.getApplicationContext();
        handler = new Handler();
        handler.postDelayed(netRunnable, fc.net_interval_initial_runnable);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int atHomeStatus = SharedPrefUtil.getSharedPrefInt(FedConstants.AT_HOME, context);
        if (intent != null && intent.hasExtra("stop") || atHomeStatus<0) {
            stopSelf(startId);
        } else if (intent != null && intent.hasExtra(FedConstants.DOWNLOAD_TIME)) {
            downloadTime = true;
        } else if (intent != null && intent.hasExtra(FedConstants.DOWNLOAD_CONFIG)) {
            downloadConfig = true;
        }

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(netRunnable);
        wakeLock.release();
        super.onDestroy();

    }

    public class DownloadDataThread extends Thread {

        public void run() {
            isDownloadWorking = true;
            SharedPrefUtil.putSharedPrefBoolean(FedConstants.DOWNLOAD_WORKING, true, context);

            String data = null, message;
            Log.i(FedConstants.MYTAG, "inside download data in NetService");
            if (downloadTime) {
                download_type = 1;
                message = "time_sync";
            } else {
                download_type = 2;
                message = fc.version + "";
            }

            for (int i = 0; i < fc.net_max_server_try_count && data == null; i++) {
                data = ServerUtils.getData(context, FedConstants.DOWNLOAD_TYPE_TIME, message);
                Log.i(FedConstants.MYTAG, "Data Download called: " + data);

                if (data != null) {
                    if (data.startsWith("time")) {
                        FEDUtils.saveSyncTimeInfo(context, data);
                        Log.i(FedConstants.MYTAG, "Time Downloaded successfully successfully");
                        if (SharedPrefUtil.getSharedPrefLong(FedConstants.TIME_SYNC_RESPONSE_PERIOD, context) > fc.time_sync_min_response_time)
                            continue;
                        downloadTime = false;
                    } else {
                        if (FEDConfigWrapper.createNewConfig(data) != null) {
                            FileUtils.saveConfig(data);
                            Log.i(FedConstants.MYTAG, "Config Saved successfully");
                        }

                        downloadConfig = false;
                    }
                    break;
                }
            }

            isDownloadWorking = false;
            SharedPrefUtil.putSharedPrefBoolean(FedConstants.DOWNLOAD_WORKING, false, context);
        }

    }


    public class UploadDataThread extends Thread {

        public void run() {
            isUploadWorking = true;
            boolean isConnected = false;
            Log.i(FedConstants.MYTAG, "inside upload data in NetService");

            for (int i = 0; i < fc.net_max_server_try_count && !isConnected; i++) {
                isConnected = ServerUtils.isConnected(context);
                Log.i(FedConstants.MYTAG, "isServerConnected: " + isConnected);
            }

            if (isConnected) {
                Log.i(FedConstants.MYTAG, "Upload started");
                ServerUtils.uploadFiles(context, fileList);
                Log.i(FedConstants.MYTAG, "Upload Finished");
            }

            isUploadWorking = false;
        }
    }

    public class AddConnectNetworkThread extends Thread {

        public void run() {
            isAddConnectNetworkWorking = true;
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            Log.i(FedConstants.MYTAG, "Calling addConnectNetwork, saved ssid count:" + list.size());
            int netId;
            if (list.size() == 0)
                netId = NetUtils.addNetwork(context);
            else
                netId = list.get(0).networkId;

            NetUtils.connectToNetwork(context, netId);
            isAddConnectNetworkWorking = false;
        }
    }

    private void stopThisService() {
        this.stopSelf();
    }

    private Runnable netRunnable = new Runnable() {
        @Override
        public void run() {

            if (isUploadWorking || isDownloadWorking || isAddConnectNetworkWorking) {
                handler.postDelayed(netRunnable, fc.net_interval_while_upload_download_working);
                return;
            }


            fileList = FileUtils.getFileList();
            fileCount = fileList.length;
            Log.i(FedConstants.MYTAG, "File count: " + fileCount);

            Arrays.sort(fileList);
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].getName().equals(SharedPrefUtil.getSharedPref(FedConstants.RUNNING_BLE_FILENAME, context)) || fileList[i].getName().equals(SharedPrefUtil.getSharedPref(FedConstants.RUNNING_SENSOR_FILENAME, context))) {
                    Log.i(FedConstants.MYTAG, "Running file, not uploading. File(" + (i + 1) + "/" + fileList.length + "): " + fileList[i].getName());
                    fileList[i] = null;
                    fileCount--;
                }
            }

            if (fileCount <= 0 && !downloadConfig && !downloadTime) {
                stopThisService();
                return;
            }

            netEnableSuccess = NetUtils.enableWiFi(context);
            if (!netEnableSuccess) {
                netEnableTryCount++;
                if (netEnableTryCount == fc.net_max_net_enable_try_count) {
                    stopThisService();
                } else
                    handler.postDelayed(netRunnable, fc.net_interval_between_net_enable_try);

                return;
            }


            if (!NetUtils.isRouterAvailable(context)) {
                routerTryCount++;
                Log.i(FedConstants.MYTAG, "Router try Count failed: " + routerTryCount + ", " + NetUtils.netStatus(context) + "\n");

                if (routerTryCount <= fc.net_max_router_try_count) {
                    new AddConnectNetworkThread().start();
                    handler.postDelayed(netRunnable, fc.net_interval_between_router_try);
                } else {
                    stopThisService();
                }

                return;
            }

            int ip = NetUtils.getIP(context);
            Log.i(FedConstants.MYTAG, "IP: " + ip);

            if (ip == 0) {
                ipTryCount++;
                Log.i(FedConstants.MYTAG, "IP try Count: " + ipTryCount + ", " + NetUtils.netStatus(context));
                if (ipTryCount >= fc.net_max_ip_try_count) {
                    stopThisService();

                } else {
                    new AddConnectNetworkThread().start();
                    handler.postDelayed(netRunnable, fc.net_interval_between_ip_try);
                }
                return;
            }

            if (downloadConfig || downloadTime)
                new DownloadDataThread().start();
            else
                new UploadDataThread().start();

            handler.postDelayed(netRunnable, fc.net_interval_between_runnables);
        }

    };


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
