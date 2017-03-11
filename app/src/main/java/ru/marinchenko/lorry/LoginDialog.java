package ru.marinchenko.lorry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Диалог ввода пароля для аутентификации в выбранной сети.
 */

public class LoginDialog extends DialogFragment implements
        DialogInterface.OnClickListener {

    private View form = null;
    private MainActivity mainActivity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        form = getActivity().getLayoutInflater().inflate(R.layout.password_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(form);
        builder.setPositiveButton(android.R.string.ok, this);
        builder.setNegativeButton(android.R.string.cancel, null);

        return builder.create();
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        mainActivity = (MainActivity) getActivity();
        EditText passwordBox = (EditText) form.findViewById(R.id.password);
        String password = passwordBox.getText().toString();

        if(password.equals("pick")){
            /*for(int i = 1; i < 11; i++){
                mainActivity.pick(i);
            }*/
            mainActivity.authentificate("polytech407");
        }

        Context ctx = mainActivity.getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(ctx, passwordBox.getText(), duration);
        toast.show();

        //mainActivity.authentificate(password);
    }

    @Override
    public void onDismiss(DialogInterface unused) {
        super.onDismiss(unused);
    }

    @Override
    public void onCancel(DialogInterface unused) {
        super.onCancel(unused);
    }
}
