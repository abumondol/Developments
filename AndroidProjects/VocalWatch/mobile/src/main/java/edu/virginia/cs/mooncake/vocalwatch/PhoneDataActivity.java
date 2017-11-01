package edu.virginia.cs.mooncake.vocalwatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import edu.virginia.cs.mooncake.vocalwatch.utils.DateTimeUtil;
import edu.virginia.cs.mooncake.vocalwatch.utils.FileUtil;
import edu.virginia.cs.mooncake.vocalwatch.utils.MC;
import edu.virginia.cs.mooncake.vocalwatch.utils.SharedPrefUtil;

public class PhoneDataActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, RecognitionListener {

    TextView tvData, tvAsk;

    static final int STATE_VIBRATED = 0;
    static final int STATE_QUERY_TOLD = 1;
    static final int STATE_DETAILS_TOLD = 2;
    static final int STATE_THANK_YOU = 10;

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    private SpeechRecognizer spRec = null;
    private Intent recognizerIntent;
    private TextToSpeech tts;
    private Button btn;
    private final int REQ_CODE_TTS = 2;
    String myUtteranceId, fileName, speakText;
    Vibrator vb;
    Context context;
    Handler handler;

    JSONObject jsonConfig, jsonCurrentEvent, logJs;
    JSONArray eventsArray, logArray;
    int eventIndex, sessionState;
    long currentTime, initial_interval = 3 * 1000, interval = 3 * 1000;
    String displayStr;
    boolean systemTold = false;

