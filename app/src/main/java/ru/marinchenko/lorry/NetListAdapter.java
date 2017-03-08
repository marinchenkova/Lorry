package ru.marinchenko.lorry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.marinchenko.lorry.util.Net;



public class NetListAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater layoutInf;

    private ArrayList<Net> nets = new ArrayList<>();
    private ArrayList<Net> recs = new ArrayList<>();


    NetListAdapter(Context ctx, ArrayList<Net> netList) {
        context = ctx;
        updateNets(netList);
        layoutInf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    //Количество элементов
    @Override
    public int getCount() {
        return nets.size();
    }

    //Элемент по позиции
    @Override
    public Object getItem(int position) {
        return nets.get(position);
    }

    //ID по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    //Пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int recIcon = R.drawable.button_net_rec;
        int wifiIcon = R.drawable.button_net_wifi;

        Net net = nets.get(position);
        View view = convertView;

        if (view == null) {
            view = layoutInf.inflate(R.layout.netlist_item, parent, false);
        }

        ((TextView) view.findViewById(R.id.netList_item_name)).setText(net.getId());
        ((ImageView) view.findViewById(R.id.netList_item_image)).setImageResource(
                net.isRec() ? recIcon : wifiIcon);

        return view;
    }

    /**
     * Обновление списков сетей {@link NetListAdapter#nets} и {@link NetListAdapter#recs}.
     * @param ids массив имен сетей
     */
    public void updateNets(String[] ids){
        ArrayList<Net> newNets = new ArrayList<>();
        nets.clear();
        recs.clear();

        for (String id : ids) {
            newNets.add(new Net(id));
        }

        for(Net n: newNets){
            if(n.isRec()) recs.add(n);
            else nets.add(n);
        }

        nets.addAll(0, recs);
    }

    /**
     * Обновление списков сетей {@link NetListAdapter#nets} и {@link NetListAdapter#recs}.
     * @param netList новый список сетей
     */
    public void updateNets(ArrayList<Net> netList){
        ArrayList<Net> wifi = new ArrayList<>();
        nets.clear();
        recs.clear();

        for(Net n: netList){
            if(n.isRec()) recs.add(n);
            else wifi.add(n);
        }

        nets.addAll(recs);
        nets.addAll(wifi);
    }
}
