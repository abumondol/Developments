package nursingactivity;

/**
 *
 * @author mm5gg
 */
public class NursingActivity {

    public static void main(String[] args) {
        
        try{
            //DataSummary.analyzeNurseIDs(1);
            DataSummary.analyzeActivities();
            
        }catch(Exception ex){
            ex.printStackTrace();
            System.out.println(ex.toString());
        }
    }
    
}
