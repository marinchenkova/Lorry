package name.marinchenko.lorryvision.view.util;


import java.util.ArrayList;

import name.marinchenko.lorryvision.view.util.net.Net;

/**
 * Created by Valentin on 26.01.2018.
 */

public class SavingBundle {

    private ArrayList<Net> netlist;
    private boolean isDrawerOpened;

    public SavingBundle() {}


    public ArrayList<Net> getNetlist() {
        return this.netlist;
    }

    public void setNetlist(ArrayList<Net> netlist) {
        this.netlist = netlist;
    }

    public boolean isDrawerOpened() {
        return this.isDrawerOpened;
    }

    public void setDrawerOpened(boolean drawerOpened) {
        this.isDrawerOpened = drawerOpened;
    }
}
