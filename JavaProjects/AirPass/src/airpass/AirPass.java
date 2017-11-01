package airpass;

public class AirPass {

    public static void main(String[] args) {
        try {
            DataProcessor.processUploadedData();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

}
