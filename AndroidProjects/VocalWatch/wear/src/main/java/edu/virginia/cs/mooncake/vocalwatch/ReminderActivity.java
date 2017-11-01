package edu.virginia.cs.mooncake.vocalwatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import edu.virginia.cs.mooncake.vocalwatch.utils.DateTimeUtil;
import edu.virginia.cs.mooncake.vocalwatch.utils.FileUtil;
import edu.virginia.cs.mooncake.vocalwatch.utils.MC;
import edu.virginia.cs.mooncake.vocalwatch.utils.SharedPrefUtil;

public class ReminderActivity extends Activity implements TextToSpeech.OnInitListener, RecognitionListener {

    static final int STATE_VIBRATED = 0;
    static final int STATE_QUERY_TOLD = 1;
    static final int STATE_THANK_YOU = 2;

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    private SpeechRecognizer spRec = null;
    private Intent recognizerIntent;
    private TextToSpeech tts;
    private Button btn1, btn2;
    private final int REQ_CODE_TTS = 2;
    String myUtteranceId, fileName, speakText, displayText = "Hi", configDisplayText;
    Vibrator vb;
    Context context;
    Handler handler;

    JSONObject jsonConfig, jsonCurrentEvent;
    JSONArray eventsArray, logArray;
    int eventIndex, event_repeat_count;
    int sessionState;
    boolean reminderProcessed = false;
    long initialInterval = 10, interval = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakeLockTag");
        wakeLock.acquire();

        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn1.setBackgroundColor(Color.GREEN);
        btn2.setBackgroundColor(Color.RED);

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
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,"en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

        fileName = Build.SERIAL + "-" + SharedPrefUtil.getSharedPref(MC.SUBJECT, context);


        eventIndex = SharedPrefUtil.getSharedPrefInt(MC.EVENT_INDEX, context);
        try {
            jsonConfig = new JSONObject(SharedPrefUtil.getSharedPref(MC.CONFIG_STRING, context));
            eventsArray = jsonConfig.getJSONArray(MC.EVENTS);
            jsonCurrentEvent = eventsArray.getJSONObject(eventIndex);
            event_repeat_count = getJsonFieldInt(jsonCurrentEvent, MC.REPEAT_COUNT);
            configDisplayText = jsonCurrentEvent.getString(MC.DISPLAY_TEXT);

            logArray = new JSONArray();
            fileName += "-" + jsonConfig.getString(MC.CONFIG_CODE);

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

        if (event_repeat_count > 0 && reminderProcessed == false)
            removeReminder();

        SharedPrefUtil.putSharedPref(MC.CONFIG_STRING, jsonConfig.toString(), context);

        setNextReminder();

        logData(sessionState, "destroy", 0, "");
        try {
            JSONObject logJs = new JSONObject();
            logJs.put(MC.CONFIG_CODE, jsonConfig.getString(MC.CONFIG_CODE));
            logJs.put(MC.QUERY_CODE, jsonCurrentEvent.getString(MC.QUERY_CODE));
            logJs.put(MC.REPEAT_COUNT, jsonCurrentEvent.getString(MC.REPEAT_COUNT));
            logJs.put(MC.LOG_ARRAY, logArray);

            FileUtil.appendStringToFile(fileName, logJs.toString() + "\n");
        } catch (Exception ex) {
            Log.i(MC.TTS, "MyTTSActivity on Destroy " + ex.toString());
        }


        wakeLock.release();
        super.onDestroy();
    }

