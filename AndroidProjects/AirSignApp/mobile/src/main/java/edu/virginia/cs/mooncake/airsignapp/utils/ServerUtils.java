package edu.virginia.cs.mooncake.airsignapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


public class ServerUtils {
    static final String TAG = MyConstants.MYTAG;
    static int timeout = 10000;


    public static boolean isConnected(Context context) {
        Log.i(MyConstants.MYTAG, "starting connection test: ");
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                //URL url = new URL(MyConstants.SERVER_URL_CONNECTION_TEST);
                URL url = new URL(MyConstants.SERVER_URL_CONNECTION_TEST);
                URLConnection urlc = url.openConnection();
                //urlc.setRequestProperty("User-Agent", "test");
                //urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(10000); // mTimeout is in milliseconds
                urlc.connect();
                Log.i(TAG, "Sever connection test passed");
                return true;

            } catch (IOException e) {
                Log.i(TAG, "Error checking internet connection", e);
                return false;
            }

        } else {
            Log.i(TAG, "Network is not connected");
        }
        return false;
    }

    public static int uploadFile(File sourceFile, String upLoadServerUri) {
        String fileName = sourceFile.getName();
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        int serverResponseCode = 0;
        int returnCode = 0;
        try {
            // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(upLoadServerUri);

            // Open a HTTP connection to the URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", fileName);

            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                    + fileName + "\"" + lineEnd);

            dos.writeBytes(lineEnd);

            // create a buffer of maximum size
            bytesAvailable = fileInputStream.available();
            Log.i(TAG, "BytesAvailable: " + bytesAvailable);

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            Log.i(TAG, "BufferSize " + bufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.i(TAG, "uploadFile, " + "HTTP Response is : " + serverResponseMessage
                    + ": " + serverResponseCode);

            if (serverResponseCode == 200) {
                if (serverResponseMessage.equals("error")) {
                    returnCode = -3;
                } else {
                    returnCode = 200;
                }

            } else {
                returnCode = -2;
            }

            // close the streams //
            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (Exception e) {
            Log.e(TAG, "Upload Exception: " + e.getMessage(), e);
            returnCode = -1;
        }

        return returnCode;

    }

}
