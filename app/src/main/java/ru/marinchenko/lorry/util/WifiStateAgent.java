package ru.marinchenko.lorry.util;

import android.net.wifi.WifiManager;


/**
 * Сохранение и восстановление настроек Wi-Fi.
 */
public class WifiStateAgent {

    private WifiManager wifiManager;

    private int wasConnectedId = -2;
    private boolean wasEnabled = false;
    private boolean saved = false;


    public WifiStateAgent(WifiManager wm){ wifiManager = wm; }

    /**
     * Сохранение состояния и включение Wi-Fi.
     */
    public void saveAndPrepare(){
        saved = true;
        wasEnabled = wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
        wasConnectedId = -2;
        if(wasEnabled) wasConnectedId = wifiManager.getConnectionInfo().getNetworkId();
        else wifiManager.setWifiEnabled(true);
    }

    /**
     * Восстановление состояния
     * @param reconnect {@code true} если нужно переподключиться
     */
    public void restore(boolean reconnect){
        if(saved){
            if(!wasEnabled) wifiManager.setWifiEnabled(false);
            else if(wasConnectedId != -1 && reconnect) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(wasConnectedId, true);
                wifiManager.reconnect();
            }
        }
    }
}
