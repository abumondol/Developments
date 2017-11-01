package edu.virginia.cs.mooncake.m2feddata;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import edu.virginia.cs.mooncake.m2feddata.myutils.DateTimeUtil;
import edu.virginia.cs.mooncake.m2feddata.myutils.M2FEDUtil;
import edu.virginia.cs.mooncake.m2feddata.myutils.MC;
import edu.virginia.cs.mooncake.m2feddata.myutils.ServiceUtil;
import edu.virginia.cs.mooncake.m2feddata.myutils.SharedPrefUtil;

public class WatchMainActivity extends Activity {

    private TextView tvFId, tvMId, tvHand;
    Button btn, btnBack;
    Handler handler;
    boolean layoutInflated = false;
    int familyId, memberId, hand;
    String handStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                tvFId = (TextView) stub.findViewById(R.id.tvFId);
                tvMId = (TextView) stub.findViewById(R.id.tvMId);
                tvHand = (TextView) stub.findViewById(R.id.tvHand);
                btn = (Button) stub.findViewById(R.id.btnStartStop);
                btnBack = (Button) stub.findViewById(R.id.btnBack);
                refresh();
                layoutInflated = true;
            }
        });

        handler = new Handler();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        familyId = SharedPrefUtil.getSharedPrefInt(MC.FAMILY_ID, this.getApplicationContext());
        memberId = SharedPrefUtil.getSharedPrefInt(MC.MEMBER_ID, this.getApplicationContext());
        hand = SharedPrefUtil.getSharedPrefInt(MC.HAND, this.getApplicationContext());
        handStr = "Undefined";
        if (hand == 1)
            handStr = "Right";
        else if (hand == 2)
            handStr = "Left";


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (layoutInflated)
            refresh();

    }

    public void btnClick(View v) {
        if (v.getId() == R.id.btnStartStop) {
            boolean status = ServiceUtil.isMySensorServiceRunning(this.getApplicationContext(), WatchSensorService.class.getName());
            if (btn.getText().equals("Start") && status == false) {
                Intent i = new Intent(this, WatchSensorService.class);

                String tag = getSerial() + "-m2fed-" + M2FEDUtil.getFormattedNumber(familyId) + "-" + M2FEDUtil.getFormattedNumber(memberId) + "-" + handStr + "-" + DateTimeUtil.getDateTimeString(System.currentTimeMillis(), 3).replace(' ', '-') + ".wada";

                i.putExtra("start", tag);
                startService(i);
                btn.setText("Started");
                btn.setBackgroundResource(R.color.grey);
                btn.setEnabled(false);
                btnBack.setEnabled(false);
                handler.postDelayed(runnable, 2000);

            } else if (btn.getText().equals("Stop") && status == true) {
                Intent i = new Intent(this, WatchSensorService.class);
                i.putExtra("stop", "" + System.currentTimeMillis());
                startService(i);
                btn.setEnabled(false);
                btn.setText("Stopped");
                btn.setBackgroundResource(R.color.grey);
                handler.postDelayed(runnable, 2000);
            }

        } else if (v.getId() == R.id.btnBack) {
            startActivity(new Intent(this.getApplicationContext(), SettingsActivity.class));
            this.finish();
            return;
        }

    }

    public void refresh() {
        boolean status = ServiceUtil.isMySensorServiceRunning(this.getApplicationContext(), WatchSensorService.class.getName());

        if (status == true) {
            btn.setText("Stop");
            btn.setBackgroundResource(R.color.red);
            btnBack.setEnabled(false);
        } else {
            btn.setText("Start");
            btn.setBackgroundResource(R.color.green);
            btnBack.setEnabled(true);
        }

        btn.setEnabled(true);

        tvFId.setText("Family ID: " + M2FEDUtil.getFormattedNumber(familyId));
        tvMId.setText("Member ID: " + M2FEDUtil.getFormattedNumber(memberId));
        tvHand.setText("Hand: " + handStr);
    }

    String getSerial() {
        String serial = Build.SERIAL;
        String[] serials = {"14442D31F8BC4E0", "14432D35F47689C", "14422D3CF36A67A", "14442D34F8B155A",
                "14442D2DF803B0E", "14442D37F7F674A", "14432D22F43D3F8", "14422D3BF394278",
                "14442D33F8BCB1A", "14442D33F8B2F72", "14432D32F4A4EDE", "14432D17F487D24"};


        for (int i = 0; i < serials.length; i++) {
            if (serial.equals(serials[i])) {
                serial = "W-" + (i + 1) + "-" + serial;
            }
        }

        return serial;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refresh();
        }
    };

}