package m2fedutils;

import java.io.File;

public class ClearResults {

    public static void clearAllData() throws Exception {
        clearData("bite");
        clearData("meal");
        clearData("ema");
        clearData("battery");
        clearData("location");
        clearData("beacon");
        clearData("log");
    }

    public static void clearData(String type) throws Exception {
        String srcFolder = "..\\M2FED_WATCH\\M2FED_results\\Eating\\" + type;
        File dir = new File(srcFolder);

        File[] files = dir.listFiles();
        System.out.println("Deleting files type: " + type + ", count:" + files.length);
        for (int i = 0; i < files.length; i++) {
            boolean res = files[i].delete();
            if (!res) {
                System.out.println("Deletion failed :" + files[i].getName());
            }
        }

    }

}
