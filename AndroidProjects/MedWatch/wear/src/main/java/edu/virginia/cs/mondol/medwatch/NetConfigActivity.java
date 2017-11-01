package edu.virginia.cs.mondol.medwatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import edu.virginia.cs.mondol.medwatch.config.FEDConfigWrapper;
import edu.virginia.cs.mondol.medwatch.config.MyNetConfig;
import edu.virginia.cs.mondol.medwatch.utils.FedConstants;
import edu.virginia.cs.mondol.medwatch.utils.FileUtils;
import edu.virginia.cs.mondol.medwatch.utils.SharedPrefUtil;

public class NetConfigActivity extends Activity {

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private TextView mTextView;
    Context context;
    int pattern_version, pattern_size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakeLockTag");
        wakeLock.acquire();

        setContentView(R.layout.activity_net_config);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        pattern_version = 0;
        pattern_size = 0;

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.tvNetConfig);
                showNetConfig();
            }
        });
        context = this.getApplicationContext();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onDestroy() {
        wakeLock.release();
        super.onDestroy();
    }

    public void btnClick(View v) {
        if (v.getId() == R.id.btnLoadNetConfig) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MyNetConfig nc = FileUtils.readNetConfig();
                    if (nc.file_read_message != null) {
                        mTextView.setText(nc.file_read_message);
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

                        pattern_version = SharedPrefUtil.getSharedPrefInt(FedConstants.PATTERN_VERSION, context);
                        pattern_size = SharedPrefUtil.getSharedPrefInt(FedConstants.PATTERN_SIZE, context);
                        if (pattern_version <= 0) {
                            float[][][][] patterns = FileUtils.getPatterns();
                            if (patterns != null) {
                                pattern_version = (int) patterns[0][0][0][0];
                                pattern_size = patterns.length - 1;
                            } else {
                                pattern_version = -2;
                                pattern_size = -2;
                            }
                        }

                        showNetConfig();
                    }
                }
            });

        } else if (v.getId() == R.id.btnBeaconTest) {
            startActivity(new Intent(this.getApplicationContext(), BLETestActivity.class));
        }
    }


    void showNetConfig() {
        MyNetConfig nc = FEDConfigWrapper.getNetConfig(this.getApplicationContext());
        mTextView.setText("SSID: " + nc.wifi_ssid + "\nPASSWORD: " + nc.wifi_password + "\nIP: " + nc.server_ip + "\nBeacons: " + nc.beacon_indices_str + "\nPattern:" + pattern_version + ", " + pattern_size);
    }
}
