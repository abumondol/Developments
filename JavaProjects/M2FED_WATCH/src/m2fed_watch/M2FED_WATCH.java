
package m2fed_watch;

//@author mm5gg
public class M2FED_WATCH {

    public static void main(String[] args){        
        try{
            new WatchDataUpload().watchUpload();
        }catch(Exception ex){
            ex.printStackTrace();
            System.out.println(ex.toString());
        }
    }
    
}
