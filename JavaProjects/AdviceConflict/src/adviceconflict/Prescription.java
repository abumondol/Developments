package adviceconflict;

import java.io.File;
import java.util.ArrayList;
import utils.FileUtils;
import utils.MC;
import utils.MyUtils;

public class Prescription {

    public static String[][][] getPossiblePair(String[][] catItems ) throws Exception {
        File file = new File(MC.SRC_FOLDER_PATH + "\\PrescriptionsList.csv");
        String[][] data = FileUtils.readCSV(file, false);       
        
        ArrayList<String[]> list;
        ArrayList<String> catList;
        String[][][] res = new String[3][][];        

        int i, j, k, type, n;
        String s;
        String[] str, str2;

        for (type = 0; type < 2; type++) {
            list = new ArrayList<>();
            catList = new ArrayList<>();
            for (i = 0; i < data.length; i++) {
                str = data[i][type].split(";");
                for (j = 0; j < str.length; j++) {
                    str[j] = str[j].trim().toLowerCase();
                    MyUtils.addToListUnique(catList, str[j]);
                }
                catItems[type] = MyUtils.arrayListToArray(catList);

                for (j = 0; j < str.length - 1; j++) {
                    for (k = 1; k < str.length; k++) {
                        addPair(list, str[j], str[k]);
                    }
                }
            }

            n = list.size();
            res[type] = new String[n][];
            for (i = 0; i < n; i++) {
                res[type][i] = list.get(i);
            }
        }

        list = new ArrayList<>();
        for (i = 0; i < data.length; i++) {
            str = data[i][0].split(";");
            for (j = 0; j < str.length; j++) {
                str[j] = str[j].trim().toLowerCase();
            }
            
            str2 = data[i][1].split(";");
            for (j = 0; j < str2.length; j++) {
                str2[j] = str2[j].trim().toLowerCase();
            }
            

            for (j = 0; j < str.length; j++) {
                for (k = 0; k < str2.length; k++) {
                    addPair(list, str[j], str2[k]);
                }
            }
        }

        type = 2;
        n = list.size();
        res[type] = new String[n][];
        for (i = 0; i < n; i++) {
            res[type][i] = list.get(i);
        }
        
        String[][] temp = res[0];
        res[0] = res[1];
        res[1] = temp;

        return res;

    }

    public static void addPair(ArrayList<String[]> list, String s1, String s2) {
        int i, n = list.size();
        String[] str;
        for (i = 0; i < n; i++) {
            str = list.get(i);
            if (str[0].equals(s1) && str[1].equals(s2) || str[0].equals(s2) && str[1].equals(s1)) {
                return;
            }
        }

        str = new String[2];
        str[0] = s1;
        str[1] = s2;
        list.add(str);
    }
    
    
    

}
