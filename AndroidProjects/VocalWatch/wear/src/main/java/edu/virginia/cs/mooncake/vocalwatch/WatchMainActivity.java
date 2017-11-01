package edu.virginia.cs.mooncake.vocalwatch;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.virginia.cs.mooncake.vocalwatch.utils.DateTimeUtil;
import edu.virginia.cs.mooncake.vocalwatch.utils.FileUtil;
import edu.virginia.cs.mooncake.vocalwatch.utils.MC;
import edu.virginia.cs.mooncake.vocalwatch.utils.SharedPrefUtil;

public class WatchMainActivity extends WearableActivity {


    private RelativeLayout mContainerView;
    private TextView tvTitle, tvStatus, tvSubject;
    private Button btnStartStop, btnSubject, btnReminderList;
    JSONObject jsonConfig;
    String configString, configCode;
    Context context;
    NextEvent nextEvent;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static String[] PERMISSION_LIST = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_main);
        setAmbientEnabled();

        mContainerView = (RelativeLayout) findViewById(R.id.container);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvSubject = (TextView) findViewById(R.id.tvSubject);
        btnStartStop = (Button) findViewById(R.id.btnStartStop);
        btnSubject = (Button) findViewById(R.id.btnSubject);
        btnReminderList = (Button) findViewById(R.id.btnReminderList);
        context = this.getApplicationContext();

        mContainerView.setBackgroundColor(Color.BLACK);
        tvTitle.setTextColor(Color.WHITE);
        tvSubject.setTextColor(Color.WHITE);
        tvStatus.setTextColor(Color.WHITE);

        //Check if storage read-write permission is enabled
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
            verifyPermissions(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    public void btnClick(View v) {
        int id = v.getId();
        if (id == R.id.btnStartStop) {
            try {
                if (btnStartStop.getText().equals("Start")) {
                    String subject = SharedPrefUtil.getSharedPref(MC.SUBJECT, context);
                    if (subject == null) {
                        tvStatus.setText("Select Subject");
                        return;
                    }

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

                    if (jsonConfig != null) {
                        Log.i(MC.TTS, "Starting controlled activity");
                        Intent i = new Intent(this.getApplicationContext(), ControlledActivity.class);
                        i.putExtra(MC.CONFIG_STRING, jsonConfig.toString());
                        startActivity(i);
                        return;
                    }

                    nextEvent = VocalWatchUtils.getNextEvent(jsonConfig);
                    SharedPrefUtil.putSharedPref(MC.CONFIG_STRING, jsonConfig.toString(), context);
                    SharedPrefUtil.putSharedPrefInt(MC.EVENT_INDEX, nextEvent.index, context);

                    VocalWatchUtils.setOrCancelAlarm(context, true, nextEvent.absoluteTime);

                    btnStartStop.setText("Started");
                    btnStartStop.setEnabled(false);
                    btnStartStop.setBackgroundColor(Color.GRAY);

                } else {
                    VocalWatchUtils.setOrCancelAlarm(context, false, 0);
                    SharedPrefUtil.removeEntry(MC.CONFIG_STRING, context);
                    SharedPrefUtil.removeEntry(MC.SUBJECT, context);
                    configString = null;
                    jsonConfig = null;
                    btnStartStop.setText("Stopped");
                    btnStartStop.setEnabled(false);
                    btnStartStop.setBackgroundColor(Color.GRAY);
                }
            } catch (Exception ex) {
                Log.i(MC.TTS, "btnClick: " + ex.toString());
            }

        } else if (id == R.id.btnSubject) {
            startActivity(new Intent(this.getApplicationContext(), MyListActivity.class).putExtra(MC.LIST_TYPE, 0));

        } else if (id == R.id.btnReminderList) {
            //Log.i(MC.TTS, "reminder list started");
            configStringToJson();
            //Log.i(MC.TTS, "reminder list config string ot json done");
            try {
                JSONArray ja = jsonConfig.getJSONArray(MC.EVENTS);
                int len = ja.length();
                String[] reminderList = new String[len];
                long[] times = new long[len];

                int i, j;
                for (i = 0; i < len; i++) {
                    times[i] = ja.getJSONObject(i).getLong(MC.TIME_MILLIS);
                    int repeat_count = ja.getJSONObject(i).getInt(MC.REPEAT_COUNT);
                    if (repeat_count == 0) {
                        reminderList[i] = repeat_count + ", " + DateTimeUtil.millisToTimeString(times[i]) + ", " + ja.getJSONObject(i).getString(MC.QUERY);
                    } else {
                        reminderList[i] = repeat_count + ", " + DateTimeUtil.millisToTimeString(times[i]) + ", " + ja.getJSONObject(i).getString(MC.REQUERY);
                    }
                }

                long temp;
                String tempString;
                for (i = 0; i < len; i++) {
                    for (j = i; j > 0 && times[j - 1] > times[j]; j--) {
                        temp = times[j];
                        times[j] = times[j - 1];
                        times[j - 1] = temp;

                        tempString = reminderList[j];
                        reminderList[j] = reminderList[j - 1];
                        reminderList[j - 1] = tempString;

                    }
                }

                startActivity(new Intent(this.getApplicationContext(), MyListActivity.class).putExtra(MC.LIST_TYPE, 1).putExtra(MC.ITEM_LIST, reminderList));
            } catch (Exception ex) {
                Log.i(MC.TTS, "btnReminderList: " + ex.toString());
            }

        } else if (id == R.id.btnNet) {
            startActivity(new Intent(this.getApplicationContext(), NetActivity.class));

        } else if (id == R.id.btnRefresh) {
            refresh();
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

    public void configStringToJson() {
        jsonConfig = null;
        configString = SharedPrefUtil.getSharedPref(MC.CONFIG_STRING, this.getApplicationContext());
        if (configString != null) {
            try {
                jsonConfig = new JSONObject(configString);
                configCode = jsonConfig.getString(MC.CONFIG_CODE);
            } catch (Exception ex) {
                Log.i(MC.TTS, "configStringToJson" + ex.toString());
                jsonConfig = null;
            }
        }

    }


    private void refresh() {
        btnStartStop.setEnabled(true);
        String s = "error";

        configStringToJson();
        if (jsonConfig != null) {
            try {
                s = "Running: " + configCode + ", " + DateTimeUtil.millisToHmsString(VocalWatchUtils.getNextEvent(jsonConfig).delay);
            } catch (Exception ex) {
                Log.i(MC.TTS, "In refresh: " + ex.toString());
            }
            btnStartStop.setText("Stop");
            btnStartStop.setBackgroundColor(Color.RED);
            btnSubject.setEnabled(false);
            btnReminderList.setEnabled(true);
        } else {
            s = "Not Running";
            btnStartStop.setText("Start");
            btnStartStop.setBackgroundColor(Color.GREEN);
            btnSubject.setEnabled(true);
            btnReminderList.setEnabled(false);
        }

        tvStatus.setText(s);

        String subject = SharedPrefUtil.getSharedPref(MC.SUBJECT, this.getApplicationContext());
        if (subject != null)
            tvSubject.setText(subject);
        else
            tvSubject.setText("N/A");
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


    /// All display default overrideen methods
    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {

    }


}
