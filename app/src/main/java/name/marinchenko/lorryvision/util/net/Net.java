package name.marinchenko.lorryvision.util.net;

import android.support.annotation.NonNull;

/**
 * Net implementation.
 */

public class Net implements Comparable<Net> {

    private final String ssid;
    private final String bssid;
    private final String caps;
    private final String password;
    private final NetType type;
    private int level;
    private boolean connected = false;

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
        this.level = signal;
    }

    @Override
    public int compareTo(@NonNull Net net) {
        return (net.getLevel() - this.level) +
                (net.getType() == NetType.lorryNetwork ? 1000 : 0);
    }

    public String getSsid() { return this.ssid; }
    public String getBssid() { return this.bssid; }
    public String getCaps() { return this.caps; }
    public String getPassword() { return this.password; }
    public NetType getType() { return this.type; }

    public int getLevel() { return this.level; }
    public boolean wasConnected() { return this.connected; }

    public void setLevel(final int level) { this.level = level; }
    public void setConnected() { this.connected = true; }

    public int getSignalIcon() {
        if (this.level > -56) return 4;
        else if (this.level > -67) return 3;
        else if (this.level > -78) return 2;
        else if (this.level > -89) return 1;
        else return 0;
    }
}