    void startTTS() {
        Intent intent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, REQ_CODE_TTS);
    }


    void startReminderSession() {
        vb.vibrate(2000);
        sessionState = STATE_VIBRATED;
        displayText = configDisplayText;
        promptSpeechInput(displayText);
        logData(sessionState, "start", 0, "");
    }

    void giveThanks(String speakText) {
        sessionState = STATE_THANK_YOU;
        speakOut(speakText);
    }

    void playQuery() {
        if (event_repeat_count == 0)
            speakText = getJsonFieldString(jsonCurrentEvent, MC.QUERY);
        else
            speakText = getJsonFieldString(jsonCurrentEvent, MC.REQUERY);

        displayText = configDisplayText;
        speakOut(speakText);

    }

    void requestToRepeat() {
        speakText = "Sorry, can you please reply again?";
        displayText = "Repeat please";
        speakOut(speakText);
    }


    void speechInputReturned(int resultCode, String receivedData) {

        logData(sessionState, "stt", resultCode, receivedData);
        Log.i(MC.TTS, "resultCode: " + resultCode + ", Received data: " + receivedData);

        if (resultCode != RESULT_OK || receivedData == null) {
            rescheduleReminder(0);
            closeThisActivity("Result Code:" + resultCode);
            return;
        }

        receivedData = receivedData.toLowerCase();
        if (receivedData.contains("done")) {
            removeReminder();
            giveThanks("Thank you.");

        } else if (receivedData.contains("yes")) {
            if (sessionState == STATE_VIBRATED) {
                sessionState = STATE_QUERY_TOLD;
                playQuery();
            } else {
                removeReminder();
                giveThanks("Thank you.");
            }

        } else if (receivedData.contains("what") || receivedData.contains("repeat")) {
            sessionState = STATE_QUERY_TOLD;
            playQuery();

        } else if ((receivedData.contains("remind") || receivedData.contains("ask"))) {
            if (receivedData.contains("dont") || receivedData.contains("don't") || receivedData.contains("do not")) {
                removeReminder();
                giveThanks("Okay, Thank you");
            } else {
                rescheduleReminder(0);
                giveThanks("I will remind you later. Thank you");
            }

        } else if (receivedData.startsWith("no ") || receivedData.startsWith("no, ") || receivedData.equals("no")) {
            rescheduleReminder(0);
            giveThanks("I will ask you later. Thank you");

        } else if (receivedData.contains("thank") || receivedData.contains("okay") || receivedData.contains("ok")) {
            rescheduleReminder(0);
            giveThanks("I will ask you later. Thank you.");

        } else {
            if (sessionState == STATE_VIBRATED) {
                rescheduleReminder(0);
                closeThisActivity(receivedData);
                return;
            } else {
                requestToRepeat();
            }
        }

    }

    void TTSFinished(String utId) {
        if (sessionState == STATE_THANK_YOU) {
            closeThisActivity("Thank you");
            return;
        }
        final String utteranceId = utId;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                promptSpeechInput(displayText);
            }
        });

    }

    void removeReminder() {
        if (event_repeat_count > 0)
            eventsArray.remove(eventIndex);
        reminderProcessed = true;
    }

    void rescheduleReminder(long time) {

        try {

            long delay;
            if (time == 0) {
                String hms = getJsonFieldString(jsonCurrentEvent, MC.REQUERY_INTERVAL);
                delay = DateTimeUtil.hmsStringToMillis(hms);
            } else {
                delay = time;
            }

            if (event_repeat_count == 0) {
                JSONObject js = new JSONObject(jsonCurrentEvent.toString());
                js.put(MC.REPEAT_COUNT, event_repeat_count + 1);
                long t = jsonCurrentEvent.getLong(MC.TIME_MILLIS);
                t = (t + delay) % (24 * 60 * 60 * 1000);
                js.put(MC.TIME_MILLIS, t);
                eventsArray.put(js);

            } else {
                if (time > 0 || time == 0 && event_repeat_count <= 3) {
                    jsonCurrentEvent.put(MC.REPEAT_COUNT, event_repeat_count + 1);
                    long t = jsonCurrentEvent.getLong(MC.TIME_MILLIS);
                    t = (t + delay) % (24 * 60 * 60 * 1000);
                    jsonCurrentEvent.put(MC.TIME_MILLIS, t);

                } else {
                    removeReminder();
                }
            }


        } catch (Exception ex) {
            Log.i(MC.TTS, "reschduleReminder " + ex.toString());
        }

        Log.i(MC.TTS, "reschduleReminder after add reminder event: " + jsonConfig.toString());
        reminderProcessed = true;
    }

    void setNextReminder() {
        try {
            NextEvent nextEvent = VocalWatchUtils.getNextEvent(jsonConfig);
            SharedPrefUtil.putSharedPrefInt(MC.EVENT_INDEX, nextEvent.index, context);
            VocalWatchUtils.setOrCancelAlarm(context, true, nextEvent.absoluteTime);
        } catch (Exception ex) {
            Log.i(MC.TTS, "setNextReminder " + ex.toString());
        }
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
                startReminderSession();
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


    private void promptSpeechInput(String dialogMessage) {
        btn1.setText("Speak");
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

    int getJsonFieldInt(JSONObject js, String fieldName) {
        try {
            return js.getInt(fieldName);
        } catch (Exception ex) {
            return -1;
        }
    }

    void closeThisActivity() {
        this.finish();

    }

    void closeThisActivity(String msg) {
        btn1.setText(msg);

    }

    void logData(int state, String action, int resultCode, String data) {
        JSONObject js = new JSONObject();
        try {
            js.put("time", System.currentTimeMillis());
            js.put("state", state);
            js.put("action", action);
            js.put("resultCode", resultCode);
            js.put("data", data);
        } catch (Exception ex) {
            Log.i(MC.TTS, "getLogObject: " + ex.toString());
        }

        logArray.put(js);
    }

    public void btnClick(View v) {
        if (v.getId() == R.id.btn1) {
            spRec.cancel();
            removeReminder();
            logData(sessionState, "btn1", 0, "");


        } else if (v.getId() == R.id.btn2) {
            spRec.cancel();
            removeReminder();
            logData(sessionState, "btn2", 0, "");

        }

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

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
        btn1.setText(data.get(0));

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
