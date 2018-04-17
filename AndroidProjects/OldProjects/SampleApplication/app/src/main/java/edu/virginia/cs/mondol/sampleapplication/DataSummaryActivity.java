package edu.virginia.cs.mondol.sampleapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class DataSummaryActivity extends Activity {

    Button buttonCalculate, buttonDelete;
    TextView tvCount, tvMean, tvMessage, tvStdev;
    boolean isCalculating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_summary);
        buttonCalculate = (Button) findViewById(R.id.buttonCalculate);
        buttonDelete = (Button) findViewById(R.id.buttonDelete);
        tvCount = (TextView) findViewById(R.id.textViewCount);
        tvMean = (TextView) findViewById(R.id.textViewMean);
        tvMessage = (TextView) findViewById(R.id.textViewMessage);
        tvStdev = (TextView) findViewById(R.id.textViewStdev);

        String folderName = Environment.getExternalStorageDirectory() + "/Sample App";
        String filePath = folderName + "/sensor_data.csv";
        File file = new File(filePath);
        if (file.exists() == false) {
            tvMessage.setText("Data file doesn't exist");
            buttonCalculate.setEnabled(false);
            buttonDelete.setEnabled(false);
        }
    }

    public void buttonClicked(View v) {
        if (v.getId() == R.id.buttonCalculate) {
            // Computation intensive works should run on UI thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String folderName = Environment.getExternalStorageDirectory() + "/Sample App";
                    String filePath = folderName + "/sensor_data.csv";
                    File file = new File(filePath);
                    if (file.exists() == false) {
                        tvMessage.setText("Data file doesn't exist");
                        return;
                    }

                    try {
                        float[][] d = readData(file);
                        calculateSummary(d);

                    } catch (Exception ex) {
                        Log.e("File read error", ex.toString());
                        tvMessage.setText("File read error!");
                    }


                }
            });

        } else if (v.getId() == R.id.buttonDelete) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String folderName = Environment.getExternalStorageDirectory() + "/Sample App";
                    String filePath = folderName + "/sensor_data.csv";
                    File file = new File(filePath);
                    if (file.exists() == false) {
                        tvMessage.setText("Data file doesn't exist");
                        return;
                    }

                    if (file.delete()) {
                        tvMessage.setText("Delete Succesful.");
                    } else {
                        tvMessage.setText("Delete Failed.");
                    }
                }
            });

        } else if (v.getId() == R.id.buttonClose) {
            this.finish();
        }
    }


    public static float[][] readData(File file) throws Exception {
        // reads the sensor_data.csv file and return the data in array. The time stamp values are not used.
        ArrayList<float[]> list = new ArrayList<float[]>();
        String line;
        String[] str;
        float[] d;

        BufferedReader br = new BufferedReader(new FileReader(file));
        while ((line = br.readLine()) != null) {
            str = line.split(",");
            d = new float[4];
            d[0] = Float.parseFloat(str[1]);
            d[1] = Float.parseFloat(str[2]);
            d[2] = Float.parseFloat(str[3]);
            d[3] = Float.parseFloat(str[4]);
            list.add(d);
        }

        int sampleCount = list.size();
        float[][] data = new float[sampleCount][];
        for (int i = 0; i < sampleCount; i++)
            data[i] = list.get(i);

        return data;
    }

    public void calculateSummary(float[][] data) { //calculates the summary (count and mean) of the sensor values
        int accelCount = 0, gyroCount = 0, magnetCount=0;
        float accelMeanX = 0, accelMeanY = 0, accelMeanZ = 0, gyroMeanX = 0, gyroMeanY = 0, gyroMeanZ = 0, magnetMeanX=0, magnetMeanY=0, magnetMeanZ=0;
        float accelStdevX = 0, accelStdevY = 0, accelStdevZ = 0, gyroStdevX = 0, gyroStdevY = 0, gyroStdevZ = 0, magnetStdevX=0, magnetStdevY=0, magnetStdevZ=0;

        for (int i = 0; i < data.length; i++) {
            if (data[i][0] == 1) { //accelerometer
                accelCount++;
                accelMeanX += data[i][1];
                accelMeanY += data[i][2];
                accelMeanZ += data[i][3];

            } else if (data[i][0] == 4) { //gyroscope
                gyroCount++;
                gyroMeanX += data[i][1];
                gyroMeanY += data[i][2];
                gyroMeanZ += data[i][3];

            }else if (data[i][0] == 2) { //magnetometer
                magnetCount++;
                magnetMeanX += data[i][1];
                magnetMeanY += data[i][2];
                magnetMeanZ += data[i][3];
            }
        }

        accelMeanX = accelMeanX / accelCount;
        accelMeanY = accelMeanY / accelCount;
        accelMeanZ = accelMeanZ / accelCount;
        gyroMeanX = gyroMeanX / gyroCount;
        gyroMeanY = gyroMeanY / gyroCount;
        gyroMeanZ = gyroMeanZ / gyroCount;
        magnetMeanX = magnetMeanX / magnetCount;
        magnetMeanY = magnetMeanY / magnetCount;
        magnetMeanZ = magnetMeanZ / magnetCount;

        for (int i = 0; i < data.length; i++) {
            if (data[i][0] == 1) { //accelerometer
                accelStdevX += (accelMeanX - data[i][1])*(accelMeanX - data[i][1]);
                accelStdevY += (accelMeanY - data[i][2])*(accelMeanY - data[i][2]);
                accelStdevZ += (accelMeanZ - data[i][3])*(accelMeanZ - data[i][3]);

            } else if (data[i][0] == 4) { //gyroscope
                gyroStdevX += (gyroMeanX - data[i][1])*(gyroMeanX - data[i][1]);
                gyroStdevY += (gyroMeanY - data[i][2])*(gyroMeanY - data[i][2]);
                gyroStdevZ += (gyroMeanZ - data[i][3])*(gyroMeanZ - data[i][3]);

            }else if (data[i][0] == 2) { //magnetometer
                magnetStdevX += (magnetMeanX - data[i][1])*(magnetMeanX - data[i][1]);
                magnetStdevY += (magnetMeanY - data[i][2])*(magnetMeanY - data[i][2]);
                magnetStdevZ += (magnetMeanZ - data[i][3])*(magnetMeanZ - data[i][3]);
            }
        }

        accelStdevX = (float)Math.sqrt(accelStdevX / accelCount);
        accelStdevY = (float)Math.sqrt(accelStdevY / accelCount);
        accelStdevZ = (float)Math.sqrt(accelStdevZ / accelCount);
        gyroStdevX = (float)Math.sqrt(gyroStdevX / gyroCount);
        gyroStdevY = (float)Math.sqrt(gyroStdevY / gyroCount);
        gyroStdevZ = (float)Math.sqrt(gyroStdevZ / gyroCount);
        magnetStdevX = (float)Math.sqrt(magnetStdevX / magnetCount);
        magnetStdevY = (float)Math.sqrt(magnetStdevY / magnetCount);
        magnetStdevZ = (float)Math.sqrt(magnetStdevZ / magnetCount);

        tvMessage.setText("Data Summary:");
        tvCount.setText(String.format("Count::  accel:%d, gyro:%d, magnet:%d", accelCount, gyroCount, magnetCount));
        tvMean.setText(String.format("Mean:: accel: %.2f, %.2f, %.2f;  gyro: %.2f, %.2f, %.2f, magnet: %.2f, %.2f, %.2f", accelMeanX, accelMeanY, accelMeanZ, gyroMeanX, gyroMeanY, gyroMeanZ, magnetMeanX, magnetMeanY, magnetMeanZ));
        tvStdev.setText(String.format("Standard Deviation:: accel: %.2f, %.2f, %.2f;  gyro: %.2f, %.2f, %.2f, magnet: %.2f, %.2f, %.2f", accelStdevX, accelStdevY, accelStdevZ, gyroStdevX, gyroStdevY, gyroStdevZ, magnetStdevX, magnetStdevY, magnetStdevZ));
    }


}
