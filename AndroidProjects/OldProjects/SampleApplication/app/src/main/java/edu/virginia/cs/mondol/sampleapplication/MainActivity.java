package edu.virginia.cs.mondol.sampleapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE = 1;
    private static String[] PERMISSION_LIST = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    Button buttonStartStop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        int permission1 = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission2 = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission1 != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(
                    PERMISSION_LIST,
                    REQUEST_CODE
            );
        }

        buttonStartStop = (Button) findViewById(R.id.buttonStartStop);
        refresh();

    }

    public void buttonClicked(View v){
        if(v.getId() == R.id.buttonStartStop) {
            // starts or stops the sensor service
            Intent i = new Intent(this, SensorService.class);
            if (isSensorServiceRunning()) {
                i.putExtra("action", "stop");
            } else {
                i.putExtra("action", "start");
            }
            startService(i);
            buttonStartStop.setEnabled(false);

        }else if(v.getId() == R.id.buttonRefresh) {
            refresh();

        }else if(v.getId() == R.id.buttonDataSummary){ //open the data summary activity
            Intent i = new Intent(this, DataSummaryActivity.class);
            startActivity(i);
        }
    }

    void refresh(){
        // set 'Stop' or 'Start' text on the button based on whether the sensor service is running or not.
        if(isSensorServiceRunning()){
            buttonStartStop.setText("Stop");
        }else{
            buttonStartStop.setText("Start");
        }

        buttonStartStop.setEnabled(true);
    }

    boolean isSensorServiceRunning(){ //check whether the sensor service is running in the background.
        String serviceName = SensorService.class.getName();
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

}



