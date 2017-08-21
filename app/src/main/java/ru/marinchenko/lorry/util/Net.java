package ru.marinchenko.lorry.util;


public class Net {
    private String ssid;
    private int level;

    public Net(String s, int l){
        ssid = s;
        level = l;
    }

    public String getSsid() {
        return ssid;
    }

    public int getLevel() {
        return level;
    }
}
