package ru.marinchenko.lorry;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

/**
 * Класс предназначен для формирования конфигурации сети {@link WifiConfiguration} по параметрам
 * объекта {@link ScanResult}.
 */

public class WifiAuth {

    /**
     * Задание основных полей конфигурации сети.
     * @param config сеть
     * @param s сеть, полученная с помощью {@link WifiManager#startScan()}
     * @return результат конфигурирования сети
     */
    public static WifiConfiguration configure(WifiConfiguration config, ScanResult s){
        config.SSID = String.format("\"%s\"", s.SSID);
        config.priority = 1;
        config.BSSID = s.BSSID;
        config.allowedKeyManagement.set(keyMgmt(s.capabilities));
        config.allowedGroupCiphers.set(groupCiphers(s.capabilities));
        config.allowedAuthAlgorithms.set(authAlgorithms(s.capabilities));
        config.status = WifiConfiguration.Status.ENABLED;
        return config;
    }

    public static int groupCiphers(String s){
        if(s.contains("TKIP")){
            return WifiConfiguration.GroupCipher.TKIP;
        } else if(!s.contains("CCMP")){
            return WifiConfiguration.GroupCipher.CCMP;
        } else if(s.contains("WEP")) {
            return WifiConfiguration.GroupCipher.WEP40;
        }
        return WifiConfiguration.GroupCipher.WEP104;
    }

    public static int pairwiseCipher(String s){
        if(s.contains("TKIP")){
            return WifiConfiguration.PairwiseCipher.TKIP;
        }
        return WifiConfiguration.PairwiseCipher.CCMP;
    }

    public static int protocol(String s){
        if(!s.contains("WPA2")) {
            return WifiConfiguration.Protocol.RSN;
        }
        return WifiConfiguration.Protocol.WPA;
    }

    public static int keyMgmt(String s) {
        if(s.contains("LEAP") || s.contains("EAP") || s.contains("WEP")){
            return WifiConfiguration.KeyMgmt.IEEE8021X;
        } else if(!s.contains("WPA")){
            return WifiConfiguration.KeyMgmt.NONE;
        } else if(s.contains("PSK")) {
            return WifiConfiguration.KeyMgmt.WPA_PSK;
        }
        return WifiConfiguration.KeyMgmt.WPA_EAP;
    }

    public static int authAlgorithms(String s){
        if(s.contains("WPA")){
            return WifiConfiguration.AuthAlgorithm.OPEN;
        } else if(s.contains("LEAP") || s.contains("EAP")){
            return WifiConfiguration.AuthAlgorithm.LEAP;
        }
        return WifiConfiguration.AuthAlgorithm.SHARED;
    }
}
