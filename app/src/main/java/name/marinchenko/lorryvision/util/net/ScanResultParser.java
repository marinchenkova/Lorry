package name.marinchenko.lorryvision.util.net;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import name.marinchenko.lorryvision.util.debug.NetStore;


/**
 * ScanResult parsing class to produce Net object.
 */

public class ScanResultParser {

    private final static Pattern LORRY = Pattern.compile("LV-[0-9]{8}");

    private List<Net> nets = new ArrayList<>();
    private List<Net> lorries = new ArrayList<>();

    public ScanResultParser() {}

    public List<Net> getNets(final Context context,
                             final List<ScanResult> results) {
        for (ScanResult s : results) {
            if (!updateNet(s)) {
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
        Collections.sort(nets);
        this.lorries = getLorries(this.nets);

        return this.nets;
    }

    private boolean updateNet(final ScanResult scanResult) {
        for (Net net : this.nets) {
            if (net.getSsid().equals(scanResult.SSID)) {
                net.setLevel(scanResult.level);
                return true;
            }
        }
        return false;
    }

    private static List<Net> getLorries(final List<Net> nets) {
        final List<Net> lorries = new ArrayList<>();
        for (Net net : nets) {
            if (net.getType() == NetType.lorryNetwork) lorries.add(net);
        }
        return lorries;
    }

    public List<Net> getLorries() { return this.lorries; }

    public static String getPassword(final Context context,
                                     final String ssid) {
        //TODO password generator for lorry networks
        return NetStore.getPassword(context, ssid);
    }

    public static NetType getType(final String name){
        //Matcher matcher = LORRY.matcher(name);
        //return matcher.matches() ? NetType.lorryNetwork : NetType.wifiNetwork;
        return name.equals("ASUS-9840") ? NetType.lorryNetwork : NetType.wifiNetwork;
    }
}
