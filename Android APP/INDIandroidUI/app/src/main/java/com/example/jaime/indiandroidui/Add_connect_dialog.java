package com.example.jaime.indiandroidui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

/**
 * Fragmento con diálogo básico
 */
public class Add_connect_dialog extends DialogFragment {

    private Add_connec_dialogListener listener;

    public Add_connect_dialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createDialog();
    }

    /**
     * Crea un diálogo de alerta sencillo
     * @return Nuevo diálogo
     */
    public AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.new_connection_dialog, null);

        final Button connect = (Button) v.findViewById(R.id.connec_button);
        final EditText host_edit=(EditText) v.findViewById(R.id.host);
        final EditText port_edit=(EditText) v.findViewById(R.id.port);
        final EditText name_edit=(EditText) v.findViewById(R.id.name);
        final Switch autoconnect_switch=(Switch) v.findViewById(R.id.autconnect);
        final Switch blobs_enable_switch=(Switch) v.findViewById(R.id.blob_recive);

        connect.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String host=host_edit.getText().toString();
                        String name=name_edit.getText().toString();
                        int port=Integer.parseInt(port_edit.getText().toString());
                        boolean autoconnect = autoconnect_switch.isChecked();
                        boolean blobs_enable = blobs_enable_switch.isChecked();
                        listener.onConnectButtonClick(name,host,port,autoconnect,blobs_enable);
                        dismiss();
                    }
                }
        );

        builder.setView(v);


        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (Add_connec_dialogListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(
                    activity.toString() +
                            " no implementó Connect_dialogListener");

        }
    }

    public interface Add_connec_dialogListener {
        void onConnectButtonClick(String name,String host,int port,boolean autoconnect, boolean blobs_enable);
    }
}
