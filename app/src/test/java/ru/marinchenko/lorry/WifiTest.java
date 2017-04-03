package ru.marinchenko.lorry;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ru.marinchenko.lorry.util.WifiConfigurator;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class WifiTest {

    @Mock
    private WifiManager mWifiManager;
    @Mock
    private Context mContext;

    private MainActivity mainActivity;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        ScanResult mNet = buildScanResult("LV-12345678", "01:02:03:04:05:06", 70);
        WifiConfigurator wifiConf = new WifiConfigurator();
        wifiConf.configure(mNet);
        mWifiManager.addNetwork(wifiConf.getConfiguredNet());
        mWifiManager.setWifiEnabled(false);

        mContext = ShadowApplication.getInstance().getApplicationContext();
        when(mContext.getSystemService(Context.WIFI_SERVICE)).thenReturn(mWifiManager);
        mainActivity = Robolectric.setupActivity(MainActivity.class);
    }


    @Test
    public void test(){
        ScanResult mNet = buildScanResult("LV-12345678", "01:02:03:04:05:06", 70);
        mainActivity.toNet(mNet);
        mainActivity.authenticate("password");

    }


    private ScanResult buildScanResult(String SSID, String BSSID, int level){
        Constructor<ScanResult> ctor;
        ScanResult sr = null;

        try {
            ctor = ScanResult.class.getDeclaredConstructor(null);
            ctor.setAccessible(true);
            sr = ctor.newInstance(null);

            sr.SSID = SSID;
            sr.BSSID = BSSID;
            sr.level = level;
            sr.capabilities = "open";

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return sr;
    }

}