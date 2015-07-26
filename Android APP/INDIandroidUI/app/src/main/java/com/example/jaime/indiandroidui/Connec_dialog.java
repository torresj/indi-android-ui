package com.example.jaime.indiandroidui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Fragmento con diálogo básico
 */
public class Connec_dialog extends DialogFragment {

    private Connec_dialogListener listener;

    public Connec_dialog() {
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

        connect.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String host=host_edit.getText().toString();
                        int port=Integer.parseInt(port_edit.getText().toString());
                        listener.onConnectButtonClick(host,port);
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
            listener = (Connec_dialogListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(
                    activity.toString() +
                            " no implementó Connect_dialogListener");

        }
    }

    public interface Connec_dialogListener {
        void onConnectButtonClick(String host,int port);
    }
}
