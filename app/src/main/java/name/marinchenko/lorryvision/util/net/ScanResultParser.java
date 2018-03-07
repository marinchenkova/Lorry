package name.marinchenko.lorryvision.util.net;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import name.marinchenko.lorryvision.util.debug.NetStore;


/**
 * ScanResult parsing class to produce Net object.
 */

public class ScanResultParser {

    private final static Pattern LORRY = Pattern.compile("LV-[0-9]{8}");

    public static List<Net> getNets(final Context context,
                                    final List<ScanResult> results) {
        final List<Net> nets = new ArrayList<>();

        for (ScanResult s : results) {
            final Net net = new Net(
                    s.SSID,
                    s.BSSID,
                    s.capabilities,
                    NetStore.getPassword(context, s.SSID),
                    getSignalIcon(s.level)
            );
            nets.add(net);
        }
        Collections.sort(nets);

        return nets;
    }

    public static NetType getType(final String name){
        Matcher matcher = LORRY.matcher(name);
        return matcher.matches() ? NetType.lorryNetwork : NetType.wifiNetwork;
    }

    public static int getSignalIcon(final int level) {
        if (level > -56) return 4;
        else if (level > -67) return 3;
        else if (level > -78) return 2;
        else if (level > -89) return 1;
        else return 0;
    }

}
