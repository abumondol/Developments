package airpass;

import java.io.File;
import myutils.MyFileUtils;

/**
 *
 * @author mm5gg
 */
public class DataProcessor {

    public static void processUploadedData() throws Exception {
        String srcFolderPath = "C:\\xampp\\htdocs\\airpass\\uploads";
        String destFolderPath = "C:\\Users\\mm5gg\\Box Sync\\Data Sets\\AirPass";

        File srcFolder = new File(srcFolderPath);
        File[] files = srcFolder.listFiles();
        System.out.println("File count: " + files.length);

        String s, user, type;
        String[] str;
        double[][] d;
        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ": " + files[i].getName());
            d = (double[][]) MyFileUtils.deSerializeFile(files[i]);
            s = arrayToString(d);

            str = files[i].getName().split("-");
            type = str[1];
            user = str[2];
            File userFolder = new File(destFolderPath + "\\text_files\\" + type + "\\" + user);
            if (!userFolder.exists()) {
                userFolder.mkdirs();
            }
            MyFileUtils.writeToFile(userFolder + "\\" + files[i].getName() + ".txt", s);

            userFolder = new File(destFolderPath + "\\binary_files\\" + type + "\\" + user);
            if (!userFolder.exists()) {
                userFolder.mkdirs();
            }
            File destFile = new File(userFolder + "\\" + files[i].getName());
            files[i].renameTo(destFile);
        }

    }

    public static String arrayToString(double[][] d) {
        int i, j;
        StringBuilder sb = new StringBuilder();
        for (i = 0; i < d.length; i++) {
                            
            sb.append((long)d[i][0]);
            sb.append(",");
            sb.append((int)d[i][1]);
            sb.append(",");
            sb.append((int)d[i][2]);
            sb.append(",");
            sb.append(d[i][3]);
            sb.append(",");
            sb.append(d[i][4]);
            sb.append(",");
            sb.append(d[i][5]);           
            sb.append("\n");
        }
        return sb.toString();
    }

}
