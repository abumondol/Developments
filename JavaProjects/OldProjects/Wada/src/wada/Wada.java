/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wada;

/**
 *
 * @author mm5gg
 */
public class Wada {

    public static void main(String[] args) {
        int sensorID;        

        try {
            if (args[0].equals("acl")) {
                sensorID = 1;               
                if (args.length < 2) {
                    System.out.println("Source folder path required");
                    return;
                }else if (args.length < 3) {
                    System.out.println("Destination folder path required");
                    return;
                }
                
                System.out.println("Processing data ...");
                Sensor.filterAllSensorFiles(args[1], args[2], sensorID);
                System.out.println("Done");

            } else if (args[0].equals("arff")) {
                if (args.length < 2) {
                    System.out.println("Filepath required");
                    return;
                }
                System.out.println("Processing file ...");
                boolean flag = Arff.csvToArff(args[1]);
                if (flag) {
                    System.out.println("Done");
                } else {
                    System.out.println("Problem in file processing.. aborted.");
                }

            } else {
                System.out.println("This command not available in wada tool");
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
        }

    }
}
