package m2fed_watch_utility;

import java.io.File;
import java.util.Arrays;

public class ResultViewer {

    public static void showResultsAll(String date, String watch_id) throws Exception {
        showBites(date, watch_id);
        showMeals(date, watch_id);
        showEmas(date, watch_id);
    }
    public static void showBites(String date, String watch_id) throws Exception {
        String srcFolder = "..\\M2FED_WATCH\\M2FED_results\\Eating\\bite";
        File dir = new File(srcFolder);

        File[] files = dir.listFiles();
        Arrays.sort(files);

        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().startsWith(date) && files[i].getName().endsWith(watch_id)) {
                String[][] data = FileUtils.readCSV(files[i], true);
                System.out.println("Bites: " + files[i].getName());
                long t;
                for (int j = 0; j < data.length; j++) {
                    t = Long.parseLong(data[j][1]);
                    System.out.print((j + 1) + ": " + DateTimeUtils.getDateTimeString(t, 3));
                    t = Long.parseLong(data[j][2]);
                    System.out.print(", " + DateTimeUtils.getDateTimeString(t, 3));
                    System.out.println(", " + data[j][3]);
                }

            }
        }

    }

    public static void showMeals(String date, String watch_id) throws Exception {
        String srcFolder = "..\\M2FED_WATCH\\M2FED_results\\Eating\\meal";
        File dir = new File(srcFolder);

        File[] files = dir.listFiles();
        Arrays.sort(files);

        for (File file : files) {
            if (file.getName().startsWith(date) && file.getName().endsWith(watch_id)) {
                String[][] data = FileUtils.readCSV(file, true);
                System.out.println("Meals: " + file.getName());
                long t;
                for (int j = 0; j < data.length; j++) {
                    t = Long.parseLong(data[j][2]);
                    System.out.print((j + 1) + ": " + DateTimeUtils.getDateTimeString(t, 3));
                    t = Long.parseLong(data[j][3]);
                    System.out.print(", " + DateTimeUtils.getDateTimeString(t, 3));
                    System.out.println(", " + data[j][4]);
                }
            }
        }

    }

    public static void showEmas(String date, String watch_id) throws Exception {
        String srcFolder = "..\\M2FED_WATCH\\M2FED_results\\Eating\\ema";
        File dir = new File(srcFolder);

        File[] files = dir.listFiles();
        Arrays.sort(files);

        for (File file : files) {
            if (file.getName().startsWith(date) && file.getName().endsWith(watch_id)) {
                String[][] data = FileUtils.readCSV(file, true);
                System.out.println("EMAs: " + file.getName());
                long t;
                for (int j = 0; j < data.length; j++) {
                    t = Long.parseLong(data[j][2]);
                    System.out.println((j + 1) + ": " + DateTimeUtils.getDateTimeString(t, 3));
                }
            }
        }

    }
    
    public static void showBattery(String date, String watch_id) throws Exception {
        String srcFolder = "..\\M2FED_WATCH\\M2FED_results\\Eating\\battery";
        File dir = new File(srcFolder);

        File[] files = dir.listFiles();
        Arrays.sort(files);

        for (File file : files) {
            if (file.getName().startsWith(date) && file.getName().endsWith(watch_id)) {
                String[][] data = FileUtils.readCSV(file, true);
                System.out.println("Battery Status: " + file.getName());
                long t;
                for (int j = 0; j < data.length; j++) {
                    t = Long.parseLong(data[j][2]);
                    System.out.println((j + 1) + ": " + DateTimeUtils.getDateTimeString(t, 2));
                }
            }
        }

    }
    
    public static void showLocation(String date, String watch_id) throws Exception {
        String srcFolder = "..\\M2FED_WATCH\\M2FED_results\\Eating\\location";
        File dir = new File(srcFolder);

        File[] files = dir.listFiles();
        Arrays.sort(files);

        for (File file : files) {
            if (file.getName().startsWith(date) && file.getName().endsWith(watch_id)) {
                String[][] data = FileUtils.readCSV(file, true);
                System.out.println("Locations: " + file.getName());
                long t;
                for (int j = 0; j < data.length; j++) {
                    t = Long.parseLong(data[j][2]);
                    System.out.println((j + 1) + ": " + DateTimeUtils.getDateTimeString(t, 2));
                }
            }
        }

    }
}
