package name.marinchenko.lorryvision.util.net;

import android.support.annotation.NonNull;

/**
 * Created by Valentin on 25.01.2018.
 */

public class Net implements Comparable<Net> {

    private final String id;
    private final NetType type;
    private int signal;
    private boolean connected;

    public Net(final String id,
               final NetType type,
               final int signal) {
        this.id = id;
        this.type = type;
        this.signal = signal;
        this.connected = false;
    }

    public String getId() {
        return id;
    }

    public NetType getType() {
        return type;
    }

    public int getSignal() {
        return signal;
    }

    public boolean wasConnected() {
        return connected;
    }

    public void setSignal(final int signal) {
        this.signal = signal;
    }

    public void setConnected() {
        this.connected = true;
    }

    @Override
    public int compareTo(@NonNull Net net) {
        return net.getSignal() - this.signal;
    }
}
