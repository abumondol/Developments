package edu.virginia.cs.mooncake.m2feddata;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import edu.virginia.cs.mooncake.m2feddata.myutils.FileUtil;
import edu.virginia.cs.mooncake.m2feddata.myutils.M2FEDUtil;
import edu.virginia.cs.mooncake.m2feddata.myutils.MC;
import edu.virginia.cs.mooncake.m2feddata.myutils.ServiceUtil;
import edu.virginia.cs.mooncake.m2feddata.myutils.SharedPrefUtil;

public class SettingsActivity extends Activity {

    private TextView tvFamilyId, tvMemberId;
    private Button btnData, btnNext;
    RadioGroup radioGroup;
    RadioButton rbLeft, rbRight;
    boolean layoutInflated = false;
    Context context;
    int familyId = 0, memberId = 0;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                tvFamilyId = (TextView) stub.findViewById(R.id.tvFamilyId);
                tvMemberId = (TextView) stub.findViewById(R.id.tvMemberId);
                btnData = (Button) stub.findViewById(R.id.btnData);
                btnNext = (Button) stub.findViewById(R.id.btnNext);
                radioGroup = (RadioGroup) stub.findViewById(R.id.radioGroupHand);
                rbLeft = (RadioButton) stub.findViewById(R.id.radioHandLeft);
                rbRight = (RadioButton) stub.findViewById(R.id.radioHandRight);
                layoutInflated = true;
                refresh();
            }
        });

        context = this.getApplicationContext();
        //Check if storage read-write permission is enabled
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
            verifyStoragePermissions(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (layoutInflated)
            refresh();

    }

    public void btnClick(View v) {
        switch (v.getId()) {
            case R.id.btnMinusFamilyId:
                if (familyId > 1) {
                    familyId--;
                    tvFamilyId.setText(M2FEDUtil.getFormattedNumber(familyId));
                }
                break;

            case R.id.btnPlusFamilyId:
                familyId++;
                tvFamilyId.setText(M2FEDUtil.getFormattedNumber(familyId));
                if (familyId >= 1 && memberId >= 1)
                    btnNext.setEnabled(true);
                break;

            case R.id.btnMinusMemberId:
                if (memberId > 1) {
                    memberId--;
                    tvMemberId.setText(M2FEDUtil.getFormattedNumber(memberId));
                }
                break;

            case R.id.btnPlusMemberId:
                memberId++;
                tvMemberId.setText(M2FEDUtil.getFormattedNumber(memberId));
                if (familyId >= 1 && memberId >= 1)
                    btnNext.setEnabled(true);
                break;

            case R.id.btnData:
                startActivity(new Intent(this.getApplicationContext(), DataActivity.class));
                this.finish();
                break;

            case R.id.btnNext:
                if (familyId > 0 && memberId > 0) {
                    int rbId = radioGroup.getCheckedRadioButtonId();
                    int hand;
                    if (rbId == R.id.radioHandRight)
                        hand = 1;
                    else
                        hand = 2;

                    SharedPrefUtil.putSharedPrefInt(MC.FAMILY_ID, familyId, this.getApplicationContext());
                    SharedPrefUtil.putSharedPrefInt(MC.MEMBER_ID, memberId, this.getApplicationContext());
                    SharedPrefUtil.putSharedPrefInt(MC.HAND, hand, this.getApplicationContext());

                    startActivity(new Intent(this.getApplicationContext(), WatchMainActivity.class));
                    this.finish();
                }
                break;

        }

    }

    void refresh() {
        if (ServiceUtil.isMySensorServiceRunning(this.getApplicationContext(), WatchSensorService.class.getName())) {
            startActivity(new Intent(this.getApplicationContext(), WatchMainActivity.class));
            this.finish();
            return;
        }

        if (FileUtil.fileCount() == 0)
            btnData.setEnabled(false);
        else
            btnData.setEnabled(true);

        if (familyId >= 1 && memberId >= 1)
            btnNext.setEnabled(true);
        else
            btnNext.setEnabled(false);

        tvFamilyId.setText(M2FEDUtil.getFormattedNumber(familyId));
        tvMemberId.setText(M2FEDUtil.getFormattedNumber(memberId));
    }


    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}
