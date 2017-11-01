package data_processing;

import constants_config.MyParameters;
import entities.SessionData;
import java.util.ArrayList;
import myutils.MyArrayUtils;
import myutils.MyFileUtils;

/**
 *
 * @author mm5gg
 */
public class DataManagerUVA {

    public static int SESSION_COUNT_LAB = 35;
    public static int SESSION_COUNT_FREE = 16;

    public static SessionData[] getLabData() throws Exception {
        String fileName = "our_lab_data.ser";
        if (DataManager.isSerFileExists(fileName)) {
            return (SessionData[]) MyFileUtils.deSerializeFile(DataManager.serFilePath + fileName);
        }

        SessionData[] sds = new SessionData[SESSION_COUNT_LAB];
        float[][] accelData;
        String filePath;

        for (int i = 0; i < SESSION_COUNT_LAB; i++) {
            System.out.println("Reading our lab data " + i);
            filePath = "C:\\ASM\\DevData\\eating\\\\our_lab_data_text\\accel_" + i;
            accelData = MyArrayUtils.stringArrayToFloatArray(MyFileUtils.readCSV(filePath, true));
            accelData = DataManager.adjustSamplingRate(accelData);
            accelData = MyArrayUtils.transpose(accelData);

            sds[i] = new SessionData();
            sds[i].accelTime = accelData[0];
            sds[i].accelData = new float[3][];
            sds[i].accelData[0] = MyArrayUtils.smooth_data(accelData[1], MyParameters.SMOOTH_FACTOR); // x-axis
            sds[i].accelData[1] = MyArrayUtils.smooth_data(accelData[2], MyParameters.SMOOTH_FACTOR); // y-axis
            sds[i].accelData[2] = MyArrayUtils.smooth_data(accelData[3], MyParameters.SMOOTH_FACTOR); // z-axis

            filePath = "C:\\ASM\\DevData\\eating\\\\our_lab_data_text\\annot_" + i;
            sds[i].annots = processAnnots(accelData[0], MyFileUtils.readCSV(filePath, true));
        }

        MyFileUtils.serializeFile(sds, DataManager.serFilePath + fileName);
        System.out.println("Read and Serialization of our lab data done.");
        return sds;
    }

    public static SessionData[] getFreeData() throws Exception {
        String fileName = "our_free_data.ser";
        if (DataManager.isSerFileExists(fileName)) {
            return (SessionData[]) MyFileUtils.deSerializeFile(DataManager.serFilePath + fileName);
        }

        SessionData[] sds = new SessionData[SESSION_COUNT_FREE];
        float[][] accelData;
        String filePath;

        for (int i = 0; i < SESSION_COUNT_FREE; i++) {
            System.out.println("Reading our free data " + i);
            filePath = "C:\\ASM\\DevData\\eating\\\\our_free_data_text\\accel_" + i;
            accelData = MyArrayUtils.stringArrayToFloatArray(MyFileUtils.readCSV(filePath, true));
            accelData = DataManager.adjustSamplingRate(accelData);
            accelData = MyArrayUtils.transpose(accelData);

            sds[i] = new SessionData();
            sds[i].accelTime = accelData[0];
            sds[i].accelData = new float[3][];
            sds[i].accelData[0] = MyArrayUtils.smooth_data(accelData[1], MyParameters.SMOOTH_FACTOR); // x-axis
            sds[i].accelData[1] = MyArrayUtils.smooth_data(accelData[2], MyParameters.SMOOTH_FACTOR); // y-axis
            sds[i].accelData[2] = MyArrayUtils.smooth_data(accelData[3], MyParameters.SMOOTH_FACTOR); // z-axis
        }

        MyFileUtils.serializeFile(sds, DataManager.serFilePath + fileName);
        System.out.println("Read and Serialization of our free data done.");
        return sds;
    }

    public static int[][] processAnnots(float[] times, String[][] a) {
        int[][] res = new int[a.length][2];
        for (int i = 0; i < a.length; i++) {
            res[i][0] = DataManager.timeToIndex(times, Float.parseFloat(a[i][0]));
            res[i][1] = Integer.parseInt(a[i][1]); // label
            
            if (res[i][1] >= 1 && res[i][1] < 400) {
                res[i][1] = 1;
            } else if (res[i][1] >= 400 && res[i][1] < 1000) {
                res[i][1] = 2;
            }else{
                res[i][1] = 0;
            }
        }
        return res;
    }
    
    public static void printAnnotStat() throws Exception {
        SessionData[] sds = getLabData();
        int biteTotal = 0, sipTotal = 0;
        int bite, sip;
        for (int sess = 0; sess < sds.length; sess++) {
            bite = 0;
            sip = 0;

            int[][] a = sds[sess].annots;
            for (int j = 0; j < a.length; j++) {
                if (a[j][1] == 1) {
                    bite++;
                } else if (a[j][1] == 2) {
                    sip++;
                }
            }

            biteTotal += bite;
            sipTotal += sip;
            System.out.println("Ground Truth annot count UVA Lab Session " + sess + " >> bite: " + bite + ", sip: " + sip);
        }

        System.out.println("Ground Truth annot count UVA Lab Session All >> bite: " + biteTotal + ", sip: " + sipTotal);
    }

}
