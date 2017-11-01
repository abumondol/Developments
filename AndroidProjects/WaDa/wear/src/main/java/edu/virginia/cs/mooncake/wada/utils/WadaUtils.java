package edu.virginia.cs.mooncake.wada.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Pattern;

/**
 * Created by TOSHIBA on 1/3/2016.
 */
public class WadaUtils {

    public static String getWatchName(Context context) {
        String name = SharedPrefUtil.getSharedPref(ConstantsUtil.WATCH_NAME, context);
        if (name != null)
            return name;

        String serial = Build.SERIAL;
        String[][] s = ConstantsUtil.WATCH_SERIAL_NAME;
        for (int i = 0; i<s.length;i++){
            if(serial.equals(s[i][0])){
                SharedPrefUtil.putSharedPref(ConstantsUtil.WATCH_NAME, s[i][1], context);
                return s[i][1];
            }
        }

        SharedPrefUtil.putSharedPref(ConstantsUtil.WATCH_NAME, "watchx", context);
        return "watchx";
    }

    public static String getTag(Context context) {
        String[] tag_names = ConstantsUtil.TAG_NAMES;
        String s = SharedPrefUtil.getSharedPref(tag_names[0] + ConstantsUtil.TEMP, context);
        ;
        for (int i = 1; i < tag_names.length; i++) {
            s += "-" + SharedPrefUtil.getSharedPref(tag_names[i] + ConstantsUtil.TEMP, context);
        }

        return s;

    }

    public static String[][] defaultConfig() {
        String[][] config = {
                {"subject1", "subject2", "subject3", "subject4", "subject5", ConstantsUtil.NULL},
                {"activity1", "activity2", "activity3", "activity4", "activity5", ConstantsUtil.NULL},
                {"right", "left", "waist", "pendant", "chest", ConstantsUtil.NULL},
                {ConstantsUtil.NULL},
                {"sit", "stand", "lie", "stand", "walk", ConstantsUtil.NULL},
                {ConstantsUtil.NULL}
        };

        return config;
    }

    public static String[] tagList(int index) {
        String[][] config = readConfig();
        if (config == null) {
            config = defaultConfig();
        }
        return config[index];
    }


    public static boolean isConfigAvailable() {
        String folderName = Environment.getExternalStorageDirectory() + "/wada/config";
        File file = new File(folderName);
        if (file.exists() == false) {
            file.mkdirs();
        }

        String filePath = folderName + "/config.txt";
        file = new File(filePath);
        return file.exists();
    }

    public static String[][] readConfig() {
        String folderName = Environment.getExternalStorageDirectory() + "/wada/config";
        File file = new File(folderName);
        if (file.exists() == false) {
            file.mkdirs();
        }

        String filePath = folderName + "/config.txt";
        file = new File(filePath);
        if (file.exists() == false) {
            return null;
        }

        String[] tagNames = ConstantsUtil.TAG_NAMES;
        String[][] config = defaultConfig();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line, s;
            String[] str;
            String[] tags;
            int linecount = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0) {
                    linecount++;
                    if (linecount > 6)
                        return null;

                    if(line.startsWith("#") || line.startsWith("%"))
                        continue;

                    Pattern p = Pattern.compile("[^a-zA-Z0-9_ ,]");
                    boolean hasOtherChar = p.matcher(line).find();
                    if (hasOtherChar) {
                        return null;
                    }

                    str = line.split(",");
                    for (int i = 0; i < tagNames.length; i++) {
                        if (str[0].toLowerCase().equals(tagNames[i].toLowerCase())) {
                            tags = new String[str.length];
                            for (int j = 1; j < str.length; j++) {
                                s = str[j].trim();
                                if (s.length() == 0)
                                    tags[j - 1] = ConstantsUtil.NULL;
                                else
                                    tags[j - 1] = s.replace(" ","_");
                            }

                            tags[str.length - 1] = ConstantsUtil.NULL;
                            config[i] = tags;
                            break;
                        }
                    }

                }
            }

            br.close();

        } catch (Exception ex) {
            Log.i("Error", "Reading config.txt");
            return null;
        }

        return config;
    }

    public static boolean deleteConfig() {
        String filePath = Environment.getExternalStorageDirectory() + "/wada/config/config.txt";
        File file = new File(filePath);
        return file.delete();
    }


}
