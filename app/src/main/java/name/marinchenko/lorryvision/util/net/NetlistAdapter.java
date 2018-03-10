package name.marinchenko.lorryvision.util.net;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.util.threading.ToastThread;

import static name.marinchenko.lorryvision.activities.main.SettingsFragment.PREF_KEY_DISPLAY_GENERAL;


public class NetlistAdapter extends BaseAdapter {

    private final static int ID_MAX_LENGTH = 21;

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
        final String netId = net.getSsid();
        final NetType type = net.getType();
        final boolean wasConnected = net.wasConnected();

        final int list[] = new int[]{
                R.drawable.ic_wifi_0,
                R.drawable.ic_wifi_1,
                R.drawable.ic_wifi_2,
                R.drawable.ic_wifi_3,
                R.drawable.ic_wifi_4
        };

        if (!wasConnected) {
            view.findViewById(R.id.netList_imageView_was_connected).setVisibility(View.GONE);
        }

        ((ImageView) view.findViewById(R.id.netList_imageView_typeOfNet)).setImageResource(
                type == NetType.lorryNetwork
                        ? R.drawable.ic_type_lorry
                        : R.drawable.ic_type_wifi
        );

        TextView textView = view.findViewById(R.id.netList_textView_netId);
        textView.setText(netId);
        textView.setSelected(netId.length() > ID_MAX_LENGTH);

        ((ImageView) view.findViewById(R.id.netList_imageView_signal))
                .setImageResource(list[net.getSignalIcon()]);

        return view;
    }

    public void update(final Activity activity,
                       final List<Net> newList) {
        this.nets.clear();

        final SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(activity);

        if (!sharedPref.getBoolean(PREF_KEY_DISPLAY_GENERAL, false)) {
            for (Net net : newList) {
                if (net.getType() == NetType.lorryNetwork) this.nets.add(net);
            }
        } else {
            this.nets.addAll(newList);
        }
    }
}
