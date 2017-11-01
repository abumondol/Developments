package m2fedutils;

import entities.Bite;
import java.util.ArrayList;

public class ResultStringUtils {

    public static String biteListToString(ArrayList<Bite> biteList, String watch_id, long upload_time) throws Exception {
        if (biteList.isEmpty()) {
            return null;
        }

        Bite b;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < biteList.size(); i++) {
            b = biteList.get(i);
            sb.append(watch_id);
            sb.append(",");
            sb.append(upload_time);
            sb.append(",");
            sb.append(b.time);
            sb.append(",");
            sb.append(b.type);
            sb.append(",");
            sb.append(b.confidence);
            sb.append("\n");
        }
        
        return sb.toString();

        
    }

}
