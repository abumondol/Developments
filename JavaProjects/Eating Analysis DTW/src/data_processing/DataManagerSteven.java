package data_processing;

import constants_config.MyParameters;
import static data_processing.DataManagerUVA.getLabData;
import entities.Pattern;
import entities.SessionData;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import myutils.MyArrayUtils;
import myutils.MyFileUtils;
import pattern.PatternManager;
import pattern.PotentialIndexFinder;

/**
 *
 * @author mm5gg
 */
public class DataManagerSteven {

    public static SessionData[][] getLabData() throws Exception {
        String fileName = "steven_lab_data.ser";
        if (DataManager.isSerFileExists(fileName)) {
            return (SessionData[][]) MyFileUtils.deSerializeFile(DataManager.serFilePath + fileName);
        }

        int subject_count = 7;
        SessionData[][] sds = new SessionData[subject_count][];
        float[][] accelData;
        String filePathAccel, filePathAnnots;
        String folderPath = "K:\\ASM\\data\\PublicData\\eating_steventech\\lab_public";

        for (int i = 0; i < sds.length; i++) {
            if (i <= 1) {
                sds[i] = new SessionData[1];
            } else {
                sds[i] = new SessionData[2];
            }

            for (int j = 0; j < sds[i].length; j++) {
                System.out.println("Reading steven lab data :" + i + ", " + j);
                if (i == 0) {
                    filePathAccel = folderPath + "\\0" + i + "\\0001\\watch_right_0001.csv";
                    filePathAnnots = folderPath + "\\0" + i + "\\0001\\annot_events.csv";
                } else if (i == 1) {
                    filePathAccel = folderPath + "\\0" + i + "\\0000\\watch_right_0000.csv";
                    filePathAnnots = folderPath + "\\0" + i + "\\0000\\annot_events.csv";
                } else {
                    filePathAccel = folderPath + "\\0" + i + "\\000" + j + "\\watch_right_000" + j + ".csv";
                    filePathAnnots = folderPath + "\\0" + i + "\\000" + j + "\\annot_events.csv";
                }

                sds[i][j] = new SessionData();

                accelData = processAccelData(MyFileUtils.readCSV(filePathAccel, true));
                accelData = DataManager.adjustSamplingRate(accelData);
                accelData = MyArrayUtils.transpose(accelData);

                sds[i][j].accelTime = accelData[0];
                sds[i][j].accelData = new float[3][];
                sds[i][j].accelData[0] = MyArrayUtils.smooth_data(accelData[1], MyParameters.SMOOTH_FACTOR); // x-axis
                sds[i][j].accelData[1] = MyArrayUtils.smooth_data(accelData[2], MyParameters.SMOOTH_FACTOR); // y-axis
                sds[i][j].accelData[2] = MyArrayUtils.smooth_data(accelData[3], MyParameters.SMOOTH_FACTOR); // z-axis
                sds[i][j].annots = processAnnots(accelData[0], MyFileUtils.readCSV(filePathAnnots, true));

                float duration = sds[i][j].accelTime[sds[i][j].accelTime.length - 1] - sds[i][j].accelTime[0];
                System.out.println("Duration: " + duration + ", Sampling rate: " + sds[i][j].accelTime.length / duration + ", Annot Count: " + sds[i][j].annots.length);
            }

        }

        MyFileUtils.serializeFile(sds, DataManager.serFilePath + fileName);
        System.out.println("Read and Serialization of Steven lab data done.");
        return sds;
    }

    public static SessionData[][] getFreeData() throws Exception {
        return getFreeData(false);

    }

