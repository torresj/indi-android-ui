package com.jtbenavente.jaime.indiandroidui;

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
import android.widget.Switch;

import java.io.File;

/**
 * Created by Jaime on 24/8/15.
 */
public class Edit_connect_dialg extends DialogFragment {

    private String name;
    private String host;
    private int port;
    private int position;
    private boolean autoconnect;
    private boolean blobs_enable;
    private Edit_connect_dialogListener listener;
    private Settings settings;

    static Edit_connect_dialg newInstance(Connection conn, int position){
        Edit_connect_dialg fragment = new Edit_connect_dialg();
        Bundle args = new Bundle();
        args.putString("name",conn.getName());
        args.putString("host",conn.getHost());
        args.putInt("port", conn.getPort());
        args.putInt("position", position);
        args.putBoolean("autoconnect", conn.getAutoconnect());
        args.putBoolean("blobs_enable",conn.getBlobsEnable());
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
        autoconnect=getArguments().getBoolean("autoconnect");
        blobs_enable=getArguments().getBoolean("blobs_enable");

        return createEditDialog();
    }

    /**
     * Crea un diálogo de alerta sencillo
     * @return Nuevo diálogo
     */
    public AlertDialog createEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        settings=Settings.getInstance();

        View v = inflater.inflate(R.layout.edit_connection_dialog, null);

        final Button edit = (Button) v.findViewById(R.id.edit_button);
        final EditText host_edit=(EditText) v.findViewById(R.id.host);
        final EditText port_edit=(EditText) v.findViewById(R.id.port);
        final EditText name_edit=(EditText) v.findViewById(R.id.name);
        final Switch autoconnect_switch=(Switch) v.findViewById(R.id.autconnect);
        final Switch blobs_enable_switch=(Switch) v.findViewById(R.id.blob_recive);
        final Button r_log = (Button)v.findViewById(R.id.remove_log_button);

        host_edit.setText(host);
        name_edit.setText(name);
        port_edit.setText(Integer.toString(port));
        autoconnect_switch.setChecked(autoconnect);
        blobs_enable_switch.setChecked(blobs_enable);

        edit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String host = host_edit.getText().toString();
                        String name = name_edit.getText().toString();
                        int port = Integer.parseInt(port_edit.getText().toString());
                        boolean autoconnect = autoconnect_switch.isChecked();
                        boolean blobs_enable = blobs_enable_switch.isChecked();
                        listener.onEditButtonClick(name, host, port, autoconnect, blobs_enable, position);
                        dismiss();
                    }
                }
        );

        File log=new File(settings.getFolderPath()+"/log/"+host_edit.getText().toString()+".txt");

        if(log.exists()){
            if(log.length()>0)
                r_log.setEnabled(true);
            else
                r_log.setEnabled(false);
        }else
            r_log.setEnabled(false);

        r_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Edit_connect_dialg.this.getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.alert)
                        .setMessage(R.string.remove_log_text)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                r_log.setEnabled(false);
                                File log=new File(settings.getFolderPath()+"/log/"+host_edit.getText().toString()+".txt");
                                log.delete();
                            }

                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
        });

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
        void onEditButtonClick(String name,String host, int port,boolean autoconnect,boolean blobs_enable,int position);
    }
}
