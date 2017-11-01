package meal;

import data_processing.DataManager;
import data_processing.DataManagerSteven;
import entities.Meal;
import entities.Pattern;
import entities.SessionData;
import java.util.ArrayList;
import myutils.MyFileUtils;
import pattern.PatternUtils;

/**
 *
 * @author mm5gg
 */
public class MealManager {

    public static void TestMealStevenTech(int reTrain) throws Exception {
        SessionData[][] sds = DataManagerSteven.getFreeData();
        Pattern[][][] allPats;
        
        allPats = (Pattern[][][]) MyFileUtils.deSerializeFile(DataManager.serFilePath + "steven_bite_results_free_retrain_" + reTrain + ".ser");
        
        Meal[] meals;
        for (int i = 0; i < allPats.length; i++) {
            for (int j = 0; j < allPats[i].length; j++) {

                System.out.println("\n\nSubject: " + i + ", Session:" + j);
                System.out.print("All pattern count: " + allPats[i][j].length);
                allPats[i][j] = PatternUtils.filterPatternsByScore(allPats[i][j], (float) 0.5);
                System.out.println(", Bite Pattern count: " + allPats[i][j].length);
                //printPatIndices(allPats[i][j]);

                meals = findMeals(allPats[i][j]);

                if (meals != null) {
                    System.out.println("\nMeal detected:" + meals.length);
                    for (int k = 0; k < meals.length; k++) {
                        float st = meals[k].startTime;
                        float et = meals[k].endTime;
                        System.out.println("Meal " + k + ": " + st + ", " + et + ", " + (et - st) / 60);
                    }
                } else {
                    System.out.println("Meal detected: 0");
                }

                if (sds[i][j].meals != null) {
                    System.out.println("\nActual Meal count:" + sds[i][j].meals.length);
                    for (int k = 0; k < sds[i][j].meals.length; k++) {
                        float st = sds[i][j].accelTime[sds[i][j].meals[k][0]];
                        float et = sds[i][j].accelTime[sds[i][j].meals[k][1]];

                        System.out.println("Meal " + k + ": " + st + ", " + et + ", " + (et - st) / 60);

                    }
                } else {
                    System.out.println("Actual Meal count: 0");
                }

            }
        }
    }

    public static void printPatIndices(Pattern[] pats) {
        System.out.println("Patter count: " + pats.length);
        for (int i = 0; i < pats.length; i++) {
            //System.out.print(pats[i].minPointIndex + ":" + pats[i].minPointTime + ", ");
            System.out.print(pats[i].minPointTime + ", ");
        }

        System.out.println();
    }

    public static Meal[] findMeals(Pattern[] pats) {
        int dbscan_min_points = 3;
        int dbscan_eps = 2 * 60;
        ArrayList<Meal> mealList = DBScan.findClustersFromPat(pats, dbscan_eps, dbscan_min_points);

        int mealCount = mealList.size();
        if (mealCount == 0) {
            return null;
        }

        Meal[] meals = new Meal[mealCount];
        for (int i = 0; i < mealCount; i++) {
            meals[i] = mealList.get(i);
        }

        return meals;
    }

}
