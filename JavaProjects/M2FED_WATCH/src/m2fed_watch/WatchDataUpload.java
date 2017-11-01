package m2fed_watch;

import data_process.WatchManager;
import java.io.File;
import java.util.Arrays;
import m2fedutils.DateTimeUtils;

//@author mm5gg
public class WatchDataUpload {

    final long wait_interval = 5 * 1000;
    String uploadFolderString = "C:\\wamp\\www\\m2fed_watch\\uploads";
    //String uploadFolderString = "C:\\xampp\\htdocs\\m2fed_watch\\uploads";    
    String destinationFolderString = "M2FED_Results/RawData/M2FED_Watch_Data";
    File srcFile, destFile, dateFolder;
    WatchManager watchManager;
    String dateFolderString;

    public WatchDataUpload() throws Exception {
        watchManager = new WatchManager();
    }

    void watchUpload() throws Exception {
        File[] files;
        int i;
        

        System.out.print("Waiting...");
        while (true) {
            files = new File(uploadFolderString).listFiles();
            if (files.length == 0) {
                watchManager.periodicRefresh();
                Thread.sleep(wait_interval);
                continue;
            }
            
      
            dateFolderString = destinationFolderString+"\\"+DateTimeUtils.currentDateInString();
            dateFolder = new File(dateFolderString);
            if(!dateFolder.exists())
                dateFolder.mkdirs();
            

            Arrays.sort(files);
            System.out.println("File Count:" + files.length);
            for (i = 0; i < files.length; i++) {
                destFile = new File(dateFolderString + "\\" + files[i].getName());
                if (!destFile.exists()) {
                    watchManager.processUploadedFile(files[i]);
                    boolean flag = files[i].renameTo(destFile);
                    if (!flag) {
                        System.out.println("Failed moving file to: " + destFile.getPath());
                    }
                } else {
                    System.out.println("Destination file exists. Delete result: " + files[i].delete());
                }

            }

            System.out.print("Waiting...");
        }
    }

}
