package constants_config;

public class EatConfig {

    public static int window_size_for_min = 16;
    public static float min_x = (float) (-2.5);
    public static float varTh = (float) 0.3;
    public static float smooth_factor = (float) (0.9);
    public static float radius_extension_factor = (float) 1.1;
    public static int window_size_left = 48;
    public static int window_size_right = 32;
    public static int pattern_count_to_be_used = 0;

    public static int dbscan_min_points = 3;
    public static int dbscan_eps = 2 * 60 * 1000;
    public static long min_analysis_interval = 30 * 1000;
    public static long min_ema_sent_interval = 10 * 60 * 1000;
    public static long min_meal_duration = 60 * 1000;
    public static int min_meal_bite_count = 4;
    public static long min_wait_time_after_meal_to_decide = 2 * 60 * 1000;
}
