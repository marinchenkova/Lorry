package ru.marinchenko.lorry.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.marinchenko.lorry.R;


public class NetListAdapter extends BaseAdapter{

    private LayoutInflater layoutInf;
    private ArrayList<Net> recs = new ArrayList<>();
    private ArrayList<Net> nets = new ArrayList<>();

    public NetListAdapter(Context ctx) {
        layoutInf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //Количество элементов
    @Override
    public int getCount() { return nets.size(); }

    //Элемент по позиции
    @Override
    public Object getItem(int position) { return nets.get(position); }

    //ID по позиции
    @Override
    public long getItemId(int position) { return position; }

    //Пункт списка
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int recIcon = R.drawable.button_net_rec;
        int wifiIcon = R.drawable.button_net_wifi;
        int wifiIcon3 = R.drawable.button_net_wifi_3;
        int wifiIcon2 = R.drawable.button_net_wifi_2;
        int wifiIcon1 = R.drawable.button_net_wifi_1;
        int wifiIcon0 = R.drawable.button_net_wifi_0;
        int signalIcon;

        String net = nets.get(position).getSsid();
        int level = nets.get(position).getLevel();

        signalIcon = wifiIcon3;
        if(level < -65) signalIcon = wifiIcon2;
        else if(level < -90) signalIcon = wifiIcon1;
        else if(level < -100) signalIcon = wifiIcon0;

        View view = convertView;

        if (view == null) {
            view = layoutInf.inflate(R.layout.netlist_item, parent, false);
        }

        ((TextView) view.findViewById(R.id.netList_item_name)).setText(net);
        ((ImageView) view.findViewById(R.id.netList_item_image)).setImageResource(
                NetConfig.ifRec(net) ? recIcon : wifiIcon);
        ((ImageView) view.findViewById(R.id.netList_item_signal)).setImageResource(signalIcon);

        return view;
    }

    /**
     * Обновление списка сетей {@link NetListAdapter#recs}.
     * @param newNets новый список сетей
     */
    public void updateNets(ArrayList<String> newNets, ArrayList<Integer> signals){
        List<Net> wifi = new ArrayList<>();
        nets.clear();
        recs.clear();

        for(int i = 0; i < newNets.size(); i++){
            Net net = new Net(newNets.get(i), signals.get(i));
            if(NetConfig.ifRec(newNets.get(i))) recs.add(net);
            else wifi.add(net);
        }

        nets.addAll(recs);
        nets.addAll(wifi);
    }
}
