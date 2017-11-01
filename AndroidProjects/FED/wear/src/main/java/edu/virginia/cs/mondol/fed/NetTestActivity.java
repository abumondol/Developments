package edu.virginia.cs.mondol.fed;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import edu.virginia.cs.mondol.fed.utils.FedConstants;
import edu.virginia.cs.mondol.fed.utils.NetUtils;
import edu.virginia.cs.mondol.fed.utils.ServerUtils;

public class NetTestActivity extends Activity {

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private TextView mTextView;
    Button btnEnable, btnTest, btnRefresh;
    WifiManager wifiManager;
    WifiInfo wifiInfo;
    Context context;
    String status = "",serverConnectionStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakeLockTag");
        wakeLock.acquire();

        setContentView(R.layout.activity_net_test);
        context = this.getApplicationContext();
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.statusNetInfo);
                btnEnable = (Button) stub.findViewById(R.id.btnNetEnable);
                btnTest = (Button) stub.findViewById(R.id.btnNetTest);
                btnRefresh = (Button) stub.findViewById(R.id.btnNetRefresh);
                serverConnectionStatus = "Server connection: n/a";
                refresh();
            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onDestroy() {
        wakeLock.release();
        super.onDestroy();
    }

    public void btnClick(View v) {
        if (v.getId() == R.id.btnNetEnable) {
            wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
            if (btnEnable.getText().equals(FedConstants.TURN_ON) && !wifiManager.isWifiEnabled())
                wifiManager.setWifiEnabled(true);
            else if (btnEnable.getText().equals(FedConstants.TURN_OFF) && wifiManager.isWifiEnabled())
                wifiManager.setWifiEnabled(false);

            btnEnable.setEnabled(false);

        }  else if (v.getId() == R.id.btnNetTest) {
            serverConnectionStatus = "Server connection: called";
            refresh();
            btnEnable.setEnabled(false);
            btnTest.setEnabled(false);
            btnRefresh.setEnabled(false);
            new MyThread().start();

        }else if (v.getId() == R.id.btnNetRefresh) {
            refresh();

        }else if (v.getId() == R.id.btnNetExit) {
            this.finish();
        }
    }

    private void refresh() {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();

        status = "";
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if (connectivityManager.getActiveNetworkInfo() != null) {
            if (connectivityManager.getActiveNetworkInfo().isConnected())
                status += "CM:C";
            else
                status += "CM:NC";

        } else
            status += "CM:X";

        status+= ", "+NetUtils.getSavedNetworkInfo(context)+"\n";


        btnEnable.setEnabled(true);
        btnTest.setEnabled(true);
        btnRefresh.setEnabled(true);

        if (wifiManager.isWifiEnabled()) {
            status += "WiFi is ON, " + "IP: "+wifiInfo.getIpAddress();
            btnEnable.setText(FedConstants.TURN_OFF);

        } else {
            status += "WiFi is OFF, " + "IP: "+wifiInfo.getIpAddress();
            btnEnable.setText(FedConstants.TURN_ON);
        }

        mTextView.setText(status+"\n"+serverConnectionStatus);

        NetUtils.getSavedNetworkInfo(context);

    }


    void updateStatus() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(status +"\n"+ serverConnectionStatus);
                btnEnable.setEnabled(true);
                btnTest.setEnabled(true);
                btnRefresh.setEnabled(true);
            }
        });

    }

    public class MyThread extends Thread {
        @Override
        public void run() {
            boolean res = true;
            int netCount = NetUtils.getSavedNetworkCount(context);
            if(netCount==0){
                int netId = NetUtils.addNetwork(context);
                res = NetUtils.connectToNetwork(context, netId);
            }

            if(!res){
                serverConnectionStatus = "Wifi connection error";
            }else {

                String data = ServerUtils.getData(context, FedConstants.DOWNLOAD_TYPE_TIME, "connection_test");
                if (data != null && data.startsWith("time"))
                    serverConnectionStatus = "Server connection: ok";
                else
                    serverConnectionStatus = "Server connection: failed";
            }

            updateStatus();
        }
    }
}
