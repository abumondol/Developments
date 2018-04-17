package edu.virginia.cs.mondol.emainitiator;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class MyIntentService extends IntentService {
    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            try {
                // Construct data
                String data = URLEncoder.encode("watch_id", "UTF-8") + "=" + URLEncoder.encode("14402D24F306CE8", "UTF-8");
                //data += "&" + URLEncoder.encode("key2", "UTF-8") + "=" + URLEncoder.encode("value2", "UTF-8");
                Toast.makeText(this, "Service Called", Toast.LENGTH_SHORT);
                // Send data
                URL url = new URL("http://192.168.0.100/m2fed_watch/ema_init.php");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                Toast.makeText(this, "Data send Called", Toast.LENGTH_SHORT);

                // Get the response
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    Toast.makeText(this, line.substring(0, Math.min(line.length(), 200)), Toast.LENGTH_SHORT);
                    break;
                }
                wr.close();
                rd.close();
            } catch (Exception e) {
                Log.i("Server", e.toString());
            }
        }

    }


}
