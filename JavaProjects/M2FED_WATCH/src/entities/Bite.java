package entities;

/**
 *
 * @author mm5gg
 */
public class Bite {

    public int index;
    public long time;
    public int type = 0;
    public float confidence = 0;
    public String watch_id;
    public boolean gt = false;

    public int pattern_index;
    public int distance;
    public int minx;

    public int dbscan_point_type;
    public int dbscan_start_index;
    public int dbscan_end_index;

}
