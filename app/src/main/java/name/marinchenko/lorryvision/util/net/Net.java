package name.marinchenko.lorryvision.util.net;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Net implementation.
 */

public class Net extends NetConfig
                 implements Comparable<Net> {

    private final NetType type;
    private final long detectTime;

    private final ArrayList<Integer> level = new ArrayList<>();
    private boolean connected = false;
    private int connectMoment = -1;
    private int detachMoment = -1;

    public Net(final String ssid,
               final String bssid,
               final String caps,
               final String password,
               final int signal) {
        super(ssid, bssid, caps, password);
        this.type = NetBuffer.getType(ssid);
        this.level.add(signal);
        this.detectTime = System.currentTimeMillis();
    }

    public NetConfig wrapConfig() {
        return new NetConfig(
                this.ssid,
                this.bssid,
                this.caps,
                this.password
        );
    }

    @Override
    public int compareTo(@NonNull Net net) {
        if (this.type == NetType.wifiNetwork && net.getType() == NetType.wifiNetwork) {
            return this.ssid.compareToIgnoreCase(net.getSsid());

        } else if (this.type == NetType.lorryNetwork && net.getType() == NetType.wifiNetwork) {
            return -1;

        } else if (this.type == NetType.wifiNetwork && net.getType() == NetType.lorryNetwork) {
            return 1;

        } else {
            return Integer.signum(net.getLevel() - this.getLevel()) +
                    2 * Long.signum(this.detectTime - net.getDetectTime());
        }
    }

    public NetType getType() { return this.type; }
    public int getLevel() { return this.level.get(this.level.size() - 1); }
    public int getConnectMoment() { return this.connectMoment; }
    public int getDetachMoment() { return this.detachMoment; }
    public long getDetectTime() { return this.detectTime; }
    public boolean wasConnected() { return this.connected; }

    public int getSignalIcon() {
        final int level = this.getLevel();
        if (level > -56) return 4;
        else if (level > -67) return 3;
        else if (level > -78) return 2;
        else if (level > -89) return 1;
        else return 0;
    }

    public int getLastTimeMeanLevel(final int sec) {
        if (this.level.size() <= sec) return -100;
        else {
            int sum = 0;
            for (int i = this.level.size() - 1; i > this.level.size() - sec - 1; i--) {
                sum += this.level.get(i);
            }
            return sum / sec;
        }
    }

    public void addLevel(final int level) { this.level.add(level); }

    public void connected() {
        this.connected = true;
        this.connectMoment = this.level.size();
    }

    public void detached() {
        this.detachMoment = this.level.size();
    }
}
