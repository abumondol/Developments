package data_process;

import classification.DBScan;
import constants_config.EatConfig;
import constants_config.MyConstants;
import entities.Cluster;
import entities.Bite;
import entities.FileDetails;
import java.util.ArrayList;
import m2fedutils.DateTimeUtils;
import m2fedutils.FileUtils;

public class MealManager {

    long lastEmaSent = 0, lastAnalysisTime = 0, lastMealStartTime = 0, lastEmaMealEndTime = 0;
    String watch_id;
    ArrayList<Bite> biteList;

    public MealManager(String wid) {
        watch_id = wid;
        biteList = new ArrayList<>();
    }

    public void addBites(ArrayList<Bite> list) throws Exception {
        System.out.println("Existing Bite List: " + biteList.size());
        for (int i = 0; i < biteList.size(); i++) {
            System.out.println("Bite Time: " + biteList.get(i).time);
        }

        System.out.println("Incoming Bite List: " + list.size());
        for (int i = 0; i < list.size(); i++) {
            System.out.println("Bite Time: " + list.get(i).time);
        }

        int size1 = biteList.size();
        addBitesToListSorted(list);
        System.out.println("Bite List Sizes: " + size1 + ", " + biteList.size());
    }

    public void process() throws Exception {
        long currentTime = System.currentTimeMillis();
        /*if (fd != null) {
            currentTime = fd.file_data_end_time_adjusted;
        }*/

        if (biteList.isEmpty() || currentTime - lastAnalysisTime <= EatConfig.min_analysis_interval || currentTime - lastEmaSent <= EatConfig.min_ema_sent_interval) {
            return;
        }

        lastAnalysisTime = currentTime;

        int i, cluster_count;
        Cluster c;

        ArrayList<Cluster> clusters = DBScan.findClusters(biteList, EatConfig.dbscan_eps, EatConfig.dbscan_min_points);
        cluster_count = clusters.size();
        if (cluster_count == 0) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (i = 0; i < cluster_count - 1; i++) {
            c = clusters.get(i);
            if (c.end_time - c.start_time > EatConfig.min_meal_duration) {
                sb.append(watch_id);
                sb.append(",");
                sb.append(System.currentTimeMillis());
                sb.append(",");
                sb.append(c.start_time);
                sb.append(",");
                sb.append(c.end_time);
                sb.append(",");
                sb.append(c.end_index - c.start_index + 1);
                sb.append("\n");
                System.out.print("Meal: ");

            } 
        }

        c = clusters.get(cluster_count - 1);
        long lastTimeForRemove = c.start_time - 1;

        if (currentTime - c.end_time > EatConfig.min_wait_time_after_meal_to_decide && c.end_time - c.start_time > EatConfig.min_meal_duration && c.end_index-c.start_index+1>=EatConfig.min_meal_bite_count) {
            sb.append(watch_id);
            sb.append(",");
            sb.append(currentTime);
            sb.append(",");
            sb.append(c.start_time);
            sb.append(",");
            sb.append(c.end_time);
            sb.append(",");
            sb.append(c.end_index - c.start_index + 1);
            sb.append("\n");

            if (currentTime - c.end_time < 10 * 60 * 1000) {
                sendEma();
                lastEmaSent = currentTime;
                lastEmaMealEndTime = c.end_time;
            }

            lastTimeForRemove = c.end_time;
        }

        if (sb.length() > 0) {
            FileUtils.appendToResultFile(MyConstants.FILE_TYPE_MEAL, c.start_time, watch_id, sb.toString());
        }

        removeFromBiteList(biteList, lastTimeForRemove);

    }

    void sendEma() throws Exception {
        String data = watch_id + "," + lastEmaSent + "," + lastEmaSent + ",0\n";
        FileUtils.appendToResultFile(MyConstants.FILE_TYPE_EMA, lastEmaSent, watch_id, data);
        System.out.println("Ema Sent: " + DateTimeUtils.getDateTimeString(lastEmaSent, 3));
    }

    void removeFromBiteList(ArrayList<Bite> biteList, long lastTimeForRemove) {
        int n = biteList.size();
        for (int i = n - 1; i >= 0; i--) {
            if (biteList.get(i).time <= lastTimeForRemove) {
                biteList.remove(i);
            }
        }
    }

    public void addBitesToListSorted(ArrayList<Bite> list) throws Exception {
        if (list.isEmpty()) {
            return;
        } else if (biteList.isEmpty()) {
            biteList.addAll(list);
            checkBiteTimeOrder();
            return;
        }

        int count = biteList.size(), count2 = list.size();

        if (biteList.get(count - 1).time < list.get(0).time) {
            biteList.addAll(list);
            checkBiteTimeOrder();
            return;
        } else if (biteList.get(count - 1).time <= list.get(0).time - 1000) {
            list.remove(0);
            biteList.addAll(list);
            checkBiteTimeOrder();
            return;
        } else if (list.get(count2 - 1).time < biteList.get(0).time) {
            biteList.addAll(0, list);
            checkBiteTimeOrder();
            return;
        }

        long t = list.get(count2 - 1).time;
        for (int j = 0; j < biteList.size(); j++) {
            if (t < biteList.get(j).time) {
                biteList.addAll(j, list);
                checkBiteTimeOrder();
                return;
            }
        }

    }

    void checkBiteTimeOrder() {
        System.out.println("New Bite List: " + biteList.size());
        for (int i = 0; i < biteList.size(); i++) {
            System.out.println("Bite Time: " + biteList.get(i).time);
        }

        for (int i = 0; i < biteList.size() - 1; i++) {
            if (biteList.get(i).time >= biteList.get(i + 1).time) {
                System.out.println("Problem is bite order in list");
                System.exit(0);
            }
        }
    }

}
