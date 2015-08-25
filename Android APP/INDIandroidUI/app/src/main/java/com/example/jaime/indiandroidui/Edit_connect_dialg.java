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

/**
 * Created by Jaime on 24/8/15.
 */
public class Edit_connect_dialg extends DialogFragment {

    private String name;
    private String host;
    private int port;
    private int position;
    private Edit_connect_dialogListener listener;

    static Edit_connect_dialg newInstance(Connection conn, int position){
        Edit_connect_dialg fragment = new Edit_connect_dialg();
        Bundle args = new Bundle();
        args.putString("name",conn.getName());
        args.putString("host",conn.getHost());
        args.putInt("port",conn.getPort());
        args.putInt("position",position);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        name=getArguments().getString("name");
        host=getArguments().getString("host");
        port=getArguments().getInt("port");
        position=getArguments().getInt("position");
        return createEditDialog();
    }

    /**
     * Crea un diálogo de alerta sencillo
     * @return Nuevo diálogo
     */
    public AlertDialog createEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.edit_connection_dialog, null);

        final Button edit = (Button) v.findViewById(R.id.edit_button);
        final EditText host_edit=(EditText) v.findViewById(R.id.host);
        final EditText port_edit=(EditText) v.findViewById(R.id.port);
        final EditText name_edit=(EditText) v.findViewById(R.id.name);

        host_edit.setText(host);
        name_edit.setText(name);
        port_edit.setText(Integer.toString(port));

        edit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String host=host_edit.getText().toString();
                        String name=name_edit.getText().toString();
                        int port=Integer.parseInt(port_edit.getText().toString());
                        listener.onEditButtonClick(name,host,port,position);
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
            listener = (Edit_connect_dialogListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(
                    activity.toString() +
                            " no implementó Disconnect_dialogListener");

        }
    }

    public interface Edit_connect_dialogListener {
        void onEditButtonClick(String name,String host, int port,int position);
    }
}
