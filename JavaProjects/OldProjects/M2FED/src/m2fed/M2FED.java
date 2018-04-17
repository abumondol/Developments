package m2fed;

// @author mm5gg

public class M2FED {
    
    public static void main(String[] args) {
        try{
            //Beacon.processBeacon();
            AnnotationProcess.processAnotations();
            //AnnotationProcess.printClassVal();
            //CombineDataFiles.combineFilesEatingData();
            
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }
}
