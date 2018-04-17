package classification;

import java.util.ArrayList;
import m2fed.FileDetails;
import m2fedutils.FileUtils;

/**
 *
 * @author mm5gg
 */
public class MealDetector {

    ArrayList<Bite> biteList;
    long mealStartTime = 0;
    long mealEndTime = 0;
    int mealStatus = 0;
    long window_size = 3 * 60 * 1000;
    int min_density = 5;
    boolean eating_status = false;
    long start_time;
    int total_bite_count;
    int bite_count_last_window;

    public MealDetector() {
        biteList = new ArrayList<>();

    }

    public void pushNewBites(ArrayList<Bite> list, FileDetails fd) throws Exception {
        biteList.addAll(list);
        mealStatusDecision(fd);
    }

    public int mealStatusDecision(FileDetails fd) throws Exception {
        long current_time = System.currentTimeMillis();
        bite_count_last_window = 0;
        for (int i = 0; i < biteList.size(); i++) {
            if (current_time - biteList.get(i).time < window_size) {
                bite_count_last_window += 1;
            }

            if (bite_count_last_window >= min_density && !eating_status) {
                eating_status = true;
                write_to_eating_ema(1, fd);
                start_time = System.currentTimeMillis() - window_size;
            } else if (bite_count_last_window < min_density && eating_status) {
                eating_status = false;
                total_bite_count = biteList.size();
                write_to_eating_ema(2, fd);
                write_to_meal_list(mealStatus, fd);
                biteList = new ArrayList<>();
            }
        }
        return mealStatus;
    }

    void write_to_eating_ema(int event_type, FileDetails fd) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(1);
        sb.append(",");
        sb.append(fd.watch_id);
        sb.append(",");
        sb.append(fd.upload_time);
        sb.append(",");
        sb.append(System.currentTimeMillis());
        sb.append(",");
        sb.append(event_type);
        sb.append(",");
        sb.append(0);
        sb.append(",");
        sb.append(0);
        sb.append("\n");
        FileUtils.writeToFile("my_data/eating_ema.txt", sb.toString());

    }

    void write_to_meal_list(int event_type, FileDetails fd) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(1);
        sb.append(",");
        sb.append(fd.watch_id);
        sb.append(",");
        sb.append(fd.upload_time);
        sb.append(",");
        sb.append(start_time);
        sb.append(",");
        sb.append(System.currentTimeMillis());
        sb.append(",");
        sb.append(event_type);
        sb.append(",");
        sb.append(total_bite_count);      
        sb.append("\n");
        FileUtils.writeToFile("my_data/meal_list.txt", sb.toString());

    }
}
