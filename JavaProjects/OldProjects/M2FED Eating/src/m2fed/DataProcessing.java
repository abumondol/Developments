package m2fed;

//@author mm5gg
import classification.Bite;
import classification.BiteClassifier;
import classification.MealDetector;
import java.io.File;
import java.util.ArrayList;
import location.LocationDetection;
import m2fedutils.FileUtils;
import m2fedutils.MyConstants;

public class DataProcessing {

    BiteClassifier biteClassifier;
    MealDetector mealDetector;
    LocationDetection locationDetection;
    
    String[][] watch_person_list;
    String[][] beacon_mac_id_list;
    String home_id;

    public DataProcessing() throws Exception {
        biteClassifier = new BiteClassifier();
        mealDetector = new MealDetector();
        locationDetection = new LocationDetection();
        home_id = "1";
    }

    public void processUploadedFile(File file) throws Exception {
        System.out.println("\nProcessing started ... " + file.getName());
        FileDetails fd = new FileDetails(file);
        fd.home_id = home_id;

        if (fd.file_type == MyConstants.FILE_TYPE_SENSOR) {
            processSensorData(fd);

        } else if (fd.file_type == MyConstants.FILE_TYPE_BLE) {
            processBLEData(fd);

        } else {
            System.out.println("Neither ble nor sensor file... " + file.getName());
        }

        System.out.println("Processing end ... " + file.getName());
    }

    public void processSensorData(FileDetails fd) throws Exception {
        //System.out.println("Processing sensor ... ");
        String[][] data = fd.data;
        int total = data.length;
        long[] time = new long[total];
        float[][] accel = new float[3][total];

        for (int i = 0; i < total; i++) {
            time[i] = (long) (Float.parseFloat(data[i][0]));
            accel[0][i] = Float.parseFloat(data[i][1]);
            accel[1][i] = Float.parseFloat(data[i][2]);
            accel[2][i] = Float.parseFloat(data[i][3]);

        }
        System.out.println("Rows, Cols: "+accel.length+", "+accel[0].length);

        ArrayList<Bite> biteList = biteClassifier.findBites(time, accel);
        if (biteList.isEmpty()) {
            return;
        }
        Bite b;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < biteList.size(); i++) {
            b = biteList.get(i);
            sb.append(home_id);
            sb.append(",");
            sb.append(fd.watch_id);
            sb.append(",");
            sb.append(fd.upload_time);
            sb.append(",");
            sb.append(b.time);
            sb.append(",");
            sb.append(b.type);
            sb.append(",");
            sb.append(0);
            sb.append("\n");
        }

        System.out.print(sb.toString());
        FileUtils.writeToFile("my_data/bite_list.txt", sb.toString());
        mealDetector.pushNewBites(biteList, fd);

    }

    public void processBLEData(FileDetails fd) throws Exception {
        //System.out.println("Processing ble ... ");
        String[][] data = fd.data;
        locationDetection.processBLEData(data);
    }
}
