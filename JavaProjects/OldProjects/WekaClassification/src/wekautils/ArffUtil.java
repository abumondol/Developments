package wekautils;

import myutils.IndexUtils;

/**
 *
 * @author mm5gg
 */
public class ArffUtil {
    
    public static String csvToArff(String[][] csvData, int[] featureIndices, boolean isHeaderAvailable, String[] classList) throws Exception {
        int total_rows = csvData.length;
        int total_cols = csvData[0].length;        
        int i,j, start = 0;
        
        if(featureIndices==null)
            featureIndices = IndexUtils.generateIndices(0, total_cols-2); //the last column is not feature, it is class label
        int fCount = featureIndices.length;
        
        String[] attributeList = new String[fCount + 1]; //the additional one is the class label        
        if(isHeaderAvailable){
            for (i = 0; i < fCount; i++) {
                attributeList[i] = csvData[0][featureIndices[i]];
            }            
            attributeList[i] = csvData[0][total_cols - 1];
            start = 1;
        }else{
            for (i = 0; i < fCount; i++) {
                attributeList[i] = "a"+featureIndices[i];
            }            
            attributeList[i] = "class";
        }
        
        if(classList == null){
            classList = getClassList(csvData, isHeaderAvailable, total_cols-1); // classColumnIndex = total_cols - 1
            //PrintUtils.printArray(classList, null, ", ");
        }

        StringBuilder sb = getArffHeader(attributeList, classList);

        for (i = 1; i < total_rows; i++) {
            for (j = 0; j < fCount; j++) {
                sb.append(csvData[i][featureIndices[j]]);
                sb.append(",");
            }
            sb.append(csvData[i][total_cols - 1]);
            sb.append("\n");
        }

        return sb.toString();
    }

    private static StringBuilder getArffHeader(String[] attributeList, String[] classList) {
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

    private static String[] addClass(String[] classList, String className) {
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
    
    
    public static String[] getClassList(String[][] csvData, boolean isHeaderAvaialble, int classColumnIndex){
        int total_rows = csvData.length;
        int total_cols = csvData[0].length;
        int i;
        
        String[] classList = new String[1];
        if(!isHeaderAvaialble)
            classList[0] = csvData[0][classColumnIndex];
        
        for (i = 1; i < total_rows; i++) {
            classList = addClass(classList, csvData[i][classColumnIndex]);
        }
        
        //java.util.Arrays.sort(classList);        
        return classList;
    }
    
    
    
}