    String date_time;
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_data);

        tvAsk = (TextView) findViewById(R.id.tvAsk);
        tvAsk.setText("");
        tvData = (TextView) findViewById(R.id.tvPhoneData);
        tvAsk.setText("");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 1.0f;
        getWindow().setAttributes(lp);
        context = this.getApplicationContext();
        vb = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        handler = new Handler();

        spRec = SpeechRecognizer.createSpeechRecognizer(this);
        spRec.setRecognitionListener(this);

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "en-US");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);


        eventIndex = SharedPrefUtil.getSharedPrefInt(MC.EVENT_INDEX, context);
        try {
            jsonConfig = new JSONObject(getIntent().getStringExtra(MC.CONFIG_STRING));
            eventsArray = jsonConfig.getJSONArray(MC.EVENTS);
            if (eventsArray.length() == 0) {
                this.finish();
            }
            eventIndex = 0;
            jsonCurrentEvent = eventsArray.getJSONObject(eventIndex);
            currentTime = System.currentTimeMillis();

            logJs = new JSONObject();
            logArray = new JSONArray();
            logJs.put(MC.SUBJECT, SharedPrefUtil.getSharedPref(MC.SUBJECT, context));
            logJs.put(MC.START_TIME, currentTime);
            date_time = DateTimeUtil.dateTimeString(currentTime);
            fileName = Build.SERIAL + "-" + SharedPrefUtil.getSharedPref(MC.SUBJECT, context) + "-" + jsonConfig.getString(MC.CONFIG_CODE) + "-" + date_time;

        } catch (Exception ex) {
            Log.i(MC.TTS, "MyTTSActivity onCreate " + ex.toString());
            closeThisActivity("Json parse error");
        }

        startTTS();

    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        if (spRec != null)
            spRec.destroy();

        logData(sessionState, "destroy", 0, "");
        try {
            logJs.put(MC.END_TIME, System.currentTimeMillis());
            logJs.put(MC.LOG_ARRAY, logArray);
            logJs.put(MC.CONFIG_JSON, jsonConfig);
            FileUtil.appendStringToFile(fileName, logJs.toString());
        } catch (Exception ex) {
            Log.i(MC.TTS, "MyTTSActivity on Destroy " + ex.toString());
        }

        //wakeLock.release();
        handler.removeCallbacks(runnable);

        super.onDestroy();
    }

    public void btnClick(View v){
        if (v.getId() == R.id.btnExit){
            this.finish();
        }
    }


    void startTTS() {
        Log.i(MC.TTS, "Starting TTS");
        Intent intent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, REQ_CODE_TTS);
    }


    void startReminderSession() {
        vb.vibrate(2000);
        sessionState = STATE_VIBRATED;
        promptSpeechInput();
    }

    void giveThanks(String speakText) {
        sessionState = STATE_THANK_YOU;
        speakOut(speakText);
    }

    void playQuery() {
        sessionState = STATE_QUERY_TOLD;
        speakText = getJsonFieldString(jsonCurrentEvent, MC.QUERY);
        speakOut(speakText);
    }

    void playDetails() {
        sessionState = STATE_DETAILS_TOLD;
        speakText = getJsonFieldString(jsonCurrentEvent, MC.DETAILS);
        speakOut(speakText);
    }

    void playQueryAndDetails() {
        sessionState = STATE_DETAILS_TOLD;
        speakText = getJsonFieldString(jsonCurrentEvent, MC.QUERY) + ". " + getJsonFieldString(jsonCurrentEvent, MC.DETAILS);
        speakOut(speakText);
    }

    void requestToRepeat() {
        speakText = "Sorry, can you please repeat?";
        speakOut(speakText);
    }

    void requestToRepeatProblem() {
        speakText = "There seem to be some problems. Can you please repeat?";
        speakOut(speakText);
    }

    void speechInputReturned(int resultCode, String receivedData) {
        logData(sessionState, "stt", resultCode, receivedData);
        Log.i(MC.TTS, "resultCode: " + resultCode + ", Received data: " + receivedData);

        displayStr = receivedData;
        //tvAsk.setText(displayStr);
        tvData.setText(displayStr);

        if (resultCode != RESULT_OK || receivedData == null) {
            displayStr = "Res Code: " + resultCode + ", Data: " + receivedData;
            if (!systemTold)
                startReminderSession();
            else
                requestToRepeatProblem();
            return;
        }

        receivedData = receivedData.toLowerCase();
        if (systemTold == false && (receivedData.startsWith("system") || receivedData.startsWith("reminder") || receivedData.startsWith("device"))) {
            systemTold = true;
            if (receivedData.split(" ").length == 1) {
                playQuery();
                return;
            }

        }


        if (!systemTold) {
            displayStr = "Start with 'System', 'Reminder' or 'Device'.\n\n Received : " + receivedData;
            startReminderSession();
            return;
        }

        if (receivedData.contains("what")) {
            if (sessionState == STATE_VIBRATED || sessionState == STATE_QUERY_TOLD) {
                playQuery();
            } else if (sessionState == STATE_DETAILS_TOLD) {
                playQueryAndDetails();
            } else {
                requestToRepeat();
            }

            return;
        }

        if (receivedData.contains("detail") || receivedData.contains("more") && !(receivedData.contains("do not") || receivedData.contains("don't"))) {
            playQueryAndDetails();
            return;
        }


        if (receivedData.contains("have not") || receivedData.contains("haven't")) {
            if (receivedData.contains("done") || receivedData.contains("taken")) {
                if ((receivedData.contains("do not") || receivedData.contains("don't")) && (receivedData.contains("remind") || receivedData.contains("ask") || receivedData.contains("repeat"))) {
                    giveThanks("Okay. Thank you");
                    return;
                }

                if (checkRemindCommand(receivedData))
                    return;

                giveThanks("I'll remind you later. Thank you.");
            } else {
                requestToRepeat();
            }

            return;
        }

        if (receivedData.contains("done") || receivedData.contains("taken")) {
            sessionState = STATE_THANK_YOU;
            giveThanks("Thank you.");
            return;
        }

        if (receivedData.contains("yes")) {
            if (sessionState == STATE_VIBRATED) {
                playQuery();
            } else if (sessionState == STATE_QUERY_TOLD && getJsonFieldString(jsonCurrentEvent, MC.QUERY_TYPE).equals(MC.REPEAT_REMINDER)) {
                giveThanks("Thank you");
            } else {
                requestToRepeat();
            }

            return;
        }

        if (receivedData.startsWith("no")) {
            if ((receivedData.contains("do not") || receivedData.contains("don't")) && (receivedData.contains("remind") || receivedData.contains("ask") || receivedData.contains("repeat"))) {
                giveThanks("Okay. Thank you");
                return;
            }

            if (receivedData.equals("no") && sessionState == STATE_QUERY_TOLD && getJsonFieldString(jsonCurrentEvent, MC.QUERY_TYPE).equals(MC.REPEAT_REMINDER)) {
                giveThanks("I will remind you later. Thank you");
                return;
            }

            if (checkRemindCommand(receivedData))
                return;

            requestToRepeat();
            return;
        }


        if ((receivedData.contains("do not") || receivedData.contains("don't")) && (receivedData.contains("remind") || receivedData.contains("ask") || receivedData.contains("repeat"))) {
            giveThanks("Okay. Thank you");
            return;
        }


        if (receivedData.contains("repeat") || (receivedData.contains("say") || receivedData.contains("tell")) && receivedData.contains("again")) {
            if (sessionState == STATE_VIBRATED) {
                requestToRepeat();
            } else if (sessionState == STATE_QUERY_TOLD) {
                playQuery();
            } else if (sessionState == STATE_DETAILS_TOLD) {
                playQueryAndDetails();
            } else {
                requestToRepeat();
            }

            return;
        }

        if (checkRemindCommand(receivedData))
            return;

        if (receivedData.contains("okay") || receivedData.contains("ok") || receivedData.contains("thank") || receivedData.contains("got it")) {
            giveThanks("I will remind you later. Thank you");
            return;
        }

        requestToRepeat();

    }

    boolean checkRemindCommand(String receivedData) {

        if (!receivedData.contains("remind") && !receivedData.contains("ask"))
            return false;

        if (receivedData.contains("hour") || receivedData.contains("minute")) {
            String hour = null, minute = null, res = "";
            if (receivedData.contains("hour")) {
                String substr = receivedData.substring(0, receivedData.indexOf("hour"));
                if (substr.length() < 1)
                    return false;

                String[] strArr = substr.split(" ");
                int len = strArr.length;
                if (strArr[len - 1].equals("half")) {

                    if (len > 3 && strArr[len - 2].equals("and")) {
                        hour = strArr[len - 3] + " and half ";
                    } else {
                        hour = " half ";
                    }

                } else {
                    hour = strArr[len - 1];

                }
            }

            if (receivedData.contains("minute")) {
                String substr = receivedData.substring(0, receivedData.indexOf("minute"));
                if (substr.length() < 1)
                    return false;

                String[] strArr = substr.split(" ");
                minute = strArr[strArr.length - 1];
            }

            if (hour == null && minute == null)
                return false;
            if (hour != null)
                res += hour + " hour";
            if (minute != null)
                res += minute + " minute";

            sessionState = STATE_THANK_YOU;
            giveThanks("I will remind you after " + res + ". Thank you");
            return true;
        }

        if (receivedData.contains("later") || receivedData.contains("again")) {
            sessionState = STATE_THANK_YOU;
            giveThanks("I will remind you later. Thank you");
            return true;
        }

        return false;
    }

    void TTSFinished(String utId) {
        if (sessionState == STATE_THANK_YOU) {
            eventIndex++;
            if (eventIndex == eventsArray.length()) {
                closeThisActivity("Finished. Thank You");
                return;
            }

            try {
                jsonCurrentEvent = eventsArray.getJSONObject(eventIndex);
            } catch (Exception ex) {
                Log.i(MC.TTS, ex.toString());
            }

            handler.postDelayed(runnable, interval);
            return;
        }

        final String utteranceId = utId;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                promptSpeechInput();
            }
        });

    }


    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(MC.TTS, "TTS : This Language is not supported");

            } else {
                Log.i(MC.TTS, "TTS: Initializaion Successful");
                //startReminderSession();
                handler.postDelayed(runnable, initial_interval);

            }

        } else {
            Log.e(MC.TTS, "TTS: Initilization Failed!");
        }

    }

    private void speakOut(String txt) {
        logData(sessionState, "tts", 0, txt);

        final String text = txt;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(MC.TTS, "TTS: speakOut called");
                myUtteranceId = System.currentTimeMillis() + "";
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, myUtteranceId);
            }
        });


    }

    private void promptSpeechInput() {
        tvAsk.setText(displayStr + " \n Speak ...");
        spRec.startListening(recognizerIntent);
    }

    UtteranceProgressListener myUtteranceProgressListener = new UtteranceProgressListener() {
        @Override
        public void onDone(String utteranceId) {
            TTSFinished(utteranceId);
        }

        @Override
        public void onError(String utteranceId) {
        }

        @Override
        public void onStart(String utteranceId) {
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_TTS) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts = new TextToSpeech(this, this);
                tts.setOnUtteranceProgressListener(myUtteranceProgressListener);
                Log.i(MC.TTS, "TTS: Initialized");

            } else {
                Intent installVoice = new Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installVoice);
                Log.i(MC.TTS, "TTS: install called");
            }
        }

    }


    String getJsonFieldString(JSONObject js, String fieldName) {
        try {
            return js.getString(fieldName);
        } catch (Exception ex) {
            return null;
        }
    }


    void closeThisActivity(String msg) {
        tvAsk.setText(msg);
        vb.vibrate(2 * 1000);
    }

    void logData(int state, String action, int resultCode, String data) {

        if (action.equals("stt")) {
            index++;
        }

        JSONObject js = new JSONObject();

        try {
            js.put("time", System.currentTimeMillis());
            js.put("sttIndex", index);
            js.put("state", state);
            js.put("action", action);
            js.put("resultCode", resultCode);
            js.put("reminderIndex", eventIndex);
            if (data != null)
                js.put("data", data);
            else
                js.put("data", "null xxx");
        } catch (Exception ex) {
            Log.i(MC.TTS, "getLogObject: " + ex.toString());
        }

        Log.i(MC.TTS, "Logging data: " + js.toString());
        logArray.put(js);


    }

    String getDisplayText() {
        try {
            return eventsArray.getJSONObject(eventIndex).getString(MC.DISPLAY_TEXT);
        } catch (Exception ex) {
            Log.i(MC.TTS, "getDisplayText error. Event Index: " + eventIndex);
        }
        return null;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            displayStr = getDisplayText();
            systemTold = false;
            logData(sessionState, "start", 0, "");
            startReminderSession();
        }
    };

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////// Recognition Listener Override Methods//////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void onReadyForSpeech(Bundle params) {
        //Log.d(MC.TTS, "onReadyForSpeech");
        //btn1.setText("onReadyForSpeech");
    }

    public void onBeginningOfSpeech() {
        //Log.d(MC.TTS, "onBeginningOfSpeech");
        //btn1.setText("onBegginingOfSpeech");
    }

    public void onRmsChanged(float rmsdB) {
        //Log.d(MC.TTS, "onRmsChanged");
        //btn1.setText("onRmsChanged");
    }

    public void onBufferReceived(byte[] buffer) {
        //Log.d(MC.TTS, "onBufferReceived");
        //btn1.setText("onBufferReceived");
    }

    public void onEndOfSpeech() {
        //Log.d(MC.TTS, "onEndofSpeech");
        //btn1.setText("onEndOfSpeech");
    }

    public void onError(int error) {
        Log.d(MC.TTS, "error " + error);
        //btn1.setText("onErrorSpeech");
        speechInputReturned(error, null);
    }

    public void onResults(Bundle results) {
        String str = new String();
        Log.d(MC.TTS, "onResults " + results);
        ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for (int i = 0; i < data.size(); i++) {
            Log.d(MC.TTS, "result " + data.get(i));
            str += data.get(i);
        }

        Log.d(MC.TTS, "onResults, size: " + data.size() + ", " + str);
        //tvAsk.setText(data.get(0));
        tvData.setText(data.get(0));

        speechInputReturned(Activity.RESULT_OK, data.get(0));
    }

    public void onPartialResults(Bundle partialResults) {
        //Log.d(MC.TTS, "onPartialResults");
        //btn1.setText("onPartialResult");
    }

    public void onEvent(int eventType, Bundle params) {
        //Log.d(MC.TTS, "onEvent " + eventType);
        //btn1.setText("onEvent");
    }


}
