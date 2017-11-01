package edu.virginia.cs.mondol.fed.config;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.virginia.cs.mondol.fed.utils.FedConstants;
import edu.virginia.cs.mondol.fed.utils.FileUtils;
import edu.virginia.cs.mondol.fed.utils.SharedPrefUtil;

/**
 * Created by Abu on 3/13/2017.
 */

public class FEDConfigWrapper {

    public static FEDConfig getFEDConfig() {
        String configStr = FileUtils.readConfig();
        if (configStr == null)
            return new FEDConfig();

        FEDConfig fc = createNewConfig(configStr);
        if (fc == null)
            fc = new FEDConfig();
        return fc;
    }

    public static FEDConfig createNewConfig(String configStr) {
        FEDConfig fc = new FEDConfig();

        try {
            JSONObject config = new JSONObject(configStr);

            JSONArray jsonArray;
            int i, arrCount;
            fc.home_id = config.getInt("home_id");
            fc.version = config.getInt("version");
            fc.ble_initial_wait_time = config.getInt("ble_initial_wait_time");
            fc.ble_data_save_interval = config.getInt("ble_data_save_interval");
            fc.ble_scan_interval = config.getInt("ble_scan_interval");
            fc.ble_scan_duration = config.getInt("ble_scan_duration");

            /*jsonArray = config.getJSONArray("ble_mac_list");
            arrCount = jsonArray.length();
            fc.ble_mac_list = new String[arrCount];
            for (i = 0; i < arrCount; i++) {
                fc.ble_mac_list[i] = jsonArray.getString(i);
            }*/

        } catch (Exception ex) {
            Log.i(FedConstants.MYTAG, ex.toString());
            return null;
        }

        return fc;

    }


    public static MyNetConfig getNetConfig(Context context) {
        String qoute = "\"";
        String ssid = SharedPrefUtil.getSharedPref(FedConstants.WIFI_SSID, context);
        String password = SharedPrefUtil.getSharedPref(FedConstants.WIFI_PASSWORD, context);
        String server_ip = SharedPrefUtil.getSharedPref(FedConstants.SERVER_IP, context);
        MyNetConfig nc = new MyNetConfig();

        nc.wifi_ssid = qoute + ssid + qoute;
        nc.wifi_password = qoute + password + qoute;
        nc.server_ip = server_ip;

        String s = SharedPrefUtil.getSharedPref(FedConstants.BLE_MAC_INDICES_STRING, context);
        nc.beacon_indices_str = s;
        if(s!=null) {
            String[] sarr = s.split(",");
            nc.beacon_indices = new int[sarr.length];
            for(int i=0;i<sarr.length;i++){
                nc.beacon_indices[i] = Integer.parseInt(sarr[i]);
            }
        }

        return nc;
    }

}
