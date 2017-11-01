package edu.virginia.cs.mooncake.airsignapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import edu.virginia.cs.mooncake.airsignapp.utils.MyConstants;
import edu.virginia.cs.mooncake.airsignapp.utils.SharedPrefUtil;
import edu.virginia.cs.mooncake.airsignapp.utils.UtilFunctions;

public class TestService extends Service {
    String[] acceptedFileNames;

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    Context context;

    String lastUser, user, fileName;

    byte[] byteData;
    double[][][] templates;
    StringBuilder testLog;
    int failCount = 0, replaceIndex = -1;
    final int step = MyConfig.SAMPLING_STEP;
    float testTh;
    final int trainCount = MyConfig.TRAIN_COUNT;
    boolean isThreadRunning = false, accepted = false;
    String attacker, attackType, logTag, fileTag;

    double[][][] distances;
    double[][] mean_std_template;
    double[][] mean_std_test;
    double[][] d;
    double deviation;

    Thread thread;

    @Override
    public void onCreate() {
        super.onCreate();

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakeLockTag");
        wakeLock.acquire();

        d = new double[trainCount][];
        testLog = new StringBuilder();
        lastUser = user = null;
        context = this.getApplicationContext();

        testTh =  SharedPrefUtil.getSharedPrefFloatStored(MyConstants.THRESHOLD, context);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("stop")) {
            stopSelf();
        } else if (intent != null && intent.hasExtra("start")) {
            user = intent.getStringExtra(MyConstants.USER);
            lastUser = user;

            if (intent.hasExtra(MyConstants.ATTACKER)) {
                attacker = intent.getStringExtra(MyConstants.ATTACKER);
                attackType = intent.getStringExtra(MyConstants.ATTACK_TYPE);
                fileTag = "test" + attackType + "-" + attacker;
                logTag = "test" + attackType;

            } else {
                attacker = null;
                fileTag = "test";
                logTag = "test";
            }

            try {
                //byteData = FileUtil.readByteArrayFromFile(MyConstants.PATH_USERS + "/" + user, MyConstants.TEMPLATES);
                Log.i("template byte count", "" + byteData.length);
                templates = (double[][][]) UtilFunctions.deserialize(byteData);

                //byteData = FileUtil.readByteArrayFromFile(MyConstants.PATH_USERS + "/" + user, MyConstants.DISTANCES);
                distances = (double[][][]) UtilFunctions.deserialize(byteData);
                Log.i("distance byte count", "" + byteData.length);
                Log.i("distance length", "" + distances.length);

                mean_std_template = SignVerification.findMeanStdAllPairs(distances);

            } catch (Exception ex) {
                Log.i("Serialization Error", ex.toString());
                stopSelf();
            }

        } else if (intent != null && intent.hasExtra(MyConstants.TEST)) {
            user = intent.getStringExtra(MyConstants.USER);
            if (lastUser == null || !user.equals(lastUser)) {
                broadcastMsg(MyConstants.TEST, "User name doesn't match. " + lastUser + " vs " + user + ". Stopping service");
                stopSelf();
            }

            byteData = intent.getByteArrayExtra(MyConstants.BYTE_DATA);
            fileName = fileTag + "-" + user + "-" + intent.getStringExtra(MyConstants.FILE_NAME);
            //FileUtil.saveByteArrayToFile(MyConstants.PATH_RAW_DATA , fileName, byteData);

            thread = new MyThread();
            thread.start();

        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("Test Service", "Destroyed");
        if (thread != null && thread.isAlive())
            thread.interrupt();
        //FileUtil.appendStringToFile(MyConstants.PATH_LOGS, user+"-"+MyConstants.TEST_LOG, testLog.toString());
        if (accepted) {
            try {
                //FileUtil.saveByteArrayToFile(MyConstants.PATH_USERS + "/" + user, MyConstants.TEMPLATES, UtilFunctions.serialize(templates));
                //FileUtil.saveByteArrayToFile(MyConstants.PATH_USERS + "/" + user, MyConstants.DISTANCES, UtilFunctions.serialize(distances));
            } catch (Exception ex) {
                Log.i("Thread Exception", ex.toString());
            }
        }

        super.onDestroy();

    }

    public class MyThread extends Thread {

        @Override
        public void run() {
            try {
                isThreadRunning = true;
                double[][] data = (double[][]) UtilFunctions.deserialize(byteData);
                data = PreProcess.processSignSingle(data, step);

                for (int i = 0; i < trainCount; i++) {
                    broadcastMsg(MyConstants.TEST, "Calculating distance with \n " + i);
                    d[i] = Distance.distances(templates[i], data);
                }

                mean_std_test = SignVerification.meanStd(d);
                deviation = SignVerification.findTestDeviation(mean_std_template, mean_std_test);
                if (deviation <= testTh) {
                    testLog.append(logTag);
                    testLog.append(",");
                    testLog.append(fileName);
                    testLog.append(",");
                    testLog.append(deviation);
                    testLog.append(",");
                    testLog.append(testTh);
                    testLog.append(",");
                    testLog.append("accepetd,");

                    if (Math.random() < 0.5) {
                        int index = (int) Math.floor(Math.random() * trainCount);
                        templates[index] = data;

                        for (int i = 0; i < trainCount; i++) {
                            if (i != index) {
                                distances[i][index] = distances[index][i] = d[i];
                            }
                        }

                        testLog.append("inserted\n");
                        accepted = true;
                        mean_std_template = SignVerification.findMeanStdAllPairs(distances);

                    } else {
                        testLog.append("not inserted\n");
                    }

                    broadcastMsg(MyConstants.TEST_SUCCESS, "Succesful. \n Deviation: " + deviation);

                } else {
                    failCount++;
                    testLog.append(logTag);
                    testLog.append(",");
                    testLog.append(fileName);
                    testLog.append(",");
                    testLog.append(deviation);
                    testLog.append(",");
                    testLog.append(testTh);
                    testLog.append(",");
                    testLog.append("rejected\n");
                    broadcastMsg(MyConstants.TEST, "Failed! Please try again\nFail Count: " + failCount +"\nDeviation: "+deviation);
                }

                isThreadRunning = false;

            } catch (Exception ex) {
                Log.i("Thread Exception", ex.toString());
            }
        }
    }

    void stopThisService() {
        this.stopSelf();
    }

    void broadcastMsg(String type, String msg) {
        //Log.i("Broadcasting", type + ": " + msg);
        Intent i = new Intent(MyConstants.BCAST_TEST);
        i.putExtra(MyConstants.TYPE, type);
        i.putExtra(MyConstants.MESSAGE, msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        //Toast.makeText(this.getApplicationContext(), type + ": " + msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * *****************************************
     */
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
