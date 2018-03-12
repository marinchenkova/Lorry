package name.marinchenko.lorryvision.util.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.services.ConnectService;
import name.marinchenko.lorryvision.services.NetScanService;
import name.marinchenko.lorryvision.util.net.WifiAgent;
import name.marinchenko.lorryvision.util.threading.ToastThread;

import static name.marinchenko.lorryvision.services.ConnectService.ACTION_CANCEL;
import static name.marinchenko.lorryvision.services.ConnectService.ACTION_DISCONNECTED;


/**
 * Connect dialog for connection cancelling.
 */

public class ConnectDialog
        extends DialogFragment
        implements DialogInterface.OnClickListener {

    public static final String KEY_ID = "key_id";
    public static final String CONNECT_TAG = "connect_tag";


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View form = getActivity()
                .getLayoutInflater()
                .inflate(R.layout.connect_dialog, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(form);
        builder.setTitle(
                getString(R.string.connect_dialog_title) + " " + getArguments().getString(KEY_ID)
        );
        builder.setNegativeButton(android.R.string.cancel, this);

        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        WifiAgent.notifyDisconnected(getActivity());
    }

    @Override
    public void onDismiss(DialogInterface unused) { super.onDismiss(unused); }

    @Override
    public void onCancel(DialogInterface unused) { super.onCancel(unused); }
}
