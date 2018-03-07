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
    private int signal;
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
        this.signal = signal;
    }

    @Override
    public int compareTo(@NonNull Net net) {
        return (net.getSignal() - this.signal) +
                (net.getType() == NetType.lorryNetwork ? 1000 : 0);
    }

    public String getSsid() { return this.ssid; }
    public String getBssid() { return this.bssid; }
    public String getCaps() { return this.caps; }
    public String getPassword() { return this.password; }
    public NetType getType() { return this.type; }

    public int getSignal() { return this.signal; }
    public boolean wasConnected() { return this.connected; }

    public void setSignal(final int signal) { this.signal = signal; }
    public void setConnected() { this.connected = true; }
}
