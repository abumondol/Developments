package edu.virginia.cs.mondol.fed.bites;

/**
 * Created by Abu on 7/6/2017.
 */

public class MealDetector {
    long meal_window_size = 1 * 60 * 1000; //minute*second*millisecond
    int bite_count_for_meal_start = 1, meal_status = 0;
}
