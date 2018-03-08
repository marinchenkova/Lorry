package name.marinchenko.lorryvision.util.net;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Net implementation.
 */

public class Net implements Comparable<Net> {

    private final String ssid;
    private final String bssid;
    private final String caps;
    private final String password;
    private final NetType type;

    private final ArrayList<Integer> level = new ArrayList<>();
    private boolean connected = false;
    private int connectMoment = -1;
    private int detachMoment = -1;

    public Net(final String ssid,
               final String bssid,
               final String caps,
               final String password,
               final int signal) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.caps = caps;
        this.password = password;
        this.type = ScanResultParser.getType(ssid);
        this.level.add(signal);
    }

    public ArrayList<String> wrap() {
        final ArrayList<String> config = new ArrayList<>();
        config.add(this.ssid);
        config.add(this.bssid);
        config.add(this.caps);
        config.add(this.password);
        return config;
    }

    @Override
    public int compareTo(@NonNull Net net) {
        return (net.getLevel() - this.getLevel()) +
                (net.getType() == NetType.lorryNetwork ? 1000 : 0);
    }

    public String getSsid() { return this.ssid; }
    public String getBssid() { return this.bssid; }
    public String getCaps() { return this.caps; }
    public String getPassword() { return this.password; }
    public NetType getType() { return this.type; }
    public int getLevel() { return this.level.get(this.level.size() - 1); }
    public int getConnectMoment() { return this.connectMoment; }
    public int getDetachMoment() { return this.detachMoment; }
    public boolean wasConnected() { return this.connected; }

    public int getSignalIcon() {
        final int level = this.getLevel();
        if (level > -56) return 4;
        else if (level > -67) return 3;
        else if (level > -78) return 2;
        else if (level > -89) return 1;
        else return 0;
    }

    public void addLevel(final int level) { this.level.add(level); }

    public void connected() {
        this.connected = true;
        this.connectMoment = this.level.size();
    }

    public void detached() {
        this.detachMoment = this.level.size();
    }

    public int getLastTimeMeanLevel(final int sec) {
        if (this.level.size() < sec) return -100;
        else {
            int sum = 0;
            for (int i = this.level.size() - 1; i > this.level.size() - sec - 1; i--) {
                sum += this.level.get(i);
            }
            return sum / sec;
        }
    }
}
