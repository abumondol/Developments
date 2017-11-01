package edu.virginia.cs.mooncake.airsignapp;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import edu.virginia.cs.mooncake.airsignapp.utils.FileUtil;
import edu.virginia.cs.mooncake.airsignapp.utils.MyConstants;
import edu.virginia.cs.mooncake.airsignapp.utils.ServiceUtil;
import edu.virginia.cs.mooncake.airsignapp.utils.SharedPrefUtil;

public class TestActivity extends AppCompatActivity {

    String user, attacker, attackType;
    TextView tvNotification, tvUserName;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
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

        user = getIntent().getStringExtra(MyConstants.USER);
        SharedPrefUtil.putSharedPref(MyConstants.TYPE, MyConstants.TEST, this.getApplicationContext());
        SharedPrefUtil.putSharedPref(MyConstants.USER, user, this.getApplicationContext());
        SharedPrefUtil.putSharedPrefLong(MyConstants.TIME, System.currentTimeMillis(), this.getApplicationContext());


        tvNotification = (TextView) findViewById(R.id.tvNotificationTest);
        tvUserName = (TextView) findViewById(R.id.tvUserNameTest);

        tvNotification.setText("Welcome. Please sign in the air");
        tvUserName.setText("User: " + user);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(MyConstants.BCAST_TEST));
        context = this.getApplicationContext();

        Intent i = new Intent(this.getApplicationContext(), TestService.class);
        i.putExtra(MyConstants.START, MyConstants.START);
        i.putExtra(MyConstants.TYPE, MyConstants.TEST);
        i.putExtra(MyConstants.USER, user);

        if (getIntent().hasExtra(MyConstants.ATTACKER)) {
            attacker = getIntent().getStringExtra(MyConstants.ATTACKER);
            attackType = getIntent().getStringExtra(MyConstants.ATTACK_TYPE);
            i.putExtra(MyConstants.ATTACKER, attacker);
            i.putExtra(MyConstants.ATTACK_TYPE, attackType);

            tvUserName.setText("User: " + user + "\n Attacker: " + attacker + "\n Type:" + attackType);
        }
        startService(i);
    }

    @Override
    protected void onDestroy() {
        SharedPrefUtil.removeAll(this.getApplicationContext());
        if (ServiceUtil.isMySensorServiceRunning(this.getApplicationContext(), TestService.class.getName())) {
            Intent i = new Intent(this.getApplicationContext(), TestService.class);
            i.putExtra("stop", "stop");
            startService(i);
        }

        SharedPrefUtil.removeAll(this.getApplicationContext());
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    public void btnClick(View v) {
        if (v.getId() == R.id.btnCancelTest) {
            this.finish();
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(MyConstants.MESSAGE);
            String type = intent.getStringExtra(MyConstants.TYPE);

            tvNotification.setText(message);
            if (type.equals(MyConstants.TEST_SUCCESS))
                showSuccessDialog();

        }
    };

    void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Succesful... Congratulations!");
        builder.setMessage("Do you want to test again");

        // Set up the buttons
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvNotification.setText("Please sign in the air");
                dialog.cancel();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAndGoHome();
                dialog.cancel();
            }
        });

        builder.show();

    }

    void finishAndGoHome() {
        startActivity(new Intent(context, MobileMainActivity.class));
        this.finish();
    }

}