    public static SessionData[][] getFreeData(boolean left) throws Exception {
        String fileName = "steven_free_data.ser";
        if (left) {
            fileName = "steven_free_data_left.ser";
        }

        if (DataManager.isSerFileExists(fileName)) {
            return (SessionData[][]) MyFileUtils.deSerializeFile(DataManager.serFilePath + fileName);
        }

        SessionData[][] sds = new SessionData[11][];
        float[][] accelData;
        String filePathAccel, filePathAnnots;
        //String folderPath = "K:\\ASM\\data\\PublicData\\eating_steventech\\PHI_ACE-E-FL_public";
        String folderPath = "C:\\ASM\\PublicData\\PHI_ACE-E-FL_public";
        int[] subjectCodes = {2, 3, 4, 5, 6, 101, 102, 103, 104, 107, 109};

        for (int i = 0; i < subjectCodes.length; i++) {
            if (subjectCodes[i] == 107) {
                sds[i] = new SessionData[5];
            } else {
                sds[i] = new SessionData[2];
            }

            for (int j = 0; j < sds[i].length; j++) {
                System.out.println("Reading steven free data :" + subjectCodes[i] + ", " + j);
                if (subjectCodes[i] < 10) {
                    filePathAccel = folderPath + "\\0" + subjectCodes[i] + "\\000" + j + "\\watch_right_000" + j + ".csv";
                    filePathAnnots = folderPath + "\\0" + subjectCodes[i] + "\\000" + j + "\\meal_events.csv";
                } else if (subjectCodes[i] == 109) {
                    filePathAccel = folderPath + "\\" + subjectCodes[i] + "\\000" + (j + 3) + "\\watch_right_000" + (j + 3) + ".csv";
                    filePathAnnots = folderPath + "\\" + subjectCodes[i] + "\\000" + (j + 3) + "\\meal_events.csv";
                } else {
                    filePathAccel = folderPath + "\\" + subjectCodes[i] + "\\000" + j + "\\watch_right_000" + j + ".csv";
                    filePathAnnots = folderPath + "\\" + subjectCodes[i] + "\\000" + j + "\\meal_events.csv";
                }

                sds[i][j] = new SessionData();

                if (left) {
                    filePathAccel = filePathAccel.replace("right", "left");
                    System.out.println(filePathAccel);
                }

                accelData = processAccelData(MyFileUtils.readCSV(filePathAccel, true));
                accelData = DataManager.adjustSamplingRate(accelData);
                accelData = MyArrayUtils.transpose(accelData);

                sds[i][j].accelTime = accelData[0];
                sds[i][j].accelData = new float[3][];
                sds[i][j].accelData[0] = MyArrayUtils.smooth_data(accelData[1], MyParameters.SMOOTH_FACTOR); // x-axis
                sds[i][j].accelData[1] = MyArrayUtils.smooth_data(accelData[2], MyParameters.SMOOTH_FACTOR); // y-axis
                sds[i][j].accelData[2] = MyArrayUtils.smooth_data(accelData[3], MyParameters.SMOOTH_FACTOR); // z-axis

                if (left) {
                    continue;
                }

                sds[i][j].meals = processMeals(accelData[0], MyFileUtils.readCSV(filePathAnnots, true), subjectCodes[i], j);

                float duration = sds[i][j].accelTime[sds[i][j].accelTime.length - 1] - sds[i][j].accelTime[0];
                System.out.println("Duration: " + duration + ", Samplint rate: " + sds[i][j].accelTime.length / duration + ", Meal Count: " + sds[i][j].meals.length);
            }

        }

        MyFileUtils.serializeFile(sds, DataManager.serFilePath + fileName);
        System.out.println("Read and Serialization of Steven free data done.");
        return sds;
    }

    public static float[][] processAccelData(String[][] a) {
        long nanoSecondFactor = 1000L * 1000L * 1000L;
        float[][] res = new float[a.length][4];
        for (int i = 0; i < a.length; i++) {
            res[i][0] = (float) Double.parseDouble(a[i][0]) / nanoSecondFactor;
            res[i][1] = Float.parseFloat(a[i][1]);
            res[i][2] = Float.parseFloat(a[i][2]);
            res[i][3] = Float.parseFloat(a[i][3]);
        }

        return res;
    }

