package ru.marinchenko.lorry;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.widget.ListView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


import ru.marinchenko.lorry.util.NetConfig;
import ru.marinchenko.lorry.util.WifiConfigurator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class WifiTest {

    @Mock
    private ScanResult mScan;
    @Mock
    private Context mContext;


    private WifiManager mWifiManager;
    private WifiManager spyWifiManager;

    private MainActivity mainActivity;
    private List<ScanResult> scans;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        int scansNum = 1;
        scans = buildScanResults(mScan, scansNum);

        mWifiManager = buildWifiManager();
        spyWifiManager = spy(mWifiManager);

        spyWifiManager.setWifiEnabled(true);
        doReturn(scans).when(spyWifiManager).getScanResults();
        doReturn(true).when(spyWifiManager).disconnect();
        doReturn(true).when(spyWifiManager).reconnect();

        mainActivity = Robolectric.setupActivity(MainActivity.class);
        mainActivity.setWifiManager(spyWifiManager);
    }


    @Test
    public void test(){
        mainActivity.scanWifi();

        ListView netList = (ListView) mainActivity.findViewById(R.id.netList);
        netList.performItemClick(netList.getChildAt(0), 0, netList.getItemIdAtPosition(0));

        mainActivity.authenticate("");
    }

    private WifiManager buildWifiManager(){
        Constructor<WifiManager> ctor;
        WifiManager wm = null;

        try {
            ctor = WifiManager.class.getDeclaredConstructor(null);
            ctor.setAccessible(true);
            wm = ctor.newInstance(null);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return wm;
    }

    private List<ScanResult> buildScanResults(ScanResult scan, int num) {
        List<ScanResult> scans = new ArrayList<>();
        for(int i = 0; i < num; i++)
            scans.add(randomScanResult(scan));
        return scans;
    }

    private ScanResult randomScanResult(ScanResult scan){
        scan.SSID = NetConfig.generateSSID();
        scan.BSSID = NetConfig.generateBSSID();
        scan.level = (int) (-60 + Math.random() * 20);
        scan.capabilities = "[OPEN]";
        return scan;
    }

    private WifiConfiguration configure(int id){
        WifiConfigurator wifiConf = new WifiConfigurator();
        wifiConf.configure(scans.get(id));
        return wifiConf.getConfiguredNet();
    }
}