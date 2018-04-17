package m2fed;

//@author mm5gg
import java.io.File;
import m2fed.myutils.FileUtils;

public class AnnotationProcess {

    static String[][] activities = {
        {"soup_spoon", "icecream_spoon", "yogurt_spoon", "rice_spoon", "cereal_spoon", "chilli_spoon", "can_food_spoon"},
        {"pizza_fork", "salad_fork", "chicken_fork", "cake_fork"},
        {"salad_chopstick"},
        {"salad_hand", "sandwich_hand", "chip_hand", "pizza_hand", "pretzel_hand", "chocolate_hand", "banana_hand", "grape_hand", "apple_hand"},
        {"tea_cup"},
        {"water_glass", "water_bottle", "water_mug", "milk_cup"},
        {"water_straw"},
        {"non_bite", "napkin_hand"}
    };
    static String[][] activities1 = {
        {"soup_spoon", "icecream_spoon"},
        {"pizza_fork", "salad_fork"},
        {"salad_chopstick"},
        {"salad_hand", "sandwich_hand", "chip_hand", "pizza_hand", "pretzel_hand", "chocolate_hand"},
        {"tea_cup"},
        {"water_glass", "water_bottle", "water_mug"},
        {"water_straw"},
        {"non_bite", "napkin_hand"}
    };
    static String[][] activities2 = {
        {"yogurt_spoon", "icecream_spoon", "rice_spoon", "cereal_spoon", "chilli_spoon", "soup_spoon"},
        {"chicken_fork", "cake_fork", "pizza_fork", "salad_fork"},
        {"salad_chopstick"},
        {"banana_hand", "chip_hand", "grape_hand", "apple_hand", "salad_hand", "sandwich_hand", ""},
        {"milk_cup", "tea_cup"},
        {"water_bottle", "water_mug"},
        {"water_straw"},
        {"non_bite", "napkin_hand"}
    };

    public static void printClassVal() {
        for (int i = 0; i < activities.length; i++) {
            for (int j = 0; j < activities[i].length; j++) {
                if (i == activities.length - 1) {
                    int code = 1000 + (j + 1);
                    System.out.println(code + " : " + activities[i][j]);
                } else {
                    int code = i * 100 + (j + 1);
                    System.out.println(code + " : " + activities[i][j]);
                }
            }
            System.out.println();
        }
    }

    public static void processAnotations() throws Exception {
        String srcFolder = "C:\\Users\\mm5gg\\Box Sync\\Data Sets\\Eating\\usc_right\\annotations\\raw";
        String destFolder = "C:\\Users\\mm5gg\\Box Sync\\Data Sets\\Eating\\usc_right\\annotations\\processed";
        int code;
        //String srcFolder = "C:\\Users\\mm5gg\\Box Sync\\Data Sets\\Eating\\uva_right_sony_ridwan_lab\\annotations\\raw";
        //String destFolder = "C:\\Users\\mm5gg\\Box Sync\\Data Sets\\Eating\\uva_right_sony_ridwan_lab\\annotations\\processed";

        File folder = new File(srcFolder);
        File[] files = folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            String[][] data = FileUtils.readCSV(files[i], false);
            String[][] res = new String[data.length][2];
            //System.out.println(files[i].getName());

            for (int j = 0; j < data.length; j++) {
                try {
                    res[j][0] = timeStringToFloat(data[j][0]) + "";
                    code = getActivityCode(data[j][4]);
                    if(code ==-1){
                        System.out.println(files[i].getName()+", "+ j+", "+data[j][4]);
                    }
                    
                    res[j][1] = code+ "";
                } catch (Exception ex) {
                    System.out.println(files[i].getName() + ", " + j + ", " + data[j][0]);
                    return;
                }
            }

            FileUtils.writeToCSVFile(destFolder + "\\" + files[i].getName(), res, null);
        }


    }

    public static int getActivityCode(String act) {
        act = changeAnnotationString(act);
        for (int i = 0; i < activities.length; i++) {
            for (int j = 0; j < activities[i].length; j++) {
                if (act.equals(activities[i][j])) {
                    if (i == activities.length - 1) {
                        return 1000 + (j + 1);
                    } else {
                        return i * 100 + (j + 1);
                    }
                }
            }
        }

        System.out.println("Error: " + act);
        return -1;
    }

    public static float timeStringToFloat(String time) {
        String[] s = time.split(":");
        int min = Integer.parseInt(s[0]);
        float sec = Float.parseFloat(s[1]);
        return min * 60 + sec;
    }

    public static String changeAnnotationString(String s) {
        s = s.toLowerCase();
        if (s.equals("cofee_mug") || s.equals("coffe_cup") || s.equals("coffe_car") || s.equals("coffe_mug") || s.equals("coffee_car")) {
            return "tea_cup";
        } else if (s.equals("scratch_hand")) {
            return "non_bite";
        } else if (s.equals("apples_hand")) {
            return "apple_hand";
        } else if (s.equals("grapes_hand")) {
            return "grape_hand";
        } else if (s.equals("water_glass")) {
            return "water_mug";
        } else if (s.equals("chips_hand") || s.equals("cookies_hand")) {
            return "chip_hand";
        }

        return s;
    }
}
