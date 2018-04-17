package wada;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

/**
 *
 * @author mm5gg
 */
public class Arff {

    public static boolean csvToArff(String fileName) throws Exception {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(fileName));

        String line = br.readLine();
        if (line == null) {
            System.out.println("No data found");
            return false;
        }
        String[] attributeList = line.split(",");
        int attributeCount = attributeList.length;


        line = br.readLine();
        if (line == null) {
            System.out.println("No data found");
            return false;
        }

        String[] tokens = line.split(",");
        if (tokens.length != attributeCount) {
            System.out.println("Filed count mismatch. Header count: " + attributeCount + ",  at line 1 ite is: " + tokens.length);
            return false;
        }


        String[] classList = new String[1];
        classList[0] = tokens[tokens.length - 1];

        sb.append(line);
        sb.append("\n");
        int rowCount = 2;

        while ((line = br.readLine()) != null && line.trim().length() > 0) {
            tokens = line.split(",");
            if (tokens.length != attributeCount) {
                System.out.println("Filed count mismatch. Header count: " + attributeCount + ",  at line " + (rowCount + 1) + " it is: " + tokens.length);
                return false;
            }

            sb.append(line);
            sb.append("\n");
            rowCount++;
            classList = addClass(classList, tokens[tokens.length - 1]);
        }

        br.close();

        StringBuilder arffData = getArffHeader(attributeList, classList);
        arffData.append(sb);

        if (fileName.endsWith(".csv")) {
            fileName = fileName.replace(".csv", ".arff");
        } else {
            fileName = fileName + ".arff";
        }

        PrintWriter out = new PrintWriter(fileName);
        out.print(arffData.toString());
        out.flush();
        out.close();
        return true;

    }

    public static StringBuilder getArffHeader(String[] attributeList, String[] classList) {
        StringBuilder s = new StringBuilder();
        s.append("@RELATION wada\n\n");

        int i;
        for (i = 0; i < attributeList.length - 1; i++) {
            s.append("@ATTRIBUTE ");
            s.append(attributeList[i]);
            s.append(" numeric\n");
        }

        s.append("@ATTRIBUTE ");
        s.append(attributeList[i]);
        s.append(" {");
        s.append(classList[0]);

        for (i = 1; i < classList.length; i++) {
            s.append(",");
            s.append(classList[i]);
        }
        s.append("}\n\n");
        s.append("@DATA\n");
        return s;
    }

    public static String[] addClass(String[] classList, String className) {
        int len = classList.length;
        int i;
        for (i = 0; i < len; i++) {
            if (className.equals(classList[i])) {
                return classList;
            }
        }

        String[] newList = new String[len + 1];
        for (i = 0; i < len; i++) {
            newList[i] = classList[i];
        }
        newList[i] = className;

        return newList;
    }
}