    public static int[][] processAnnots(float[] times, String[][] annots) {
        ArrayList<int[]> res = new ArrayList<int[]>();
        int[] a;
        int hand = 0;
        String s;
        System.out.println("Annot file line count: " + annots.length);
        //MyArrayUtils.printArray(annots);

        for (int i = 0; i < annots.length - 1; i++) {
            s = annots[i][2];

            if (s.equals("DL") && annots[i + 1][2].equals("DR")) {
                hand = 3;
                i++;
            } else if (s.equals("DR")) {
                hand = 1;
            } else if (s.equals("DL")) {
                hand = 2;
            } else if (s.equals("I") || s.equals("Q")) {

                int index = DataManager.timeToIndex(times, Float.parseFloat(annots[i][0]) + Float.parseFloat(annots[i][1]) / 2);
                if (index == -1) {
                    System.out.println("********* No data for time: " + annots[i][0] + ", line no: " + (i + 1));
                    break;
                }

                if (hand == 0) {
                    System.out.println("********** Hand is not found : " + annots[i][2] + ", line no: " + (i + 1));
                    continue;
                }

                a = new int[2];
                a[0] = index;
                if (s.equals("I")) {
                    a[1] = 1;
                } else {
                    a[1] = 2;
                }

                if (hand == 1 || hand == 3) {
                    res.add(a);
                }
            }

        }

        /*System.out.println("Bite/drink count: "+res.size());
        if(res.size() == 0)
            System.exit(0);*/
        int[][] r = new int[res.size()][];
        for (int i = 0; i < res.size(); i++) {
            r[i] = res.get(i);
        }

        return r;
    }

    public static int[][] processMeals(float[] times, String[][] meals, int subjectCode, int session) {
        int mealCount = meals.length;
        if (subjectCode == 5 && session == 0) {
            mealCount = 9;
        } else if (subjectCode == 6 && session == 0) {
            mealCount = 2;
        } else if (subjectCode == 102 && session == 1) {
            mealCount = 1;
        } else if (subjectCode == 104 && session == 0) {
            mealCount = 7;
        }

        int[][] res = new int[mealCount][3];

        float t;
        int i = 0, j = 0;
        for (i = 0; i < mealCount; i++) {
            t = Float.parseFloat(meals[i][1]);
            for (; j < times.length - 1; j++) {
                if (t >= times[j] && t < times[j + 1]) {
                    res[i][0] = j;
                    break;
                }
            }

            t = Float.parseFloat(meals[i][2]);
            for (; j < times.length - 1; j++) {
                if (t >= times[j] && t < times[j + 1]) {
                    res[i][1] = j;
                    break;
                }
            }

            if (res[i][1] == 0) {
                if (subjectCode == 107 && session == 0 || subjectCode == 107 && session == 2) {
                    res[i][1] = times.length - 1;
                }
            }

            if (res[i][0] == 0 || res[i][1] == 0) {
                System.out.println("***** Time indexing problem. Meal line: " + (i + 1));
                System.exit(0);
            }

            res[i][2] = getMealCode(meals[i][3]);
        }

        return res;
    }

    public static int getMealCode(String meal) {
        meal = meal.trim();
        int code = -1;
        if (meal.equals("meal") || meal.equals("lunch") || meal.equals("dinner")) {
            code = 1;
        } else if (meal.equals("snack")) {
            code = 2;
        } else if (meal.equals("breakfast")) {
            code = 3;
        } else if (meal.equals("drink")) {
            code = 4;
        } else if (meal.equals("eat")) {
            code = 0;
        } else {
            System.out.println(" ********* Meal type doesnt match ************ " + meal);
            System.exit(0);
        }

        return code;
    }

    public static void printAnnotStat() throws Exception {
        SessionData[][] sds = getLabData();
        int biteTotal = 0, sipTotal = 0;
        int bite, sip;
        for (int subject = 0; subject < sds.length; subject++) {
            for (int sess = 0; sess < sds[subject].length; sess++) {
                bite = 0;
                sip = 0;
                int[][] a = sds[subject][sess].annots;
                for (int j = 0; j < a.length; j++) {
                    if (a[j][1] == 1) {
                        bite++;
                    } else if (a[j][1] == 2) {
                        sip++;
                    }
                }

                biteTotal += bite;
                sipTotal += sip;
                System.out.println("Ground Truth annot count UVA Lab Subject " + subject + ", Session " + sess + " >> bite: " + bite + ", sip: " + sip);
            }
        }

        System.out.println("Ground Truth annot count UVA Lab Session All >> bite: " + biteTotal + ", sip: " + sipTotal);
    }

