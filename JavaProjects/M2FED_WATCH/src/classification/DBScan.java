package classification;

import entities.Cluster;
import entities.Bite;
import java.util.ArrayList;
import constants_config.MyConstants;

public class DBScan {

    public static ArrayList<Cluster> findClusters(ArrayList<Bite> biteList, long eps, int minPts) {
        ArrayList<Cluster> clusterList = new ArrayList<Cluster>();
        findEpsNeighbors(biteList, eps, minPts);
        int i, j, a, si, ei;
        
        //for (i= 0; i < biteList.size(); i++) {
            //System.out.println(biteList.get(i).dbscan_point_type);
        //}        
        
        si = -1;
        for (i = 0; i < biteList.size(); ) {
            if (biteList.get(i).dbscan_point_type != MyConstants.DBSCAN_CORE) {
                i++;
                continue;
            }

            if (si == -1) {
                si = biteList.get(i).dbscan_start_index;
            }
            
            i = biteList.get(i).dbscan_end_index;
            a = -1;
            for (j = i + 1; j < biteList.get(i).dbscan_end_index; j++) {
                if (biteList.get(j).dbscan_point_type == MyConstants.DBSCAN_CORE) {
                    a = j;
                }
            }
            
            if(a==-1){
                ei = biteList.get(i).dbscan_end_index;                
                Cluster c = new Cluster();
                c.start_index = si;
                c.end_index = ei;
                c.start_time = biteList.get(si).time;
                c.end_time = biteList.get(ei).time;
                clusterList.add(c);
                
                i = ei + 1;
                si = -1;
                
            }else{
                i = a;                
            }
            
        }

        return clusterList;
    }

    static void findEpsNeighbors(ArrayList<Bite> biteList, long eps, int minPts) {
        Bite b;
        int i, j;
        for (i = 0; i < biteList.size(); i++) {
            b = biteList.get(i);
            for (j = i + 1; j < biteList.size() && biteList.get(j).time <= b.time + eps; j++);
            b.dbscan_end_index = j - 1;

            for (j = i - 1; j >= 0 && biteList.get(j).time >= b.time - eps; j--);
            b.dbscan_start_index = j + 1;
            
            //System.out.println(b.dbscan_end_index - b.dbscan_start_index);

            if (b.dbscan_end_index - b.dbscan_start_index >= minPts) {
                b.dbscan_point_type = MyConstants.DBSCAN_CORE;
            }
        }

    }

}
