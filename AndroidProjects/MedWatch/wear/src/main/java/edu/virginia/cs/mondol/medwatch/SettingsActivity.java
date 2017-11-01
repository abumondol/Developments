package edu.virginia.cs.mondol.medwatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import edu.virginia.cs.mondol.medwatch.utils.FedConstants;
import edu.virginia.cs.mondol.medwatch.utils.FileUtils;
import edu.virginia.cs.mondol.medwatch.utils.SharedPrefUtil;

public class SettingsActivity extends Activity {

    private TextView tvStatus;
    private CheckBox cbUsePattern, cbAtHomeOnly, cbTestMeals;
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
                cbUsePattern = (CheckBox) stub.findViewById(R.id.cbUsePattern);
                cbAtHomeOnly = (CheckBox) stub.findViewById(R.id.cbAtHomeOnly);
                cbTestMeals = (CheckBox) stub.findViewById(R.id.cbTestMeals);
                refresh();
            }
        });

    }


    public void btnClick(View v) {

        if (v.getId() == R.id.cbUsePattern) {
            if (cbUsePattern.isChecked())
                SharedPrefUtil.putSharedPrefInt(FedConstants.PATTERN_USE, 1, context);
            else
                SharedPrefUtil.putSharedPrefInt(FedConstants.PATTERN_USE, -1, context);

        } else if (v.getId() == R.id.cbAtHomeOnly) {
            if (cbAtHomeOnly.isChecked())
                SharedPrefUtil.putSharedPrefInt(FedConstants.AT_HOME_ONLY, 1, context);
            else
                SharedPrefUtil.putSharedPrefInt(FedConstants.AT_HOME_ONLY, -1, context);

        } else if (v.getId() == R.id.cbTestMeals) {
            if (cbTestMeals.isChecked()) {
                SharedPrefUtil.putSharedPrefInt(FedConstants.TEST_MEALS, 1, context);
                //Log.i(FedConstants.MYTAG, "Meal Checked");
            } else {
                SharedPrefUtil.putSharedPrefInt(FedConstants.TEST_MEALS, -1, context);
                //Log.i(FedConstants.MYTAG, "Meal UnChecked");
            }

        } else if (v.getId() == R.id.btnSettingsRefresh) {
            refresh();

        } else if (v.getId() == R.id.btnSettingsReset) {
            alertTitle = "RESET";
            alertMessage = "Are you sure to reset app?";
            alertType = 1;
            AlertDialog alert = getAlertDialogBuilder().create();
            alert.show();
        }

    }

    void refresh() {

        int use_code = SharedPrefUtil.getSharedPrefInt(FedConstants.PATTERN_USE, context);
        if (use_code > 0)
            cbUsePattern.setChecked(true);
        else
            cbUsePattern.setChecked(false);

        int at_home_only = SharedPrefUtil.getSharedPrefInt(FedConstants.AT_HOME_ONLY, context);
        if (at_home_only > 0)
            cbAtHomeOnly.setChecked(true);
        else
            cbAtHomeOnly.setChecked(false);

        int test_meals = SharedPrefUtil.getSharedPrefInt(FedConstants.TEST_MEALS, context);
        if (test_meals > 0) {
            cbTestMeals.setChecked(true);
            //Log.i(FedConstants.MYTAG, "Meal Checked by App");
        }else {
            cbTestMeals.setChecked(false);
            //Log.i(FedConstants.MYTAG, "Meal Unchecked by App");
        }
    }


    public AlertDialog.Builder getAlertDialogBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(alertTitle);
        builder.setMessage(alertMessage);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (alertType == 1){
                    SharedPrefUtil.removeAll(context);
                    FileUtils.deleteAllFiles();
                }

                dialog.dismiss();
            }

        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        return builder;

    }


}
