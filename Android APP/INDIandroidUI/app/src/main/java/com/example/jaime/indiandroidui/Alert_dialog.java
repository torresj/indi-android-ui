package com.example.jaime.indiandroidui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Jaime on 21/8/15.
 */
public class Alert_dialog extends DialogFragment {

    private String text;

    static Alert_dialog newInstance(String text_resource){
        Alert_dialog fragment = new Alert_dialog();
        Bundle args = new Bundle();
        args.putString("text",text_resource);
        fragment.setArguments(args);
        return fragment;
    }

    public Alert_dialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        text=getArguments().getString("text");

        return createAlertDialog();
    }

    /**
     * Crea un diálogo de alerta sencillo
     * @return Nuevo diálogo
     */
    public AlertDialog createAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.alert)
                .setMessage(text)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dismiss();
                            }
                        });

        return builder.create();
    }
}
