package m2fed_watch_utility;

public class M2FED_WATCH_UTILITY {

    public static void main(String[] args) {
        try {
            //ClearData.clearAllData();
            String watch_uva_3 = "14442D27F8D2EF6";
            String watch_usc_1 = "14432D35F47689C";
            String watch_usc_2 = "14442D31F8BC4E0";
            
            String watch = watch_usc_2;
            String date = "2017-07-14";
            
            ResultViewer.showResultsAll(date, watch);
            //ResultViewer.showBites(date, watch);
            //ResultViewer.showMeals(date, watch);
            //ResultViewer.showMeals(date, watch);
            
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

    }

}
