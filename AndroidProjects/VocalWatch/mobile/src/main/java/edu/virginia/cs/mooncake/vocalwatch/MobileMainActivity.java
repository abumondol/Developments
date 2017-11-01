package edu.virginia.cs.mooncake.vocalwatch;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import edu.virginia.cs.mooncake.vocalwatch.utils.FileUtil;
import edu.virginia.cs.mooncake.vocalwatch.utils.MC;

public class MobileMainActivity extends Activity {

    JSONObject jsonConfig;
    String configString, configCode;
    TextView tvStatus;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static String[] PERMISSION_LIST = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_main);

        tvStatus = (TextView) findViewById(R.id.tvStatus);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
            verifyPermissions(this);

    }

    public void btnClick(View v){
        if(v.getId() == R.id.btnPhone){
            if (FileUtil.isConfigAvailable() == false) {
                tvStatus.setText("Config N/A");
                return;
            }

            configString = FileUtil.readConfig();
            readAndProcessConfig(configString);
            if (jsonConfig == null) {
                tvStatus.setText("Config Error");
                Log.i(MC.TTS, "Config Error: " + configString);
                return;
            }


            Log.i(MC.TTS, "Starting controlled activity");
            Intent i = new Intent(this.getApplicationContext(), PhoneDataActivity.class);
            i.putExtra(MC.CONFIG_STRING, jsonConfig.toString());
            startActivity(i);

        }else if(v.getId() == R.id.btnWatch){
            startActivity( new Intent(this.getApplicationContext(), WatchDataActivity.class));

        }else if (v.getId() == R.id.btnExit){
            this.finish();
        }
    }


    public void readAndProcessConfig(String confStr) {
        jsonConfig = VocalWatchUtils.checkJSONString(confStr);
        if (jsonConfig == null)
            return;
        try {
            jsonConfig = VocalWatchUtils.processEventTimes(jsonConfig);
            configCode = jsonConfig.getString(MC.CONFIG_CODE);
            Log.i(MC.TTS, "JSONCofig read and process: " + jsonConfig.toString());
        } catch (Exception ex) {
            Log.i(MC.TTS, "readAndProcessConfig: " + ex.toString());
            jsonConfig = null;
        }

    }


    public static void verifyPermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSION_LIST,
                    PERMISSION_REQUEST_CODE
            );
        }

    }

}
