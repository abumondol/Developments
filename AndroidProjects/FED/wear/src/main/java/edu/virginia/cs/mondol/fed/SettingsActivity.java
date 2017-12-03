package edu.virginia.cs.mondol.fed;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import edu.virginia.cs.mondol.fed.utils.FedConstants;
import edu.virginia.cs.mondol.fed.utils.SharedPrefUtil;

public class SettingsActivity extends Activity {

    private TextView tvStatus;
    private CheckBox cbNoBeacon;
    Context context;
    int alertType;
    String alertMessage, alertTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = this.getApplicationContext();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                tvStatus = (TextView) stub.findViewById(R.id.tvStatus);
                cbNoBeacon = (CheckBox) stub.findViewById(R.id.cbNoBeacon);
                refresh();
            }
        });

    }


    public void btnClick(View v) {

        if (v.getId() == R.id.cbNoBeacon) {
            if (cbNoBeacon.isChecked())
                SharedPrefUtil.putSharedPrefBoolean(FedConstants.NO_BEACON, true, context);
            else
                SharedPrefUtil.putSharedPrefBoolean(FedConstants.NO_BEACON, false, context);

        } else if (v.getId() == R.id.btnSettingsRefresh) {
            refresh();

        } else if (v.getId() == R.id.btnSettingsReset) {
            SharedPrefUtil.putSharedPrefBoolean(FedConstants.NO_BEACON, false, context);
            refresh();
        }

    }

    void refresh() {

        if (SharedPrefUtil.getSharedPrefBoolean(FedConstants.NO_BEACON, context))
            cbNoBeacon.setChecked(true);
        else
            cbNoBeacon.setChecked(false);

    }

}
