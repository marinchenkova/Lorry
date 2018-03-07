package name.marinchenko.lorryvision.util.net;

import android.net.wifi.ScanResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * Created by Valentin on 06.03.2018.
 */

public class ScanResultParser {

    private final static Pattern LORRY = Pattern.compile("LV-[0-9]{8}");

    public static List<Net> getNets(final List<ScanResult> results) {
        final List<Net> nets = new ArrayList<>();
        final List<Net> lorries = new ArrayList<>();

        for (ScanResult s : results) {
            final Net net = new Net(s.SSID, getType(s.SSID), getSignalIcon(s.level));
            if (net.getType() == NetType.lorryNetwork) lorries.add(net);
            else nets.add(net);
        }

        Collections.sort(nets);
        Collections.sort(lorries);
        lorries.addAll(nets);

        return lorries;
    }

    public static NetType getType(final String name){
        Matcher matcher = LORRY.matcher(name);
        return matcher.matches() ? NetType.lorryNetwork : NetType.wifiNetwork;
    }

    public static int getSignalIcon(final int level) {
        if (level > -60) return 4;
        else if (level > -70) return 3;
        else if (level > -80) return 2;
        else if (level > -85) return 1;
        else return 0;
    }

}
