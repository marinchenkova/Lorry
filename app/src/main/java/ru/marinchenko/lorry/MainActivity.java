package ru.marinchenko.lorry;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.marinchenko.lorry.util.Net;

public class MainActivity extends Activity {

    private NetListAdapter netListAdapter;
    private Settings settings = new Settings();
    private WifiManager wifiManager;

    private ArrayList<Net> nets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        netListAdapter = new NetListAdapter(this, nets);

        ListView netListView = (ListView) findViewById(R.id.netList);
        netListView.setAdapter(netListAdapter);
    }

    /** Called when the user clicks the Send button */
/*    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
*/

    private void testNets(){
        nets.add(new Net("Wi-Fi Net 1"));
        nets.add(new Net("LV-12345678"));
        nets.add(new Net("LV-12345679"));
        nets.add(new Net("Wi-Fi Net 2"));
    }

    /**
     * Вызывается при нажатии кнопки "Настройки"
     * @param view кнопка
     */
    public void toSettings(View view){
        //TODO toSettings()
    }

    /**
     * Вызывается при переключении флажка "Подключаться автоматически"
     * @param view флажок
     */
    public void setAutoConnect(View view){
        //TODO setAutoConnect()
    }

    /**
     * Вывод информации о времени обновления списка сетей
     * @param view текстовое поле
     */
    public void showUpdateInfo(View view){
        //TODO showUpdateInfo()
    }

    /**
     * Вызывается при нажатии кнопки "НАЙТИ СЕТЬ"
     * @param view кнопка
     */
    public void scanWifi(View view){
        wifiManager.startScan();
        List<ScanResult> scanResults = wifiManager.getScanResults();
        nets.clear();

        for(ScanResult s : scanResults){
            nets.add(new Net(s.SSID));
        }

        netListAdapter.updateNets(nets);
        netListAdapter.notifyDataSetChanged();
    }

    /**
     * Вызывается при нажатии на сеть из списка доступных сетей. Если сеть раздается
     * видеорегистратором, приложение перейдет к просмотру его камеры.
     */
    public void toNet(View view){
        TextView tv = (TextView) view.findViewById(R.id.netList_item_name);
        if(Net.ifRec(tv.getText().toString())){
            //TODO toNet()
            ((TextView) view.findViewById(R.id.netList_item_name)).setText("YES!");
        }
    }
}
