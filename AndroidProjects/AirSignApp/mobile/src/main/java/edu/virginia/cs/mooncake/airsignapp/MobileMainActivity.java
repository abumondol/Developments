package edu.virginia.cs.mooncake.airsignapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.virginia.cs.mooncake.airsignapp.utils.FileUtil;
import edu.virginia.cs.mooncake.airsignapp.utils.MyConstants;


public class MobileMainActivity extends Activity {

    public static final int LIST_CODE_TYPE = 0;
    public static final int LIST_CODE_USER = 1;
    public static final int LIST_CODE_IMPOSTER = 2;
    public static final int LIST_CODE_PASSWORD = 3;
    String[] listNames = {"type", "user", "imposter", "password"};

    int typeIndex=0, userIndex=-1, imposterIndex=-1, passwordIndex=-1, listCode=0;

    String[] typeList = {"genuine", "mimic"};
    String[] userList, passwordList;


    Context context;
    Button btnType, btnUser, btnImposter, btnPassword, btnAddUser, btnAddPassword, btnStart;
    TextView tvType, tvUser, tvPassword, tvImposter;

    private static final int REQUEST_CODE = 1;
    private static String[] PERMISSIONS_LIST = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_main);
        context = this.getApplicationContext();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
            verifyPermissions(this);

        btnType = (Button) findViewById(R.id.btnType);
        btnUser = (Button) findViewById(R.id.btnUser);
        btnImposter = (Button) findViewById(R.id.btnImposter);
        btnPassword = (Button) findViewById(R.id.btnPassword);
        btnAddUser = (Button) findViewById(R.id.btnAddUser);
        btnAddPassword = (Button) findViewById(R.id.btnAddPassword);
        btnStart = (Button) findViewById(R.id.btnStart);

        tvType = (TextView) findViewById(R.id.tvType);
        tvUser = (TextView) findViewById(R.id.tvUser);
        tvImposter = (TextView) findViewById(R.id.tvImposter);
        tvPassword = (TextView) findViewById(R.id.tvPassword);

        userList = FileUtil.getUserList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
        Log.i("MobileMainActivity", "onResume");
    }

    public void btnClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.btnType:
                listCode = LIST_CODE_TYPE;
                showList();
                break;

            case R.id.btnUser:
                listCode = LIST_CODE_USER;
                showList();
                break;

            case R.id.btnImposter:
                listCode = LIST_CODE_IMPOSTER;
                showList();
                break;

            case R.id.btnPassword:
                listCode = LIST_CODE_PASSWORD;
                showList();
                break;

            case R.id.btnAddUser:
                listCode = LIST_CODE_USER;
                showTextPrompt();
                break;

            case R.id.btnAddPassword:
                listCode = LIST_CODE_PASSWORD;
                showTextPrompt();
                break;

            case R.id.btnStart:
                Intent intent = new Intent(this, DataReceiptionActivity.class);
                intent.putExtra(MyConstants.TYPE, typeList[typeIndex]);
                intent.putExtra(MyConstants.USER, userList[userIndex]);
                intent.putExtra(MyConstants.PASSWORD, passwordList[passwordIndex]);
                if(typeIndex==1)
                    intent.putExtra(MyConstants.IMPOSTER, userList[imposterIndex]);

                startActivity(intent);
                break;

            case R.id.btnData:
                startActivity(new Intent(this, DataActivity.class));
                break;

            case R.id.btnExit:
                this.finish();
                break;

        }

    }

    void refresh() {
        btnImposter.setEnabled(false);
        btnPassword.setEnabled(false);
        btnAddPassword.setEnabled(false);
        btnStart.setEnabled(false);

        tvType.setText(typeList[typeIndex]);
        tvUser.setText("Please select");
        tvPassword.setText("Please select");

        if (typeIndex == 0) {
            imposterIndex = -1;
            tvImposter.setText("N/A");
        }

        if (userIndex >= 0 && userList != null) {
            tvUser.setText(userList[userIndex]);
            btnPassword.setEnabled(true);
            btnAddPassword.setEnabled(true);
            if (typeIndex == 1) {
                btnImposter.setEnabled(true);
            }

            if (passwordIndex >= 0 && (typeIndex == 0 || typeIndex == 1 && imposterIndex >= 0)) {
                btnStart.setEnabled(true);
            }

            if (passwordIndex >= 0 && passwordList!=null)
                tvPassword.setText(passwordList[passwordIndex]);


            if (imposterIndex >= 0)
                tvImposter.setText(userList[imposterIndex]);
        }

    }

    void setSelectedIndex(int val) {
        if(listCode == LIST_CODE_TYPE)
            typeIndex = val;
        else if(listCode == LIST_CODE_IMPOSTER)
            imposterIndex = val;
        else if(listCode == LIST_CODE_PASSWORD)
            passwordIndex = val;
        else {
            userIndex = val;
            passwordList = FileUtil.getPasswordList(userList[userIndex]);
            passwordIndex = -1;
        }

        refresh();
    }

    void setInputText(String s) {
        if (listCode == LIST_CODE_USER) {
            if (searchList(userList, s))
                dialogMessage("User already exists. Not added.");
            else {
                FileUtil.addUser(s);
                userList = FileUtil.getUserList();
            }

        } else if (listCode == LIST_CODE_PASSWORD) {
            String user = userList[userIndex];
            passwordList = FileUtil.getPasswordList(user);

            if (searchPasswordList(passwordList, s))
                dialogMessage("Password already exists. Not added.");
            else {
                int index = 1;
                if (passwordList != null)
                    index =passwordList.length + 1;

                FileUtil.addPassword(user, index+" "+s);
                passwordList = FileUtil.getPasswordList(user);
            }

        }

        refresh();
    }

    boolean searchPasswordList(String[] list, String password) {
        if (list == null)
            return false;

        for (int i = 0; i < list.length; i++) {
            if (list[i].split(" ")[1].equals(password))
                return true;
        }
        return false;
    }

    boolean searchList(String[] list, String str) {
        if (list == null)
            return false;

        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(str))
                return true;
        }
        return false;
    }

    String[] getList(){
        if(listCode == LIST_CODE_TYPE)
            return typeList;
        else if(listCode == LIST_CODE_USER || listCode == LIST_CODE_IMPOSTER)
            return userList;
        else if(listCode == LIST_CODE_PASSWORD)
            return passwordList;

        return null;
    }


    void showList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select " + listNames[listCode]);
        builder.setItems(getList(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setSelectedIndex(which);
            }
        }).create();

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    void showTextPrompt() {
        // Set up the input
        final EditText input = new EditText(this);
        String title = "Add a user";
        if (listCode == LIST_CODE_PASSWORD)
            title = "Add a password for " + userList[userIndex];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = input.getText().toString();
                s = s.trim();
                if (s.contains(" "))
                    dialogMessage("Space is not allowed. Please try another.");
                else if (s.length() == 0)
                    dialogMessage("Empty input. Please try again.");
                else
                    setInputText(s);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    void dialogMessage(String msg) {
        new AlertDialog.Builder(this).setMessage(msg).setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }
        ).show();
    }

    public static void verifyPermissions(Activity activity) {
        int permission1 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int permission3 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.BODY_SENSORS);
        if (permission1 != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED || permission3 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_LIST,
                    REQUEST_CODE
            );
        }
    }

}
