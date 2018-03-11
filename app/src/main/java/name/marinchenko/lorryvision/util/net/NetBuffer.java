package name.marinchenko.lorryvision.util.net;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import name.marinchenko.lorryvision.util.debug.NetStore;


/**
 * ScanResult parsing class to produce Net object.
 */

public class NetBuffer {

    private final static Pattern LORRY = Pattern.compile("LV-[0-9]{8}");

    private List<Net> nets = new ArrayList<>();
    private List<Net> lorries = new ArrayList<>();
    private List<Net> previousLorries = new ArrayList<>();

    public NetBuffer() {}

    public List<Net> getNets(final List<ScanResult> results,
                             final Context context) {
        updateAndRemoveNets(results);
        addNets(results, context);

        Collections.sort(this.nets);

        this.previousLorries = this.lorries;
        this.lorries = getLorries(this.nets);

        return this.nets;
    }

    public List<Net> getLorries() { return this.lorries; }

    public boolean lorriesChanged() {
        if (this.lorries.size() == 0) return false;
        if (this.lorries.size() != this.previousLorries.size()) return true;
        for (int i = 0; i < this.lorries.size(); i++) {
            if (!this.lorries.get(i).getSsid().equals(this.previousLorries.get(i).getSsid())) {
                return true;
            }
        }
        return false;
    }

    public void setConnected(final String ssid) {
        for (Net net : this.nets) {
            if (net.getSsid().equals(ssid)) net.connected();
        }
    }

    @Nullable
    public Net search(final String ssid) {
        for (Net net : this.nets) {
            if (net.getSsid().equals(ssid)) return net;
        }
        return null;
    }

    public static String getPassword(final Context context,
                                     final String ssid) {
        //TODO password generator for lorry networks
        return NetStore.getPassword(context, ssid);
    }

    public static NetType getType(final String name){
        //Matcher matcher = LORRY.matcher(name);
        //return matcher.matches() ? NetType.lorryNetwork : NetType.wifiNetwork;
        return name.equals("ASUS-9840") ? NetType.lorryNetwork : NetType.wifiNetwork;
        //return NetType.wifiNetwork;
    }

    private void updateAndRemoveNets(final List<ScanResult> results) {
        out : for (int i = 0; i < this.nets.size(); i++) {
            final Net net = this.nets.get(i);
            for (ScanResult s : results) {
                if (net.getSsid().equals(s.SSID)) {
                    net.addLevel(s.level);
                    continue out;
                }
            }
            this.nets.remove(i);
        }
    }

    private void addNets(final List<ScanResult> results,
                         final Context context) {
        out : for (ScanResult s : results) {
            for (Net net : this.nets) {
                if (net.getSsid().equals(s.SSID)) continue out;
            }
            if (!s.SSID.equals("")) {
                final Net net = new Net(
                        s.SSID,
                        s.BSSID,
                        s.capabilities,
                        getPassword(context, s.SSID),
                        s.level
                );
                this.nets.add(net);
            }
        }
    }

    private static List<Net> getLorries(final List<Net> nets) {
        final List<Net> lorries = new ArrayList<>();
        for (Net net : nets) {
            if (net.getType() == NetType.lorryNetwork) lorries.add(net);
        }
        return lorries;
    }
}
