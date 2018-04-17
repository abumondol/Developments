package edu.virginia.cs.mooncake.airsign.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class NetUtil {

    public static JSONObject checkLogin(Context context, String phoneNo,
                                        String pin) {
        if (!isConnected(context))
            return null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(ConstantsUtil.SERVER_URL_LOGIN);

            List<NameValuePair> nvp = new ArrayList<NameValuePair>();
            nvp.add(new BasicNameValuePair("phoneNo", phoneNo));
            nvp.add(new BasicNameValuePair("pin", pin));
            httppost.setEntity(new UrlEncodedFormEntity(nvp));

            HttpResponse response = httpclient.execute(httppost);
            if (response != null) {
                HttpEntity entity = response.getEntity();
                // Read the content stream
                InputStream instream = entity.getContent();
                String res = convertStreamToString(instream);
                return new JSONObject(res);
            }
        } catch (Exception ex) {
            Log.e("result", "caught " + ex.toString());
            return null;
        }
        return null;
    }


    public static JSONObject createAccount(Context context, String phoneNo,
                                           String pin, int yearOfBirth, String gender) {
        if (!isConnected(context))
            return null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(ConstantsUtil.SERVER_URL_LOGIN);

            List<NameValuePair> nvp = new ArrayList<NameValuePair>();
            nvp.add(new BasicNameValuePair("phoneNo", phoneNo));
            nvp.add(new BasicNameValuePair("pin", pin));
            nvp.add(new BasicNameValuePair("yearOfBirth", "" + yearOfBirth));
            nvp.add(new BasicNameValuePair("gender", gender));
            httppost.setEntity(new UrlEncodedFormEntity(nvp));

            HttpResponse response = httpclient.execute(httppost);
            if (response != null) {
                HttpEntity entity = response.getEntity();
                // Read the content stream
                InputStream instream = entity.getContent();
                String res = convertStreamToString(instream);
                return new JSONObject(res);
            }
        } catch (Exception ex) {
            Log.e("result", "caught " + ex.toString());
            return null;
        }
        return null;
    }

    public static void sendData(Context context) {

    }


    /*public static void sendData(Context context) {
        if (!isConnected(context)) {
            Log.i("connection test", "failed");
            return;
        }
        String result = "";
        try {
            MySQLiteOpenHelper sqlHelper = MySQLiteOpenHelper
                    .getInstance(context);
            JSONObject json;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(ConstantsUtil.SERVER_URL_INSERT);
            Log.i("Send Data", "Started");

            long time = 100;
            while (time > 0) {
                json = DBUtil.getData(sqlHelper);
                if (json == null) {
                    Log.i("Send Data", "No more Data");
                    return;
                } else {
                    Log.i("Send Data", "Got Json: Length is "
                            + json.toString().length());
                }

                time = json.getLong(ConstantsUtil.JSON_LAST_TIME);

                int userId = SharedPrefUtil.getSharedPrefInt(
                        ConstantsUtil.SPK_USERID, context);
                json.put(ConstantsUtil.JSON_USERID, userId);

                List<NameValuePair> nvp = new ArrayList<NameValuePair>();
                nvp.add(new BasicNameValuePair(ConstantsUtil.SERVER_NVP_JSON,
                        json.toString()));
                httppost.setEntity(new UrlEncodedFormEntity(nvp));
                HttpResponse response = httpclient.execute(httppost);

                if (response != null) {
                    HttpEntity entity = response.getEntity();
                    InputStream instream = entity.getContent();
                    String res = convertStreamToString(instream);

                    if (res != null)
                        Log.i("res", "not null : " + res + "," + res.length());
                    else {
                        Log.i("res", "null");
                        return;
                    }

                    JSONObject resultJson = new JSONObject(res);
                    result = resultJson.getString(ConstantsUtil.JSON_RESULT);
                    Log.i("result from server", result);
                    if (result.equals("ok")) {
                        Log.i("result", "ok");
                        DBUtil.deleteData(sqlHelper, time);
                    } else {
                        Log.i("result", "not ok");
                        break;
                    }
                }
            }

        } catch (Exception e) {
            Log.e("result", "caught:" + e.toString());
        }
    }*/

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = "";
        try {
            /*
             * while ((line = reader.readLine()) != null) { sb.append(line +
			 * "\n"); }
			 */
            line = reader.readLine();
            sb.append(line);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("Inside convert stream", "Caught");
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (line.equals(""))
            return null;
        return sb.toString();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                URL url = new URL(ConstantsUtil.SERVER_URL_CONNECTION_TEST);
                HttpURLConnection urlc = (HttpURLConnection) url
                        .openConnection();
                urlc.setRequestProperty("User-Agent", "test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(10000); // mTimeout is in milliseconds
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            } catch (IOException e) {
                Log.i("warning", "Error checking internet connection", e);
                return false;
            }
        }
        return false;
    }

	/*
     * private boolean isNetworkConnected(Context context) { ConnectivityManager
	 * cm = (ConnectivityManager) context
	 * .getSystemService(Context.CONNECTIVITY_SERVICE); NetworkInfo ni =
	 * cm.getActiveNetworkInfo(); if (ni == null) { return false; } else return
	 * true; }
	 */

    public static void uploadFiles(Context context) {
        if(!isConnected(context))
            return;
        String folderName = Environment.getExternalStorageDirectory() + "/airsign";
        File file = new File(folderName);
        if (file.exists() == false) {
            return;
        }

        String[] files = file.list();
        for (int i = 0; i < files.length; i++) {
            file = new File(folderName, files[i]);
            if (uploadFile(file, ConstantsUtil.SERVER_URL_UPLOAD_FILE) == 200)
                file.delete();
        }
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
            Log.i("BytesAvailable", "" + bytesAvailable);

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            Log.i("BufferSize", "" + bufferSize);
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

            Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage
                    + ": " + serverResponseCode);

            if (serverResponseCode == 200) {

                String msg = "File Upload Completed."
                        + fileName;

                Log.i("Upload Response", msg);

            }

            // close the streams //
            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (Exception e) {
            Log.e("Upload file to server Exception",
                    "Exception : " + e.getMessage(), e);
            serverResponseCode = 0;
        }

        return serverResponseCode;

    }
}
