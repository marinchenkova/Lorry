package name.marinchenko.lorryvision.view.util.net;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import name.marinchenko.lorryvision.R;


public class NetlistAdapter extends BaseAdapter {

    private final ArrayList<Net> nets = new ArrayList<>();
    private final LayoutInflater inflater;

    public NetlistAdapter(final Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.nets.size();
    }

    @Override
    public Object getItem(int i) {
        return this.nets.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i,
                        final View convertView,
                        final ViewGroup viewGroup) {
        final View view =
                convertView == null
                        ? inflater.inflate(R.layout.item_netlist, viewGroup, false)
                        : convertView;

        final Net net = this.nets.get(i);
        final String netId = net.getId();
        final NetType type = net.getType();
        final int signal = net.getSignal();

        final int list[] = new int[]{
                R.drawable.ic_wifi_0,
                R.drawable.ic_wifi_1,
                R.drawable.ic_wifi_2,
                R.drawable.ic_wifi_3,
                R.drawable.ic_wifi_4
        };

        ((ImageView) view.findViewById(R.id.netList_imageView_typeOfNet)).setImageResource(
                type == NetType.lorryNetwork
                        ? R.drawable.ic_type_lorry
                        : R.drawable.ic_type_wifi
        );
        ((TextView) view.findViewById(R.id.netList_textView_netId)).setText(netId);
        ((ImageView) view.findViewById(R.id.netList_imageView_signal)).setImageResource(list[signal]);

        return view;
    }

    public void update(final List<Net> newList) {
        this.nets.clear();
        this.nets.addAll(newList);
    }
}
