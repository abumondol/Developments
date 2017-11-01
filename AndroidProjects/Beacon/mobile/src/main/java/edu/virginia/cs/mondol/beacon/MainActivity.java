package edu.virginia.cs.mondol.beacon;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    RadioGroup rgFilter, rgMode, rgSave;
    EditText etMac;
    TextView tvNotification;

    private static final int REQUEST_CODE = 1;
    private static String[] PERMISSIONS_LIST = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_ADMIN
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rgFilter = (RadioGroup) findViewById(R.id.rgFilter);
        rgMode = (RadioGroup) findViewById(R.id.rgMode);
        rgSave = (RadioGroup) findViewById(R.id.rgSave);
        etMac = (EditText) findViewById(R.id.etMac);
        tvNotification = (TextView) findViewById(R.id.tvNotification);

        //Check if storage read-write permission is enabled
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
            verifyPermissions(this);
    }

    public void btnClick(View v) {
        if (v.getId() == R.id.btnStartView) {
            int filterCode = 0, modeCode = 0, saveCode = 0;
            String mac = "";

            switch (rgFilter.getCheckedRadioButtonId()) {
                case R.id.rbFilterAll:
                    filterCode = MyConstants.FILTER_CODE_ALL;
                    break;
                case R.id.rbFilterFile:
                    filterCode = MyConstants.FILTER_CODE_FILE;
                    break;
                case R.id.rbFilterEmbed:
                    filterCode = MyConstants.FILTER_CODE_EMBED;
                    break;
                case R.id.rbFilterInput:
                    filterCode = MyConstants.FILTER_CODE_INPUT;
                    break;
                default:
                    filterCode = MyConstants.FILTER_CODE_ALL;
            }

            switch (rgMode.getCheckedRadioButtonId()) {
                case R.id.rbModeLowPower:
                    modeCode = ScanSettings.SCAN_MODE_LOW_POWER;
                    break;
                case R.id.rbModeBalance:
                    modeCode = ScanSettings.SCAN_MODE_BALANCED;
                    break;
                case R.id.rbModeLowLatency:
                    modeCode = ScanSettings.SCAN_MODE_LOW_LATENCY;
                    break;
                default:
                    modeCode = ScanSettings.SCAN_MODE_LOW_POWER;
            }

            switch (rgSave.getCheckedRadioButtonId()) {
                case R.id.rbSaveNo:
                    saveCode = 0;
                    break;
                case R.id.rbSaveYes:
                    saveCode = 1;
                    break;
                default:
                    saveCode = 0;
            }

            if(filterCode == MyConstants.FILTER_CODE_INPUT){
                mac = etMac.getText().toString();
                if(!isValidMac(mac)){
                    tvNotification.setText("Invalid MAC.");
                    return;
                }
            }else{
                mac = "";
            }

            Intent intent = new Intent(this.getApplicationContext(), BeaconViewActivity.class);
            intent.putExtra("filter", filterCode);
            intent.putExtra("mode", modeCode);
            intent.putExtra("save", saveCode);
            intent.putExtra("mac", mac.trim());
            startActivity(intent);
        }
    }

    public static void verifyPermissions(Activity activity) {
        int permission1 = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permission3 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADMIN);
        if (permission1 != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED || permission3 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_LIST,
                    REQUEST_CODE
            );
        }

    }

    boolean isValidMac(String mac){
        if(mac==null)
            return false;

        mac = mac.trim();
        if(mac.length()!=17)
            return false;

        String[] str = mac.split(":");
        if(str.length!=6)
            return false;

        for(int i=0;i<6;i++){
            if(str[i].length()!=2 || str[i].matches("^.*[^a-zA-Z0-9].*$"))
                return false;
        }

        return true;
    }


}
