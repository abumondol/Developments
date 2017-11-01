package m2fed_watch;

import data_process.WatchManager;
import java.io.File;
import java.util.Arrays;
import m2fedutils.ClearResults;

//@author mm5gg
public class WatchDataUploadTest {

    final long wait_interval = 5 * 1000;
    //String uploadFolder = "C:\\wamp\\www\\m2fed_watch\\uploads";
    String uploadFolder = "my_data/test_raw_data/abu";    
    String destinationFolder = "M2FED_Results/RawData/M2FED_Watch_Data";
    File srcFile, destFile;
    WatchManager watchManager;

    public WatchDataUploadTest() throws Exception {
        watchManager = new WatchManager();
    }

    void watchUpload() throws Exception {
        ClearResults.clearAllData();
        File[] files = new File(uploadFolder).listFiles();;
        if (files.length == 0) {
            System.exit(0);
        }

        Arrays.sort(files);
        System.out.println("File Count:" + files.length);
        for (File file : files) {
            watchManager.processUploadedFile(file);
        }

        System.out.print("Done...");
    }

}
