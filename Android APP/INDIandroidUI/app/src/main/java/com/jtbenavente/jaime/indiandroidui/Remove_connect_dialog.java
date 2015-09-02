package com.jtbenavente.jaime.indiandroidui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

/**
 * Created by Jaime on 27/7/15.
 */
public class Remove_connect_dialog extends DialogFragment{

    private CharSequence[] items;
    private int[] ids;
    private Remove_connec_dialogListener listener;

    static Remove_connect_dialog newInstance(CharSequence[] items){
        Remove_connect_dialog fragment = new Remove_connect_dialog();
        Bundle args = new Bundle();
        args.putCharSequenceArray("items",items);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        items=getArguments().getCharSequenceArray("items");
        return createSimpleDialog();
    }

    /**
     * Crea un diálogo de alerta sencillo
     * @return Nuevo diálogo
     */
    public AlertDialog createSimpleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final ArrayList<Integer> itemsSeleccionados = new ArrayList<Integer>();

        builder.setTitle(getResources().getText(R.string.disconnect))
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            // Guardar indice seleccionado
                            itemsSeleccionados.add(which);
                        } else if (itemsSeleccionados.contains(which)) {
                            // Remover indice sin selección
                            itemsSeleccionados.remove(Integer.valueOf(which));
                        }
                    }
                })
                .setPositiveButton(getResources().getText(R.string.ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ArrayList<Integer> selected=new ArrayList<>();
                                listener.onDisconnectButtonClick(itemsSeleccionados);
                            }
                        })
                .setNegativeButton(getResources().getText(R.string.cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (Remove_connec_dialogListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(
                    activity.toString() +
                            " no implementó Disconnect_dialogListener");

        }
    }

    public interface Remove_connec_dialogListener {
        void onDisconnectButtonClick(ArrayList<Integer> items);
    }
}
