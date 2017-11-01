package edu.virginia.cs.mondol.fed;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.virginia.cs.mondol.fed.config.FEDConfigWrapper;
import edu.virginia.cs.mondol.fed.config.MyNetConfig;
import edu.virginia.cs.mondol.fed.utils.FEDUtils;
import edu.virginia.cs.mondol.fed.utils.FedConstants;
import edu.virginia.cs.mondol.fed.utils.FileUtils;
import edu.virginia.cs.mondol.fed.utils.NetUtils;
import edu.virginia.cs.mondol.fed.utils.ServerUtils;
import edu.virginia.cs.mondol.fed.utils.SharedPrefUtil;

public class MonitorOutputActivity extends Activity {

    private TextView tvOutput;
    int type = 0;
    Context context;
    String outputStr;
    Button btnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_output);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        context = this.getApplicationContext();
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                tvOutput = (TextView) stub.findViewById(R.id.tvMonitorOutput);
                btnRefresh = (Button) stub.findViewById(R.id.btnMonitorOutputRefresh);
                type = getIntent().getIntExtra(FedConstants.MONITOR, 0);
                if (type == FedConstants.MONITOR_TYPE_SERVER_SYNC) {
                    if (SharedPrefUtil.getSharedPrefLong(FedConstants.TIME_SYNC_WATCH, context) >= System.currentTimeMillis())
                        SharedPrefUtil.putSharedPrefLong(FedConstants.TIME_SYNC_WATCH, 0, context);

                    if (SharedPrefUtil.getSharedPrefLong(FedConstants.TIME_SYNC_WATCH, context) == 0)
                        tvOutput.setText("Sync info not available.");
                    else
                        tvOutput.setText("*** Existing sync info ***\n" + FEDUtils.getTimeSyncInfo(context));
                }
                refresh();
            }
        });
    }


    public void btnClick(View v) {
        if (v.getId() == R.id.btnMonitorOutputRefresh) {
            if (type == FedConstants.MONITOR_TYPE_SERVER_SYNC) {
                tvOutput.setText("Trying to sync ...");
                btnRefresh.setEnabled(false);
                outputStr = "*** Sync Results *** \n";
                new MonitorOutputActivity.MySyncThread().start();
            } else
                refresh();
        } else if (v.getId() == R.id.btnMonitorOutputExit) {
            this.finish();
        }

    }


    void refresh() {
        if (type == 0) {
            tvOutput.setText("Monitor Output: No intent data");

        } else if (type == FedConstants.MONITOR_TYPE_NET_CONFIG) {
            showNetConfigData();

        } else if (type == FedConstants.MONITOR_TYPE_SERVER_SYNC) {
            btnRefresh.setText("SYNC");
        }


    }

    void showNetConfigData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnRefresh.setEnabled(false);
                outputStr = "Net and Beacon Info\n";
                MyNetConfig nc = FileUtils.readNetConfig();
                if (nc.file_read_message != null) {
                    tvOutput.setText(nc.file_read_message);
                    SharedPrefUtil.removeEntry(FedConstants.WIFI_SSID, context);
                    SharedPrefUtil.removeEntry(FedConstants.WIFI_PASSWORD, context);
                    SharedPrefUtil.removeEntry(FedConstants.SERVER_IP, context);

                } else {
                    SharedPrefUtil.putSharedPref(FedConstants.WIFI_SSID, nc.wifi_ssid, context);
                    SharedPrefUtil.putSharedPref(FedConstants.WIFI_PASSWORD, nc.wifi_password, context);
                    SharedPrefUtil.putSharedPref(FedConstants.SERVER_IP, nc.server_ip, context);

                    String s = "";
                    for (int i = 0; i < nc.beacon_indices.length; i++) {
                        if (i == nc.beacon_indices.length - 1)
                            s += nc.beacon_indices[i];
                        else
                            s += nc.beacon_indices[i] + ",";
                    }

                    SharedPrefUtil.putSharedPref(FedConstants.BLE_MAC_INDICES_STRING, s, context);
                    nc = FEDConfigWrapper.getNetConfig(context);
                    String str = "SSID: " + nc.wifi_ssid + "\nPASSWORD: " + nc.wifi_password + "\nIP: " + nc.server_ip + "\nBeacons: " + nc.beacon_indices_str;

                    int use_code = SharedPrefUtil.getSharedPrefInt(FedConstants.PATTERN_USE, context);
                    if (use_code > 0) {
                        int pattern_version = 0, pattern_size = 0;
                        try {
                            float[][][][] patterns = FileUtils.getPatterns();
                            if (patterns != null) {
                                pattern_version = (int) patterns[0][0][0][0];
                                pattern_size = patterns.length - 1;
                                SharedPrefUtil.putSharedPrefInt(FedConstants.PATTERN_VERSION, pattern_version, context);
                                SharedPrefUtil.putSharedPrefInt(FedConstants.PATTERN_SIZE, pattern_size, context);
                            } else {
                                pattern_version = -1;
                                pattern_size = -1;

                            }
                        } catch (Exception ex) {
                            Log.i(FedConstants.MYTAG, ex.toString());
                            pattern_version = -5;
                            pattern_size = -5;
                        }
                        str += "\nPattern:" + pattern_version + ", " + pattern_size;
                    }

                    tvOutput.setText(str);

                }

                btnRefresh.setEnabled(true);
            }
        });
    }


    void updateOutput() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvOutput.setText(outputStr);
                btnRefresh.setEnabled(true);
            }
        });

    }

    public class MySyncThread extends Thread {
        @Override
        public void run() {
            boolean res = true;
            int netCount = NetUtils.getSavedNetworkCount(context);
            if (netCount == 0) {
                int netId = NetUtils.addNetwork(context);
                res = NetUtils.connectToNetwork(context, netId);
            }

            if (!res) {
                outputStr += "Wifi connection error";
            } else {

                String timeData = ServerUtils.getData(context, FedConstants.DOWNLOAD_TYPE_TIME, "time_sync");
                if (timeData != null && timeData.startsWith("time")) {
                    FEDUtils.saveSyncTimeInfo(context, timeData);
                    outputStr += "Time sync Successful.\n" + FEDUtils.getTimeSyncInfo(context);
                    //outputStr +="Time sync Successful.\n"+ timeData;
                } else
                    outputStr += "Time Sync: failed";
            }

            updateOutput();
        }
    }

}
