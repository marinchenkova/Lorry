package ru.marinchenko.lorry.util;

import android.net.wifi.WifiManager;


/**
 * Сохранение и восстановление настроек Wi-Fi.
 */
public class WifiStateAgent {

    private WifiManager wifiManager;

    private String  wasConnectedSSID = "<unknown>";
    private int wasConnectedId = -2;
    private boolean wasEnabled = false;
    private boolean saved = false;


    public WifiStateAgent(WifiManager wm){ wifiManager = wm; }

    /**
     * Сохранение состояния Wi-Fi.
     */
    public void save(){
        wasEnabled = wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED;

        if(wasEnabled) {
            wasConnectedSSID = wifiManager.getConnectionInfo().getSSID();
            wasConnectedId = wifiManager.getConnectionInfo().getNetworkId();
        }

        saved = true;
    }

    /**
     * Включение Wi-Fi, если он был выключен.
     */
    public void wifiOn(){ if(!wasEnabled) wifiManager.setWifiEnabled(true); }

    /**
     * Восстановление сохраненного состояния Wi-Fi.
     */
    public void restore(){
        if(saved){
            if(!wasEnabled) wifiManager.setWifiEnabled(false);
            else if(!wifiManager.getConnectionInfo().getSSID().equals(wasConnectedSSID)) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(wasConnectedId, true);
                wifiManager.reconnect();
            }
        }
    }
}
