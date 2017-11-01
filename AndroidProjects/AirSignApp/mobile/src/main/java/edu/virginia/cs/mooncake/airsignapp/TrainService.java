package edu.virginia.cs.mooncake.airsignapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import edu.virginia.cs.mooncake.airsignapp.utils.MyConstants;
import edu.virginia.cs.mooncake.airsignapp.utils.UtilFunctions;

public class TrainService extends Service {

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    Context context;

    String lastUser, user, fileName;
    String[] acceptedFileNames;
    byte[] byteData;
    double[][][] templates;
    StringBuilder trainLog;
    int count = 0, replaceIndex = -1;
    final int step = MyConfig.SAMPLING_STEP;
    final float trainTh = MyConfig.TRAIN_THRESHOLD;
    final int trainCount = MyConfig.TRAIN_COUNT;
    boolean isThreadRunning = false;
    Thread thread;

    double[][][] distances;


    @Override
    public void onCreate() {
        super.onCreate();

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakeLockTag");
        wakeLock.acquire();

        distances = new double[trainCount][trainCount][];
        templates = new double[trainCount][][];
        acceptedFileNames = new String[trainCount];
        trainLog = new StringBuilder();
        lastUser = user = null;
        context = this.getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("stop")) {
            stopSelf();
        } else if (intent != null && intent.hasExtra(MyConstants.TRAIN)) {
            user = intent.getStringExtra(MyConstants.USER);
            if (lastUser == null) {
                lastUser = user;
            } else if (!user.equals(lastUser)) {
                broadcastMsg(MyConstants.TRAIN, "User name doesn't match. " + lastUser + " vs " + user + ". Stopping service");
                stopSelf();
            }

            byteData = intent.getByteArrayExtra(MyConstants.BYTE_DATA);
            fileName = "train-" + user + "-" + intent.getStringExtra(MyConstants.FILE_NAME);
            //FileUtil.saveByteArrayToFile(MyConstants.PATH_RAW_DATA, fileName, byteData);

            trainLog.append("train,");
            trainLog.append(fileName);
            trainLog.append(",");
            trainLog.append(count);
            if (isThreadRunning) {
                trainLog.append("threadRunning");
                trainLog.append("\n");
                return Service.START_NOT_STICKY;
            }
            trainLog.append("\n");

            try {
                double[][] data = (double[][]) UtilFunctions.deserialize(byteData);
                if (count < trainCount) {
                    templates[count] = PreProcess.processSignSingle(data, step);
                    acceptedFileNames[count] = fileName;
                } else if (replaceIndex >= 0) {
                    templates[replaceIndex] = PreProcess.processSignSingle(data, step);
                    acceptedFileNames[replaceIndex] = fileName;
                }
                count++;

                if (count >= trainCount) {
                    broadcastMsg(MyConstants.TRAIN, "Sign Count " + count +  "\n array size: " + data.length + ", " + data[0].length + "\n Processing.... Please wait");
                    thread = new MyThread();
                    thread.start();
                } else {
                    broadcastMsg(MyConstants.TRAIN, "Sign Count " + count + "\n array size: " + data.length + ", " + data[0].length + "\n Provide " + (trainCount - count) + " more signatures.");
                }

            } catch (Exception ex) {
                Log.i("onStartCommand", ex.toString());
                stopSelf();
            }

        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("Train Service", "Destroyed");
        if(thread!= null && thread.isAlive())
            thread.interrupt();
        //FileUtil.appendStringToFile(MyConstants.PATH_LOGS, user+"-"+ MyConstants.TRAIN_LOG, trainLog.toString());
        super.onDestroy();

    }

    public class MyThread extends Thread {

        @Override
        public void run() {
            try {
                isThreadRunning = true;
                int i, j;
                if (count == trainCount) {
                    for (i = 0; i < trainCount - 1; i++) {
                        for (j = i + 1; j < trainCount; j++) {
                            broadcastMsg(MyConstants.TRAIN, "Calculating all pair distance\n " + i + "  " + j);
                            distances[i][j] = distances[j][i] = Distance.distances(templates[i], templates[j]);

                        }
                    }

                } else {
                    for (i = 0; i < trainCount; i++) {
                        if (i != replaceIndex) {
                            broadcastMsg(MyConstants.TRAIN, "Calculating distance\n " + replaceIndex + "  " + i);
                            distances[i][replaceIndex] = distances[replaceIndex][i] = Distance.distances(templates[i], templates[replaceIndex]);
                        }
                    }
                }

                broadcastMsg(MyConstants.TRAIN, "Calculating deviations. Please wait... \n ");
                int[] res  = SignVerification.verifyTrain(distances, trainTh);
                replaceIndex = res[0];

                if (replaceIndex > trainCount) {
                    //FileUtil.saveByteArrayToFile(MyConstants.PATH_USERS + "/" + user, MyConstants.TEMPLATES, UtilFunctions.serialize(templates));
                    //FileUtil.saveByteArrayToFile(MyConstants.PATH_USERS + "/" + user, MyConstants.DISTANCES, UtilFunctions.serialize(distances));

                    for (i = 0; i < trainCount; i++) {
                        trainLog.append("train,");
                        trainLog.append(fileName);
                        trainLog.append(",");
                        trainLog.append(res[i+1]/1000.0);
                        trainLog.append(",accepted\n");
                    }
                    broadcastMsg(MyConstants.TRAIN_SUCCESS, "Congratulations!!!\n You have succesfully registered");

                } else {
                    trainLog.append("train,");
                    trainLog.append(fileName);
                    trainLog.append(",");
                    trainLog.append(res[replaceIndex+1]/1000.0);
                    trainLog.append(",rejected\n");
                    broadcastMsg(MyConstants.TRAIN, "Variations in signatures.\n Please provide one more signature.\n ReplaceIndex: " + replaceIndex);
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
        Intent i = new Intent(MyConstants.BCAST_TRAIN);
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
