package distance_finder;

import java.io.File;
import myutils.MyArrayUtils;
import java.util.ArrayList;
import myutils.MyFileUtils;
import myutils.MyMathUtils;

/**
 * * @author mm5gg
 */
public class DistanceFinderLeftRight {

    public static String[] dtw_type_names = {"dist_accel_euclid_1d", "dist_accel_euclid_3d", "dist_acn_euclid_1d", "dist_acn_euclid_3d", "dist_acn_cosine", "dist_acn_radian"};
    public float[][][] accel = new float[52][][];
    public float[][][] acn = new float[52][][];
    public int[][][] segspos = new int[36][][];
    public int[][][] segsneg = new int[52][][];
    float[][][] accel_acn;

    int left_max = 64, right_max = 64;
    //int total_max = left_max + right_max - 1;
    float[][] d = new float[left_max][left_max];
    float[][] D = new float[left_max][left_max];

    String srcFolder = "mydata";//"C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\MyMatlab\\Eating\\eating_dtw\\data\\";
    String destFolder = "myresults\\distances_left_right";

    public DistanceFinderLeftRight() throws Exception {

        for (int i = 0; i < 52; i++) {
            System.out.println("reading data... " + i);
            accel[i] = MyArrayUtils.stringArrayToFloatArray(MyFileUtils.readCSV(srcFolder + "\\accel_data\\accel_" + i, true));
            acn[i] = MyArrayUtils.normalize_data(accel[i]);
            segsneg[i] = MyArrayUtils.stringArrayToIntArray(MyFileUtils.readCSV(srcFolder + "\\segment_info\\segsneg_" + i, true));
            if (i < segspos.length) {
                segspos[i] = MyArrayUtils.stringArrayToIntArray(MyFileUtils.readCSV(srcFolder + "\\segment_info\\segspos_" + i, true));
            }
        }

    }

    public void findDistancesForType(int dtw_type, int other_data_type) throws Exception {

        if (dtw_type < 2) {
            accel_acn = accel;
        } else {
            accel_acn = acn;
        }

        String destFile = "", destSubFolder;
        File f;
        float[][] res;

        destSubFolder = destFolder + "\\" + dtw_type_names[dtw_type];
        f = new File(destSubFolder);
        if (!f.exists()) {
            f.mkdirs();
        }

        for (int sid = 0; sid < segspos.length; sid++) {
            res = findDistancesforSubject(sid, dtw_type, other_data_type);

            switch (other_data_type) {
                case 0:
                    destFile = destSubFolder + "\\pos_pos_" + sid + ".csv";
                    break;
                case 1:
                    destFile = destSubFolder + "\\pos_neg_" + sid + ".csv";
                    break;
                case 2:
                    destFile = destSubFolder + "\\pos_neg2_" + sid + ".csv";
                    break;
                default:
                    System.out.println("Error in other data type: " + other_data_type);
                    System.exit(0);
            }

            MyFileUtils.writeToCSVFile(destFile, res, null);
        }
    }

    public float[][] findDistancesforSubject(int sid, int dtw_type, int other_data_type) {

        ArrayList<float[]> res = new ArrayList<>();
        float[] row;

        int i, j, k, s, ix, ix2, label, label2, s1, e1, sub_count, seg_count, seg_count2;
        float minx, minx2;
        int[][][] segs2;

        if (other_data_type == 0) {
            segs2 = segspos;
        } else {
            segs2 = segsneg;
        }

        seg_count = segspos[sid].length;
        for (i = 0; i < seg_count; i++) {
            System.out.print("\n" + dtw_type_names[dtw_type] + "-" + other_data_type + " :: " + (sid + 1) + ", " + (i + 1) + "/" + seg_count + " > ");

            ix = segspos[sid][i][0];
            label = segspos[sid][i][1];
            minx = accel[sid][ix][0];

            s = 0;
            sub_count = segspos.length;
            if (other_data_type == 2) {
                s = segspos.length;
                sub_count = segs2.length;
            }

            for (; s < sub_count; s++) {
                System.out.print(s + " ");
                seg_count2 = segs2[s].length;
                for (j = 0; j < seg_count2; j++) {
                    ix2 = segs2[s][j][0];
                    label2 = segs2[s][j][1];
                    minx2 = accel[s][ix2][0];

                    row = new float[20];
                    row[0] = sid;
                    row[1] = i;
                    row[2] = ix;
                    row[3] = minx;
                    row[4] = label;

                    row[5] = s;
                    row[6] = j;
                    row[7] = ix2;
                    row[8] = minx2;
                    row[9] = label2;

                    k = 10;
                    d = distanceMatrix(accel_acn[sid], ix - left_max + 1, ix, accel_acn[s], ix2 - left_max + 1, ix2, dtw_type, d);
                    D = findDistance(d, D, true);
                    for (s1 = 31; s1 < d.length; s1 += 8) {
                        row[k] = D[s1][s1];
                        k++;
                    }

                    d = distanceMatrix(accel_acn[sid], ix, ix + right_max - 1, accel_acn[s], ix2, ix2 + right_max - 1, dtw_type, d);
                    D = findDistance(d, D, false);
                    for (s1 = 31; s1 < d.length; s1 += 8) {
                        row[k] = D[s1][s1];
                        k++;
                    }
                    res.add(row);
                }
            }

        }

        System.out.println("Size: " + res.size());
        return MyArrayUtils.floatArrayListToArray(res);
    }

