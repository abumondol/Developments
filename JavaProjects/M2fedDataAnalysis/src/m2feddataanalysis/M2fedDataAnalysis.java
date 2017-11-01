package m2feddataanalysis;

import rawdata.RawBeaconProcessor;

public class M2fedDataAnalysis {

    public static void main(String[] args) {
        String folderPath = "C:\\Users\\mm5gg\\Box Sync\\M2FED\\Deployment Data\\Brooks house\\downloaded at 09_04_2017";

        try {
            RawBeaconProcessor.processBeaconData(folderPath);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

    }

}
