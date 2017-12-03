package nursingactivity;

/**
 *
 * @author mm5gg
 */
public class NursingActivity {

    public static void main(String[] args) {
        
        try{
            //DataSummary.analyzeNurseIDs(1);
            //DataSummary.analyzeActivities();
            ProcessLabels.process_labels();
            
        }catch(Exception ex){
            ex.printStackTrace();
            System.out.println(ex.toString());
        }
    }
    
}