    public float[][] distanceMatrix(float[][] t, int s1, int e1, float[][] r, int s2, int e2, int type, float[][] d) {

        if (e1 - s1 != e2 - s2) {
            System.out.println("N is not equal to M for DTW.");
            System.exit(0);
        }

        float dx, dy, dz;
        int n, m, i, j;

        switch (type) {
            case 0:
            case 2:
                int len1 = d.length;
                int len2 = d[0].length;
                float[][] ds = new float[len1][len2];

                for (int axis = 0; axis < 3; axis++) {
                    d = distanceMatrixOneAxis(t, s1, e1, r, s2, e2, d, axis);
                    for (int a = 0; a < len1; a++) {
                        for (int b = 0; b < len2; b++) {
                            ds[a][b] += d[a][b];
                        }
                    }
                }

                d = ds;
                break;

            case 1:
            case 3:
                for (n = 0, i = s1; i <= e1; n++, i++) {
                    for (m = 0, j = s2; j <= e2; m++, j++) {
                        dx = t[i][0] - r[j][0];
                        dy = t[i][1] - r[j][1];
                        dz = t[i][2] - r[j][2];
                        d[n][m] = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
                    }
                }
                break;

            case 4:
                for (n = 0, i = s1; i <= e1; n++, i++) {
                    for (m = 0, j = s2; j <= e2; m++, j++) {
                        d[n][m] = 1 - (t[i][0] * r[j][0] + t[i][1] * r[j][1] + t[i][2] * r[j][2]);
                    }
                }
                break;

            case 5:
                for (n = 0, i = s1; i <= e1; n++, i++) {
                    for (m = 0, j = s2; j <= e2; m++, j++) {
                        d[n][m] = (float) Math.acos(t[i][0] * r[j][0] + t[i][1] * r[j][1] + t[i][2] * r[j][2]);
                    }
                }
                break;

            default:
                System.out.println("Error type in distance: " + type);
                System.exit(0);
        }

        return d;
    }

    public float[][] distanceMatrixOneAxis(float[][] t, int s1, int e1, float[][] r, int s2, int e2, float[][] d, int axis) {

        if (e1 - s1 != e2 - s2) {
            System.out.println("N is not equal to M for DTW.");
            System.exit(0);
        }

        int n, m, i, j;
        for (n = 0, i = s1; i <= e1; n++, i++) {
            for (m = 0, j = s2; j <= e2; m++, j++) {
                d[n][m] = Math.abs(t[i][axis] - r[j][axis]);
            }
        }

        return d;
    }

    public float[][] findDistance(float[][] d, float[][] D, boolean reverse) {
        int s = 0;
        int e = d.length - 1;
        float[][] temp = d.clone();

        if (reverse) {
            for (int i = 0; i < e; i++) {
                for (int j = 0; j < e; j++) {
                    d[i][j] = temp[e - i][e - j];
                }
            }
        }

        D[s][s] = d[s][s];

        int n, m;
        for (n = s + 1; n <= e; n++) {
            D[n][s] = d[n][s] + D[n - 1][s];
        }

        for (m = s + 1; m <= e; m++) {
            D[s][m] = d[s][m] + D[s][m - 1];
        }

        for (n = s + 1; n <= e; n++) {
            for (m = s + 1; m <= e; m++) {
                D[n][m] = d[n][m] + MyMathUtils.min(D[n - 1][m], D[n][m - 1], D[n - 1][m - 1]);
            }
        }

        return D;

    }

}
