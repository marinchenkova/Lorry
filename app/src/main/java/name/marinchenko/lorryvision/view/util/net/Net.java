package name.marinchenko.lorryvision.view.util.net;

/**
 * Created by Valentin on 25.01.2018.
 */

public class Net {

    private final String id;
    private final NetType type;
    private int signal;

    public Net(final String id,
               final NetType type,
               final int signal) {
        this.id = id;
        this.type = type;
        this.signal = signal;
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

    public void setSignal(final int signal) {
        this.signal = signal;
    }
}
