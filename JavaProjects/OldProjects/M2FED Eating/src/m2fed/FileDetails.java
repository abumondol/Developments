package m2fed;
// @author mm5gg

import java.io.File;
import m2fedutils.FileUtils;
import m2fedutils.MyConstants;

 
public class FileDetails {
    public File file;
    public String file_name;
    public int file_type;
    public long start_time;
    public long end_time;
    public long upload_time;
    public String watch_id;
    public String[][] data;
    public String home_id;
    public String person_id;
    
    
    public FileDetails(File f) throws Exception{
        file = f;
        file_name = file.getName();        
        String[] str = file_name.split("-");
        
        if(str[0].equals("sensor"))
            file_type = MyConstants.FILE_TYPE_SENSOR;
        else if(str[0].equals("ble"))
            file_type = MyConstants.FILE_TYPE_BLE;
        else
            file_type =  MyConstants.FILE_TYPE_OTHER;
        
        watch_id = str[1];
        start_time = Long.parseLong(str[2]);
        end_time = Long.parseLong(str[3]);
        data = FileUtils.readCSV(file, true);
        upload_time = System.currentTimeMillis();
        //set_person_and_home_id(watch_person_list);        
    }
    
    private void set_person_and_home_id(String[][] watch_person_list){
        for(int i =0;i<watch_person_list.length;i++){
            if(watch_id.equals(watch_person_list[i][0])){
                home_id = watch_person_list[i][1];
                person_id = watch_person_list[i][2];
            }
        }
    }
    
}
