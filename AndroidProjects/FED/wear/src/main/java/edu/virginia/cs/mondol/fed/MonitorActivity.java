package edu.virginia.cs.mondol.fed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;

public class MonitorActivity extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
    }

    public void btnClick(View v){
        if(v.getId() == R.id.btnMonitorBites){
            startActivity(new Intent(this.getApplicationContext(), MonitorOutputActivity.class).putExtra("type", 1));
        }else if(v.getId() == R.id.btnMonitorData){
            startActivity(new Intent(this.getApplicationContext(), DataActivity.class));
        }else if(v.getId() == R.id.btnMonitorExit){
            this.finish();
        }

    }
}
