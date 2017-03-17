package ru.marinchenko.lorry.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import ru.marinchenko.lorry.MainActivity;
import ru.marinchenko.lorry.R;


/**
 * Диалог ввода пароля для аутентификации в выбранной сети.
 */
public class LoginDialog extends DialogFragment implements
        DialogInterface.OnClickListener {

    private View form = null;

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
        MainActivity mainActivity = (MainActivity) getActivity();
        EditText passwordBox = (EditText) form.findViewById(R.id.edittext_password);

        mainActivity.authenticate(passwordBox.getText().toString());
    }

    @Override
    public void onDismiss(DialogInterface unused) { super.onDismiss(unused); }

    @Override
    public void onCancel(DialogInterface unused) { super.onCancel(unused); }
}