    //////////////////////////////// Writing data to File////////////////////////////////////
    public static void writeFreeDataToTextFile() throws Exception {
        String filePath = "C:\\ASM\\DevData\\eating\\java\\";
        SessionData[][] sds = getFreeData();

        filePath = "C:\\ASM\\DevData\\eating\\java\\text_data_for_matlab\\steven\\";
        for (int subject = 0; subject < sds.length; subject++) {
            for (int session = 0; session < sds[subject].length; session++) {
                System.out.println("Writing StevenTech Data for subject, session: " + subject + ", " + session);

                float[][] d = new float[4][];
                d[0] = sds[subject][session].accelTime;
                d[1] = sds[subject][session].accelData[0];
                d[2] = sds[subject][session].accelData[1];
                d[3] = sds[subject][session].accelData[2];
                d = MyArrayUtils.transpose(d);
                MyFileUtils.writeToCSVFile(filePath + "data_free\\steven_free_accel_" + subject + "_" + session + ".csv", d, null);

                MyFileUtils.writeToCSVFile(filePath + "data_free\\steven_free_meals_" + subject + "_" + session + ".csv", sds[subject][session].meals, null);

            }
        }
    }

    public static void writeFreeResultsToTextFile() throws Exception {
        String filePath = "C:\\ASM\\DevData\\eating\\java\\";
        Pattern[][][] pats0 = (Pattern[][][]) MyFileUtils.deSerializeFile(filePath + "steven_bite_results_free_retrain_0.ser");
        Pattern[][][] pats1 = (Pattern[][][]) MyFileUtils.deSerializeFile(filePath + "steven_bite_results_free_retrain_1.ser");
        Pattern[][][] pats2 = (Pattern[][][]) MyFileUtils.deSerializeFile(filePath + "steven_bite_results_free_retrain_2.ser");
        //Pattern[][][] pats3 = (Pattern[][][]) MyFileUtils.deSerializeFile(filePath + "steven_bite_results_free_retrain_3.ser");

        filePath = "C:\\ASM\\DevData\\eating\\java\\text_data_for_matlab\\steven\\";
        for (int subject = 0; subject < pats0.length; subject++) {
            for (int session = 0; session < pats0[subject].length; session++) {
                System.out.println("Writing StevenTech Results for subject, session: " + subject + ", " + session);

                MyFileUtils.writeToCSVFile(filePath + "results_free\\steven_res0_free_bites_" + subject + "_" + session + ".csv", getBiteStat(pats0[subject][session]), null);
                MyFileUtils.writeToCSVFile(filePath + "results_free\\steven_res1_free_bites_" + subject + "_" + session + ".csv", getBiteStat(pats1[subject][session]), null);
                MyFileUtils.writeToCSVFile(filePath + "results_free\\steven_res2_free_bites_" + subject + "_" + session + ".csv", getBiteStat(pats2[subject][session]), null);
                //MyFileUtils.writeToCSVFile(filePath + "steven_res3_free_bites_" + subject + "_" + session + ".csv", getBiteStat(pats3[subject][session]), null);
            }
        }

    }

    public static void writeLabDataToTextFile() throws Exception {
        SessionData[][] sds = getLabData();

        String filePath = "C:\\ASM\\DevData\\eating\\java\\text_data_for_matlab\\steven\\";
        for (int subject = 0; subject < sds.length; subject++) {
            for (int session = 0; session < sds[subject].length; session++) {
                System.out.println("Writing StevenTech Lab Data for subject, session: " + subject + ", " + session);

                float[][] d = new float[4][];
                d[0] = sds[subject][session].accelTime;
                d[1] = sds[subject][session].accelData[0];
                d[2] = sds[subject][session].accelData[1];
                d[3] = sds[subject][session].accelData[2];
                d = MyArrayUtils.transpose(d);
                MyFileUtils.writeToCSVFile(filePath + "data_lab\\steven_lab_accel_" + subject + "_" + session + ".csv", d, null);

                MyFileUtils.writeToCSVFile(filePath + "data_lab\\steven_lab_annots_" + subject + "_" + session + ".csv", sds[subject][session].annots, null);

            }
        }
    }

