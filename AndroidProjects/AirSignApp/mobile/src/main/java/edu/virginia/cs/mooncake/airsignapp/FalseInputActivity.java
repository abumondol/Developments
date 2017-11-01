package edu.virginia.cs.mooncake.airsignapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import edu.virginia.cs.mooncake.airsignapp.utils.FileUtil;
import edu.virginia.cs.mooncake.airsignapp.utils.MyConstants;

public class FalseInputActivity extends AppCompatActivity {

    String attackType, attacker, realUser;
    TextView tvAttackType, tvAttacker, tvRealUser;
    String[] userList;
    String[] typeList = {"1", "2"};
    ;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_false_input);
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

        tvAttackType = (TextView) findViewById(R.id.tvAttackType);
        tvAttacker = (TextView) findViewById(R.id.tvAttacker);
        tvRealUser = (TextView) findViewById(R.id.tvRealUser);

        context = this.getApplicationContext();
        userList = FileUtil.getUserList();

    }

    public void btnClick(View v) {
        if (v.getId() == R.id.btnAttackType) {
            dialogTypeList();

        } else if (v.getId() == R.id.btnAttacker) {
            dialogInputTextTrain();

        } else if (v.getId() == R.id.btnRealUser) {
            dialogUserList();

        } else if (v.getId() == R.id.btnAttack) {
            if (attackType != null && attacker != null && realUser != null) {
                Intent i = new Intent(this.getApplicationContext(), TestActivity.class);
                i.putExtra(MyConstants.USER, realUser);
                i.putExtra(MyConstants.ATTACKER, attacker);
                i.putExtra(MyConstants.ATTACK_TYPE, attackType);
                startActivity(i);
                this.finish();
            }

        } else if (v.getId() == R.id.btnFalseCancel) {
            this.finish();
        }
    }


    void dialogUserList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Real User");
        builder.setItems(userList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                realUser = userList[which];
                tvRealUser.setText(realUser);
            }
        }).create();

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                realUser = null;
                tvRealUser.setText("NA");
                dialog.cancel();
            }
        });

        builder.show();
    }

    void dialogTypeList() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select type");
        builder.setItems(typeList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                attackType = typeList[which];
                tvAttackType.setText(attackType);
            }
        }).create();

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                attackType = null;
                tvAttackType.setText("NA");
                dialog.cancel();
            }
        });

        builder.show();
    }


    void dialogInputTextTrain() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attacker");


        final EditText input = new EditText(this);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                attacker = input.getText().toString();

                if (attacker.trim().length() == 0) {
                    attacker = null;
                    tvAttacker.setText("NA");

                } else {
                    tvAttacker.setText(attacker);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                attacker = null;
                tvAttacker.setText("NA");
            }
        });

        builder.show();

    }


}
