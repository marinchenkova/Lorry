package ru.marinchenko.lorry.util;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;

/**
 * Класс предназначен для формирования конфигурации сети {@link WifiConfiguration} по параметрам
 * объекта {@link ScanResult}.
 */

public class WifiConfigurator {

    private WifiConfiguration config = new WifiConfiguration();
    private int type = 0;

    /**
     * Задание основных полей конфигурации сети.
     * @param s сеть Wi-Fi
     */
    public void configure(ScanResult s){
        config.SSID = String.format("\"%s\"", s.SSID);
        config.priority = 40;
        config.BSSID = s.BSSID;

        String cap = s.capabilities;

        //WPA, WPA2 Wi-Fi network
        if(cap.contains("WPA")) {
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

            type = 1;

        //WEP Wi-Fi network
        } else if(cap.contains("WEP")) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

            type = 2;

        //Open Wi-Fi network
        } else {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        }
    }

    public void setPassword(String password){
        switch (type){
            case 1:
                config.preSharedKey = String.format("\"%s\"", password);
                break;

            case 2:
                if (password.matches("[0-9a-fA-F]+")) config.wepKeys[0] = password;
                else config.wepKeys[0] = String.format("\"%s\"", password);
                config.wepTxKeyIndex = 0;
        }
    }

    public WifiConfiguration getConfiguredNet(){ return config; }
}
