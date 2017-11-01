package myutils;

import java.util.Locale;

/**
 *
 * @author mm5gg
 */
public class MyDeviceUtils {

    public static String[] watchListUSC = {"14442D31F8BC4E0", "14432D35F47689C", "14422D3CF36A67A", "14442D34F8B155A",
        "14442D2DF803B0E", "14442D37F7F674A", "14432D22F43D3F8", "14432D17F487D24",
        "14442D33F8BCB1A", "14442D33F8B2F72", "14432D32F4A4EDE", "14422D3BF394278"};

    public static String[] beaconListUSC = {"D4C416BF5385", "C196B85904C4", "FD7676B335A3", "F70B5243E7B3",
        "FC4A94985124", "E5830DF962F0", "F563F3C569D7", "D0D70C19C171",
        "CF247561D166", "C83BF6930EBA", "F6881C4E7AE4", "D61831D4CC96"};

    public static int getWatchIndex(String serial) {
        for (int i = 0; i < watchListUSC.length; i++) {
            if (watchListUSC[i].equalsIgnoreCase(serial)) {
                return i;
            }
        }
        return -1;
    }

    public static int getBeaconIndex(String mac) {
        for (int i = 0; i < beaconListUSC.length; i++) {
            if (beaconListUSC[i].equalsIgnoreCase(mac)) {
                return i;
            }
        }
        return -1;
    }

}
