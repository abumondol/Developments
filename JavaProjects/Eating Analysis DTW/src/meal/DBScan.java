package meal;

import entities.Bite;
import java.util.ArrayList;
import entities.Meal;
import entities.Pattern;

public class DBScan {

    public static int POINT_TYPE_NOISE = 0;
    public static int POINT_TYPE_REACHABLE = 1;
    public static int POINT_TYPE_CORE = 2;

    public static ArrayList<Meal> findClustersFromPat(Pattern[] pats, long eps, int minPts) {
        ArrayList<Bite> biteList = new ArrayList<Bite>();
        for (int i = 0; i < pats.length; i++) {
            Bite b = new Bite();
            b.pattern = pats[i];
            biteList.add(b);
        }

        return findClustersFromBite(biteList, eps, minPts);
    }

    public static ArrayList<Meal> findClustersFromBite(ArrayList<Bite> biteList, long eps, int minPts) {

        ArrayList<Meal> mealList = new ArrayList<>();
        findEpsNeighbors(biteList, eps, minPts);
        int i, j, a, si, ei;
        Meal m;

        //for (i= 0; i < biteList.size(); i++) {
        //System.out.println(biteList.get(i).dbscan_point_type);
        //}        
        si = -1;
        for (i = 0; i < biteList.size();) {
            if (biteList.get(i).dbscan_point_type != POINT_TYPE_CORE) {
                i++;
                continue;
            }

            if (si == -1) {
                si = biteList.get(i).dbscan_start_index;
            }

            i = biteList.get(i).dbscan_end_index;
            a = -1;
            for (j = i + 1; j < biteList.get(i).dbscan_end_index; j++) {
                if (biteList.get(j).dbscan_point_type == POINT_TYPE_CORE) {
                    a = j;
                }
            }

            if (a == -1) {
                ei = biteList.get(i).dbscan_end_index;
                m = new Meal();
                m.startIndex = si;
                m.endIndex = ei;
                m.startTime = biteList.get(si).pattern.minPointTime;
                m.endTime = biteList.get(ei).pattern.minPointTime;                
                mealList.add(m);

                i = ei + 1;
                si = -1;

            } else {
                i = a;
            }

        }
        
        return mealList;
    }

    static void findEpsNeighbors(ArrayList<Bite> biteList, long eps, int minPts) {
        Bite b;
        int i, j;
        for (i = 0; i < biteList.size(); i++) {
            b = biteList.get(i);
            for (j = i + 1; j < biteList.size() && biteList.get(j).pattern.minPointTime <= b.pattern.minPointTime + eps; j++);
            b.dbscan_end_index = j - 1;

            for (j = i - 1; j >= 0 && biteList.get(j).pattern.minPointTime >= b.pattern.minPointTime - eps; j--);
            b.dbscan_start_index = j + 1;

            //System.out.println(b.dbscan_end_index - b.dbscan_start_index);
            if (b.dbscan_end_index - b.dbscan_start_index >= minPts) {
                b.dbscan_point_type = POINT_TYPE_CORE;
            }
        }

    }

}
