package ru.marinchenko.lorry;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Pair;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ServiceController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowWifiManager;

import java.util.ArrayList;
import java.util.List;

import ru.marinchenko.lorry.util.NetConfig;
import ru.marinchenko.lorry.services.WifiAgent;
import ru.marinchenko.lorry.util.WifiConfigurator;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.robolectric.Shadows.shadowOf;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class WifiTest {

    @Mock
    private ScanResult mScan;

    private WifiManager mWifiManager;
    private WifiManager spyWifiManager;
    private ShadowWifiManager shadowWifiManager;

    private WifiAgent wifiAgent;
    private ServiceController<WifiAgent> controller;

    private List<ScanResult> scans;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        int scansNum = 2;
        scans = buildScanResults(mScan, scansNum);

        mWifiManager =
                (WifiManager) RuntimeEnvironment.application.getSystemService(Context.WIFI_SERVICE);
        spyWifiManager = spy(mWifiManager);
        shadowWifiManager = shadowOf(spyWifiManager);

        shadowWifiManager.setWifiEnabled(true);
        shadowWifiManager.setScanResults(scans);

        doReturn(true).when(spyWifiManager).disconnect();
        doReturn(true).when(spyWifiManager).reconnect();

        controller = Robolectric.buildService(WifiAgent.class);
        wifiAgent = controller.bind().create().get();
        //wifiAgent.setWifiManager(spyWifiManager);
    }


    @Test
    public void wifiConnected(){
        WifiConfiguration conf1 = configure(scans.get(0));
        WifiConfiguration conf2 = configure(scans.get(1));

        //wifiAgent.authenticate(conf1);
        //wifiAgent.authenticate(conf2);

        Pair<Integer, Boolean> lastEnabled = shadowWifiManager.getLastEnabledNetwork();
        assertThat(lastEnabled).isEqualTo(new Pair<>(1, true));
    }


    private List<ScanResult> buildScanResults(ScanResult scan, int num) {
        List<ScanResult> scans = new ArrayList<>();
        for(int i = 0; i < num; i++)
            scans.add(randomScanResult(scan));
        return scans;
    }


    private ScanResult randomScanResult(ScanResult scan){
        scan.SSID = NetConfig.generateRandomSSID();
        scan.BSSID = NetConfig.generateRandomBSSID();
        scan.level = (int) (-60 + Math.random() * 20);
        scan.capabilities = "[OPEN]";
        return scan;
    }


    private WifiConfiguration configure(ScanResult s){
        WifiConfigurator wifiConf = new WifiConfigurator();
        wifiConf.configure(s);
        return wifiConf.getConfiguredNet();
    }
}