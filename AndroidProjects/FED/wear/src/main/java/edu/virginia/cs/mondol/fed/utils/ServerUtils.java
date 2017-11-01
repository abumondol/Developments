package edu.virginia.cs.mondol.fed.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class ServerUtils {
    static final String TAG = FedConstants.MYTAG;
    static int timeout = 10000;

    public static String getUrlString(Context context, String path) {
        String server_ip = SharedPrefUtil.getSharedPref(FedConstants.SERVER_IP, context);
        return "http://" + server_ip + "/m2fed_watch/" + path;
    }


    public static boolean isConnected(Context context) {
        Log.i(FedConstants.MYTAG, "starting connection test: ");
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                //URL url = new URL(FedConstants.SERVER_URL_CONNECTION_TEST);
                URL url = new URL(getUrlString(context, FedConstants.SERVER_URL_CONNECTION_TEST));
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


    public static void uploadFiles(Context context, File[] files) {
        /*if (!isConnected(context)) {
            Log.i(TAG, "Network connection test failed");
            return;
        } else {
            Log.i(TAG, "Network is connected");
        }*/

        String uploadURLString = getUrlString(context, FedConstants.SERVER_URL_UPLOAD_FILE);
        int uploadCode = 0;
        for (int i = 0; i < files.length; i++) {
            if (files[i] == null) {
                continue;
            }

            Log.i(FedConstants.MYTAG, "Uploadling file (" + (i + 1) + "/" + files.length + "): " + files[i].getName());
            uploadCode = uploadFile(files[i], uploadURLString);
            Log.i(FedConstants.MYTAG, "Upload Code: " + uploadCode);
            if (uploadCode > 0)
                files[i].delete();
        }

        SharedPrefUtil.putSharedPref(FedConstants.LAST_UPLOAD_ATTEMPT_RESULT, "" + uploadCode, context);

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


    public static String getData(Context context, int download_type, String message) {
        long callTime, responseTime;
        HttpURLConnection conn = null;
        String urlString;
        if (download_type == FedConstants.DOWNLOAD_TYPE_TIME) {
            urlString = getUrlString(context, FedConstants.SERVER_URL_DOWNLOAD_TIME);
        } else if (download_type == FedConstants.DOWNLOAD_TYPE_PARAMS) {
            urlString = getUrlString(context, FedConstants.SERVER_URL_DOWNLOAD_PARAMS);
        } else {
            return null;
        }

        try {

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("watch_id", Build.SERIAL)
                    .appendQueryParameter("message", message);
            String query = builder.build().getEncodedQuery();

            URL u = new URL(urlString);
            conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            //c.setRequestProperty("Content-length", "0");
            //c.setUseCaches(false);
            //c.setAllowUserInteraction(false);
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);

            callTime = System.currentTimeMillis();
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            conn.connect();
            int status = conn.getResponseCode();
            responseTime = System.currentTimeMillis();
            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (download_type == FedConstants.DOWNLOAD_TYPE_TIME) {
                            sb.append(line);
                            break;
                        }
                        sb.append(line + "\n");
                    }
                    br.close();
                    //Log.i("Get Data Succesful",sb.toString());
                    if (download_type == FedConstants.DOWNLOAD_TYPE_TIME) {
                        sb.append(":");
                        sb.append(callTime);
                        sb.append(":");
                        sb.append(responseTime);

                    }
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            Log.i("Download Data Exception", ex.toString());
        } catch (IOException ex) {
            Log.i("Download Data Exception", ex.toString());
        } finally {
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception ex) {
                    Log.i("Get Data Ex closing", ex.toString());
                }
            }
        }

        return null;

    }

}
