package com.jtbenavente.jaime.indiandroidui;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

/**
 * Created by jaimetorres on 11/8/16.
 */
public class Demo_dialog extends DialogFragment {

    private Demo_dialogListener listener;
    private boolean show_demo_dialog;
    private CheckBox checkbox;
    private SharedPreferences sharedPref;

    public Demo_dialog(){}

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createDialog();
    }

    private AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        show_demo_dialog = sharedPref.getBoolean("show_demo_dialog",true);

        View view = inflater.inflate(R.layout.demo_dialog,null);
        checkbox = (CheckBox) view.findViewById(R.id.checkBox);
        checkbox.setChecked(show_demo_dialog);

        builder .setTitle(R.string.demo_server)
                .setMessage(R.string.demo_text)
                .setView(view)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        show_demo_dialog = checkbox.isChecked();

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("show_demo_dialog",show_demo_dialog);
                        editor.commit();

                        listener.onOkButtonClick();
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onCancelButtonClick();
                        dismiss();
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (Demo_dialogListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(
                    activity.toString() +
                            " no implementó Demo_dialogListener");

        }
    }

    public interface Demo_dialogListener {
        void onOkButtonClick();
        void onCancelButtonClick();
    }
}
