/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package adviceconflict;

import static adviceconflict.PreProcess.addTopicsToList;
import java.util.HashMap;
import utils.FileUtils;
import utils.MC;

/**
 *
 * @author mm5gg
 */
public class ConflictFinder {

    public static String getIntraAppConflict(String[][][] data, String[] topics, int[][][] adviceGrid) throws Exception {
        StringBuilder sb = new StringBuilder();
        int app, line1, line2, lineCount, i, j, k;
        int appCount = data.length;
        int itemCount = topics.length;
        boolean conflict;
        String[] appNames = FileUtils.getAppNames(MC.SRC_FOLDER_PATH);
        String conflictTopics="";
                
        sb.append("App, Advice 1 Line No, Advice 2 Line No, Advice 1, Advice 2, Conflict Topics\n");

        for (app = 0; app < appCount; app++) {
            lineCount = data[app].length;
            for (line1 = 0; line1 < lineCount - 1; line1++) {
                for (line2 = line1 + 1; line2 < lineCount; line2++) {
                    conflict = false;
                    conflictTopics="";
                    for (i = 0; i < itemCount; i++) {
                        if (adviceGrid[app][line1][i] == 1 && adviceGrid[app][line2][i] == -1 || adviceGrid[app][line1][i] == -1 && adviceGrid[app][line2][i] == 1) {
                            //System.out.println(app + ": " + line1 + ", " + line2 + ", " + i);
                            conflict = true;
                            //break;
                            conflictTopics += topics[i]+";";
                        }
                    }

                    if (conflict) {
                        sb.append(appNames[app]);
                        sb.append(",");
                        sb.append(data[app][line1][0]);
                        sb.append(",");
                        sb.append(data[app][line2][0]);
                        sb.append(",");
                        sb.append("\""+data[app][line1][1].replace(";", ",").replace("\"", "")+"\"");
                        sb.append(",");
                        sb.append("\""+data[app][line2][1].replace(";", ",").replace("\"", "")+"\"");
                        sb.append(",");
                        sb.append(conflictTopics);
                        sb.append("\n");
                    }

                }
            }

        }

        return sb.toString();
    }

    public static String getInterAppConflict(String[][][] data, String[] topics, int[][][] adviceGrid) throws Exception {
        StringBuilder sb = new StringBuilder();
        int app1, app2, line1, line2, lineCount1, lineCount2, i, j, k;
        int appCount = data.length;
        int itemCount = topics.length;
        boolean conflict;
        String[] appNames = FileUtils.getAppNames(MC.SRC_FOLDER_PATH);
        String conflictTopics="";

        sb.append("App 1, App 2, Advice 1 Line No, Advice 2 Line No, Advice 1, Advice 2, Conflict Topics\n");

        for (app1 = 0; app1 < appCount - 1; app1++) {
            for (app2 = app1 + 1; app2 < appCount; app2++) {
                lineCount1 = data[app1].length;
                lineCount2 = data[app2].length;
                for (line1 = 0; line1 < lineCount1; line1++) {
                    for (line2 = 0; line2 < lineCount2; line2++) {
                        conflict = false;
                        conflictTopics="";
                        for (i = 0; i < itemCount; i++) {
                            if (adviceGrid[app1][line1][i] == 1 && adviceGrid[app2][line2][i] == -1 || adviceGrid[app1][line1][i] == -1 && adviceGrid[app2][line2][i] == 1) {
                                //System.out.println(app + ": " + line1 + ", " + line2 + ", " + i);
                                conflict = true;
                                //break;
                                //break;
                                conflictTopics += topics[i]+";";
                            }
                        }

                        if (conflict) {
                            sb.append(appNames[app1]);
                            sb.append(",");
                            sb.append(appNames[app2]);
                            sb.append(",");
                            sb.append(data[app1][line1][0]);
                            sb.append(",");
                            sb.append(data[app2][line2][0]);
                            sb.append(",");
                            sb.append("\""+data[app1][line1][1].replace(";", ",").replace("\"", "")+"\"");
                            sb.append(",");
                            sb.append("\""+data[app2][line2][1].replace(";", ",").replace("\"", "")+"\"");
                            sb.append(",");
                            sb.append(conflictTopics);
                            sb.append("\n");
                        }

                    }
                }

            }
        }

        return sb.toString();
    }
    
    
}