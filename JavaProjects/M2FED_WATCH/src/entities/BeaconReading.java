package entities;

public class BeaconReading {

    public long time;
    public String mac;
    public int txPower;
    public int rssi;

    public BeaconReading(long t, String m, int tx, int rs) throws Exception {
        time = t;
        mac = m;
        txPower = tx;
        rssi = rs;
    }

    public BeaconReading(String[] str) throws Exception {
        time = Long.parseLong(str[0]);
        mac = str[2];;
        txPower = Integer.parseInt(str[3]);
        rssi = Integer.parseInt(str[4]);
    }
}
