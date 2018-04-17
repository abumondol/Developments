package edu.virginia.cs.mooncake.airsign;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.json.JSONObject;

import edu.virginia.cs.mooncake.airsign.utils.ConstantsUtil;
import edu.virginia.cs.mooncake.airsign.utils.FileUtil;
import edu.virginia.cs.mooncake.airsign.utils.NetUtil;

public class IntentServiceNetwork extends IntentService {

    public static String ACTION = "action";
    public static String LOGIN = "login";
    public static String CREATE_ACCOUNT = "create_account";
    public static String UPLOAD = "upload";

    public IntentServiceNetwork() {
        super("IntentServiceNetwork");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!intent.hasExtra(ACTION))
            return;

        if (intent.getStringExtra(ACTION).equals(LOGIN)) {
            ResultReceiver rec = intent
                    .getParcelableExtra(ConstantsUtil.INTENT_RESULT_RECEIVER);
            String email = intent.getStringExtra(ConstantsUtil.INTENT_EMAIL);
            String password = intent
                    .getStringExtra(ConstantsUtil.INTENT_PASSWORD);

            JSONObject json = NetUtil.checkLogin(getApplicationContext(),
                    email, password);

            int resultCode = 1;
            Bundle bundle = null;

            if (json == null) {
                resultCode = -1;
            } else if (json.isNull(ConstantsUtil.JSON_CONFIG) != true) {
                bundle = new Bundle();
                try {
                    bundle.putString(ConstantsUtil.BUNDLE_CONFIG,
                            json.getString(ConstantsUtil.JSON_CONFIG));
                    resultCode = 1;
                } catch (Exception ex) {
                    resultCode = -2;
                    bundle = null;
                }
            } else {
                try {
                    Log.i("Login response",
                            json.getString(ConstantsUtil.JSON_RESULT));
                } catch (Exception ex) {
                    Log.i("Login response", "json error");
                }
                resultCode = 0;
            }

            rec.send(resultCode, bundle);


        } else if (intent.getStringExtra(ACTION).equals(CREATE_ACCOUNT)) {
            ResultReceiver rec = intent
                    .getParcelableExtra(ConstantsUtil.INTENT_RESULT_RECEIVER);
            String email = intent.getStringExtra(ConstantsUtil.INTENT_EMAIL);
            String password = intent
                    .getStringExtra(ConstantsUtil.INTENT_PASSWORD);
            JSONObject json = NetUtil.checkLogin(getApplicationContext(),
                    email, password);

            int resultCode = 1;
            Bundle bundle = null;

            if (json == null) {
                resultCode = -1; // exception in Netutil at phone
            } else if (json.isNull(ConstantsUtil.JSON_RESULT)) {
                resultCode = -2; // exception in server
                try {
                    Log.i("Login response",
                            json.getString(ConstantsUtil.JSON_RESULT));
                } catch (Exception ex) {
                    Log.i("Login response", "json error");
                }

            } else {
                try {
                    if (json.getString(ConstantsUtil.JSON_RESULT).equals("ok")) {
                        bundle = new Bundle();

                        bundle.putString(ConstantsUtil.BUNDLE_CONFIG,
                                json.getString(ConstantsUtil.JSON_CONFIG));
                        resultCode = 1;
                    }else{
                        resultCode = 0;
                        bundle = null;
                    }
                } catch (Exception ex) {
                    resultCode = -3; // error in parsing json
                    bundle = null;
                }
            }

            rec.send(resultCode, bundle); // send method calls the
            // onReceiveResult method of rec

        } else if (intent.getStringExtra(ACTION).equals(UPLOAD)) {
            int count = FileUtil.getFileCount(this.getApplicationContext());
            Log.i("File Upload Called", "File Count: "+count);
            if(count>0) {
                NetUtil.uploadFiles(this.getApplicationContext());
            }
        }

    }

}
