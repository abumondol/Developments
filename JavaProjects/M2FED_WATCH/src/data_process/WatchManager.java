package data_process;

import java.io.File;
import java.util.ArrayList;
import entities.FileDetails;

public class WatchManager {
    
    ArrayList<Watch> watchList;
    
    
    public WatchManager() {
        watchList = new ArrayList<>();
    }
    
    public void processUploadedFile(File file) throws Exception {
        FileDetails fd = new FileDetails(file);
        Watch watch = getWatch(fd.watch_id);
        watch.processUploadedFile(fd);
    }
    
    public void periodicRefresh() throws Exception{
        for(Watch w:watchList){
            w.sdm.mealManager.process();
        }
        
    }
    
    Watch getWatch(String watch_id) throws Exception{
        for (int i = 0; i < watchList.size(); i++) {
            if (watchList.get(i).watch_id.equals(watch_id)) {
                return watchList.get(i);
            }
        }
        Watch watch = new Watch(watch_id);
        watchList.add(watch);
        return watch;
    }
    
}
