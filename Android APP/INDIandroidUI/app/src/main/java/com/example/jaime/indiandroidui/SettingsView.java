package com.example.jaime.indiandroidui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by Jaime on 29/8/15.
 */
public class SettingsView extends Fragment implements View.OnClickListener,TextWatcher{

    private Settings settings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        settings=Settings.getInstance();

        View view = inflater.inflate(R.layout.settings_view, container, false);
        Switch s_android=(Switch) view.findViewById(R.id.android_notifications);
        Switch s_sound=(Switch) view.findViewById(R.id.sound_notifications);
        Switch s_vibrate=(Switch) view.findViewById(R.id.vibrate_notifications);
        Switch s_dialog=(Switch) view.findViewById(R.id.dialog_notifications);
        EditText folder=(EditText) view.findViewById(R.id.folder);

        s_android.setTag("android");
        s_sound.setTag("sound");
        s_vibrate.setTag("vibrate");
        s_dialog.setTag("dialog");
        folder.setTag("folder");

        s_android.setChecked(settings.getAndroidNotificacions());
        s_sound.setChecked(settings.getSoundNotifications());
        s_vibrate.setChecked(settings.getVibrateNotifications());
        s_dialog.setChecked(settings.getDialogNotifications());
        folder.setText(settings.getFolderPath());

        s_android.setOnClickListener(this);
        s_sound.setOnClickListener(this);
        s_vibrate.setOnClickListener(this);
        s_dialog.setOnClickListener(this);
        folder.addTextChangedListener(this);

        return view;
    }

    @Override
    public void onPause(){
        super.onPause();
        settings.saveSettings();
    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();

        if (tag.equals("android")) {
            Switch s = (Switch) v;
            settings.setAndroidNotifications(s.isChecked());
        }else if (tag.equals("sound")) {
            Switch s = (Switch) v;
            settings.setSoundNotifications(s.isChecked());
        }else if (tag.equals("vibrate")) {
            Switch s = (Switch) v;
            settings.setVibrateNotifications(s.isChecked());
        }else{
            Switch s = (Switch) v;
            settings.setDialogNotifications(s.isChecked());}

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        settings.setFolderPath(s.toString());
    }
}
