package edu.virginia.cs.mooncake.airsignapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import edu.virginia.cs.mooncake.airsignapp.utils.MyConstants;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TextView tv = (TextView)findViewById(R.id.tvMessage);
        tv.setText(getIntent().getStringExtra(MyConstants.MESSAGE));
    }

    public void btnClick(View v){
        if(v.getId() == R.id.btnHome){
            startActivity(new Intent(this.getApplicationContext(), MobileMainActivity.class));
            this.finish();
        }
    }

}
