package ru.marinchenko.lorry.util;

import android.content.Context;
import android.net.wifi.ScanResult;
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
    private List<ScanResult> recs = new ArrayList<>();
    private List<ScanResult> nets = new ArrayList<>();

    public NetListAdapter(Context ctx, List<ScanResult> netList) {
        updateNets(netList);
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

        ScanResult net = nets.get(position);
        View view = convertView;

        if (view == null) {
            view = layoutInf.inflate(R.layout.netlist_item, parent, false);
        }

        ((TextView) view.findViewById(R.id.netList_item_name)).setText(net.SSID);
        ((ImageView) view.findViewById(R.id.netList_item_image)).setImageResource(
                NetConfig.ifRec(net.SSID) ? recIcon : wifiIcon);

        return view;
    }

    /**
     * Обновление списка сетей {@link NetListAdapter#recs}.
     * @param newNets новый список сетей
     */
    public void updateNets(List<ScanResult> newNets){
        List<ScanResult> wifi = new ArrayList<>();
        nets.clear();
        recs.clear();

        for(ScanResult s: newNets){
            if(NetConfig.ifRec(s.SSID)) recs.add(s);
            else wifi.add(s);
        }

        nets.addAll(recs);
        nets.addAll(wifi);
    }
}