    public static void writeFreeDataResultLeftToTextFile() throws Exception {
        SessionData[][] sds = getFreeData(true);
        String filePath = "C:\\ASM\\DevData\\eating\\java\\text_data_for_matlab\\steven\\";
        for (int subject = 0; subject < sds.length; subject++) {
            for (int session = 0; session < sds[subject].length; session++) {
                System.out.println("Writing StevenTech Free Left Data for subject, session: " + subject + ", " + session);

                float[][] d = new float[4][];
                d[0] = sds[subject][session].accelTime;
                d[1] = sds[subject][session].accelData[0];
                d[2] = sds[subject][session].accelData[1];
                d[3] = sds[subject][session].accelData[2];
                d = MyArrayUtils.transpose(d);
                MyFileUtils.writeToCSVFile(filePath + "data_free\\steven_free_accel_left_" + subject + "_" + session + ".csv", d, null);

                int[] minIndices = PotentialIndexFinder.findMinPointIndicesFiltered(invertXAccel(sds[subject][session].accelData));
                Pattern[] pats = PatternManager.getPatterns(sds[subject][session].accelTime, sds[subject][session].accelData, minIndices);
                MyFileUtils.writeToCSVFile(filePath + "results_free\\steven_res_left_free_bites_" + subject + "_" + session + ".csv", getBiteStat(pats), null);

            }
        }

    }

    public static float[][] invertXAccel(float[][] accel) {
        for (int i = 0; i < accel[0].length; i++) {
            accel[0][i] *= -1;
        }

        return accel;
    }

    public static float[][] getBiteStat(Pattern[] pats) throws Exception {
        float[][] res = new float[pats.length][5];
        for (int i = 0; i < pats.length; i++) {
            res[i][0] = pats[i].minPointIndex;
            res[i][1] = pats[i].minPointTime;
            res[i][2] = pats[i].minPointXVal;
            res[i][3] = pats[i].stdev;
            res[i][4] = pats[i].biteScore;
        }
        return res;
    }

    public static void printLabDataSummary() throws Exception {
        SessionData[][] sds = getLabData();
        int[][] res = new int[4][3];
        //System.out.println("Subject count: "+sds.length);
        MyArrayUtils.printArray(sds[0][0].annots);
        for (int i = 0; i < sds.length; i++) {
            System.out.println("Subject: " + i + ", session count: " + sds[i].length);
            for (int j = 0; j < sds[i].length; j++) {
                int[][] a = sds[i][j].annots;
                for (int k = 0; k < a.length; k++) {
                    res[a[k][1] / 10][a[k][1] % 10] += 1;
                }
            }
        }

        System.out.println(" Steventech Lab data Summary ");
        System.out.println(" Right Hand: " + res[1][1] + ", " + res[1][2]);
        System.out.println(" Left Hand:  " + res[2][1] + ", " + res[2][2]);
        System.out.println(" Both Hand:  " + res[3][1] + ", " + res[3][2]);
    }

    public static void deleteNonTextFiles() {
        String folder = "C:\\ASM\\PublicData\\ACE_lab_public_2\\lab_public\\";
        for (int i = 0; i <= 6; i++) {
            for (int j = 0; j <= 1; j++) {
                if (i == 1 && j == 1) {
                    continue;
                }
                File f = new File(folder + "0" + i + "\\000" + j);
                System.out.println(f.getAbsolutePath());

                File[] files = f.listFiles();
                for (File file : files) {
                    if (file.getName().endsWith(".csv") || file.getName().endsWith(".txt")) {
                        continue;
                    }

                    if (file.delete()) {
                        System.out.println("  > " + file.getName());
                    } else {
                        System.out.println("  xxx Cant delete file : " + file.getName());
                    }
                }
            }

        }
    }

    public static void combineFileForPrint() throws Exception {
        String folder = "C:\\ASM\\PublicData\\PHI_ACE-E-FL_public\\";
        String[] subjects = {"02", "03", "04", "05", "06", "101", "102", "103", "104", "107", "109"};

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < subjects.length; i++) {
            File[] sessions = new File(folder + subjects[i]).listFiles();
            for (int j = 0; j < sessions.length; j++) {
                sb.append("\n----------------------------------\n");
                sb.append("Subject: " + i + ", Code: " + subjects[i] + ",  Session: " + sessions[j].getName());
                sb.append("\n----------------------------------\n");

                Path path = Paths.get(sessions[j].getAbsolutePath() + "\\meal_events.csv");
                sb.append(new String(Files.readAllBytes(path)));
                sb.append("\n\n");
            }
        }

        PrintWriter pw = new PrintWriter(folder + "all_meals.txt");
        pw.write(sb.toString());
        pw.flush();
        pw.close();

    }

}
