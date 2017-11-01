package edu.virginia.cs.mondol.fed.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

import edu.virginia.cs.mondol.fed.config.FEDConfigWrapper;
import edu.virginia.cs.mondol.fed.config.MyNetConfig;

/**
 * Created by TOSHIBA on 4/22/2016.
 */
public class NetUtils {

    public static int addNetwork(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.i(FedConstants.MYTAG, "Before NetID: " + wifiInfo.getNetworkId() + ", SSID:" + wifiInfo.getSSID());

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        MyNetConfig nc = FEDConfigWrapper.getNetConfig(context);
        Log.i(FedConstants.MYTAG, "Configured ssid: " + nc.wifi_ssid + ", password:" + nc.wifi_password);

        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = nc.wifi_ssid;
        wc.preSharedKey = nc.wifi_password;
        //wc.hiddenSSID = true;
        wc.status = WifiConfiguration.Status.ENABLED;
        //wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        //wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        int netId = wifiManager.addNetwork(wc);
        return netId;
    }

    public static boolean connectToNetwork(Context context, int netId) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.i(FedConstants.MYTAG, "Before NetID: " + wifiInfo.getNetworkId() + ", SSID:" + wifiInfo.getSSID());

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        boolean b1 = wifiManager.disconnect();
        boolean b2 = wifiManager.enableNetwork(netId, true);
        boolean b3 = wifiManager.reconnect();
        Log.i(FedConstants.MYTAG, "connectToNetwork: " + b1 + "," + b2 + "," + b3);

        wifiInfo = wifiManager.getConnectionInfo();
        Log.i(FedConstants.MYTAG, "After NetID: " + netId + ", " + wifiInfo.getNetworkId() + ", SSID:" + wifiInfo.getSSID());
        return b3;
    }

    public static boolean isRouterAvailable(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        String ssid = wifiInfo.getSSID();
        MyNetConfig nc = FEDConfigWrapper.getNetConfig(context);
        if (ssid != null && ssid.equals(nc.wifi_ssid)) {
            Log.i(FedConstants.MYTAG, ssid + " avaialable");
            return true;
        }

        Log.i(FedConstants.MYTAG, ssid + " not avaialable.");
        return false;

    }

    public static boolean enableWiFi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            boolean flag = wifiManager.setWifiEnabled(true);
            Log.i(FedConstants.MYTAG, "Enabling Wifi. Result: " + flag);
            return false;
        }

        return true;
    }

    /*public static void disableWiFi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
            Log.i(FedConstants.MYTAG, "Disabling Wifi");
        }
    }*/

    public static int getIP(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo().getIpAddress();
    }

    /*public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static String cmStatus(Context context) {
        String status = "";
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if (connectivityManager.getActiveNetworkInfo() != null) {
            if (connectivityManager.getActiveNetworkInfo().isConnected())
                status += ", connected";
            else
                status += ", not connected";

        } else
            status += "\n null";

        return status;
    }*/

    public static String netStatus(Context context) {
        String status = "";
        final ConnectivityManager cm = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));

        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            if (networkInfo != null) {
                boolean isWiFi = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
                status += "CM: isWifi-" + isWiFi + ", isConnected-" + networkInfo.isConnected();
            } else {
                status += "CM: not null, NetInfo: null, ";
            }
        } else {
            status += "CM is null, "; //cm is null
        }

        status += getIPAndisEnable(context);
        return status;
    }

    public static String getIPAndisEnable(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int netId = wifiInfo.getNetworkId();
        return "isWifiEnabled: " + wifiManager.isWifiEnabled() + ", IP: " + wifiManager.getConnectionInfo().getIpAddress() + ", NetID: " + netId + ", SSID:" + wifiInfo.getSSID();
    }

    public static void removeAllNetwork(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            wifiManager.removeNetwork(i.networkId);
            wifiManager.saveConfiguration();
        }
    }

    public static int getSavedNetworkCount(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        if (list != null)
            return list.size();
        return 0;
    }

    public static String getSavedNetworkInfo(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        String ssid = SharedPrefUtil.getSharedPref(FedConstants.WIFI_SSID, context);
        String s;

        if (list == null) {
            s = "SSID LIST: NULL";
        } else {
            s = "SSID(" + list.size() + "): " + ssid;
            if (ssid == null) {
                return s;
            }

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).SSID.equals(ssid)) {
                    s += "(" + (i + 1) + ")";
                    break;
                }
            }
        }

        return s;
    }

}
