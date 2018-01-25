package name.marinchenko.lorryvision.view.demoTest;

import java.util.ArrayList;

import name.marinchenko.lorryvision.view.util.net.Net;
import name.marinchenko.lorryvision.view.util.net.NetType;

/**
 * Created by Valentin on 25.01.2018.
 */

public class TestBase {

    public static ArrayList<Net> getNetlistForListViewTest() {
        final ArrayList<Net> list = new ArrayList<>();

        final String strs[] = new String[]{
                "LorryVision network 1", "LorryVision network 2",
                "Usual network 1", "Usual network 2", "Usual network 3",
                "Usual network 4", "Usual network 5", "Scrolling demo network",
                "Network with too long name"};

        for (int i = 0; i < strs.length; i++) {
            list.add(new Net(
                    strs[i],
                    i < 2 ? NetType.lorryNetwork : NetType.wifiNetwork,
                    Math.abs(i - 4)
            ));
        }

        return list;
    }
}
