package com.example.jaime.indiandroidui;

import android.content.res.Configuration;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Created by Jaime on 29/8/15.
 */
public class Settings {
    private String folder_path;
    private boolean android_notifications;
    private boolean dialog_notification;
    private boolean sound_notifications;
    private boolean vibrate_notificaciones;
    private Properties settings_file;

    private static Settings instance=null;

    private Settings(){
        String default_folder= Environment.getExternalStorageDirectory().getAbsolutePath()+"/RemoteObservatory";
        this.settings_file = new Properties();
        folder_path=default_folder;
        android_notifications=dialog_notification=sound_notifications=vibrate_notificaciones=false;

        File f=new File(folder_path+"/settings.txt");
        if(!f.exists()){
            settings_file.setProperty("folder_path",default_folder);
            settings_file.setProperty("android_notifications",String.valueOf(android_notifications));
            settings_file.setProperty("dialog_notifications",String.valueOf(dialog_notification));
            settings_file.setProperty("sound_notifications",String.valueOf(sound_notifications));
            settings_file.setProperty("vibrate_notifications",String.valueOf(vibrate_notificaciones));
            try {
                OutputStream out= new FileOutputStream(f);
                settings_file.store(out,"Settings File");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            try {
                InputStream in = new FileInputStream(f);
                settings_file.load(in);
                folder_path=settings_file.getProperty("folder_path");
                android_notifications=Boolean.parseBoolean(settings_file.getProperty("android_notifications"));
                dialog_notification=Boolean.parseBoolean(settings_file.getProperty("dialog_notifications"));
                sound_notifications=Boolean.parseBoolean(settings_file.getProperty("sound_notifications"));
                vibrate_notificaciones=Boolean.parseBoolean(settings_file.getProperty("vibrate_notifications"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static Settings getInstance() {
        if(instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public String getFolderPath(){
        return folder_path;
    }

    public boolean getAndroidNotificacions(){
        return android_notifications;
    }

    public boolean getDialogNotifications(){
        return dialog_notification;
    }

    public boolean getSoundNotifications(){
        return sound_notifications;
    }

    public boolean getVibrateNotifications(){
        return vibrate_notificaciones;
    }

    public void setFolderPath(String folder_path){
        this.folder_path=folder_path;
    }

    public void setAndroidNotifications(boolean android_notifications){
        this.android_notifications=android_notifications;
    }

    public void setDialogNotifications(boolean dialog_notification){
        this.dialog_notification=dialog_notification;
    }

    public void setSoundNotifications(boolean sound_notifications){
        this.sound_notifications=sound_notifications;
    }

    public void setVibrateNotifications(boolean vibrate_notificaciones){
        this.vibrate_notificaciones=vibrate_notificaciones;
    }

    public void saveSettings(){
        File f=new File(folder_path+"/settings.txt");
        settings_file.setProperty("folder_path",folder_path);
        settings_file.setProperty("android_notifications",String.valueOf(android_notifications));
        settings_file.setProperty("dialog_notifications",String.valueOf(dialog_notification));
        settings_file.setProperty("sound_notifications",String.valueOf(sound_notifications));
        settings_file.setProperty("vibrate_notifications",String.valueOf(vibrate_notificaciones));
        try {
            OutputStream out = new FileOutputStream( f );
            settings_file.store(out,"Settings File");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
