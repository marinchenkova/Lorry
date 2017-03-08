package ru.marinchenko.lorry;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import ru.marinchenko.lorry.util.Net;

public class MainActivity extends Activity {

    private NetListAdapter netListAdapter;
    private Settings settings = new Settings();

    private ArrayList<Net> testNets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
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

    private void init(){
        testNets.add(new Net("Wi-Fi Net 1"));
        testNets.add(new Net("LV-12345678"));
        testNets.add(new Net("LV-12345679"));
        testNets.add(new Net("Wi-Fi Net 2"));
        netListAdapter = new NetListAdapter(this, testNets);

        ListView netListView = (ListView) findViewById(R.id.netList);
        netListView.setAdapter(netListAdapter);
    }

    /**
     * Вызывается при нажатии кнопки "Настройки"
     * @param view кнопка
     */
    public void toSettings(View view){
        //TODO toSettings()
    }


    /**
     * Вызывается при нажатии кнопки "НАЙТИ СЕТЬ"
     * @param view кнопка
     */
    public ArrayList<Net> findNets(View view){
        //TODO findNets()
        return null;
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


}
