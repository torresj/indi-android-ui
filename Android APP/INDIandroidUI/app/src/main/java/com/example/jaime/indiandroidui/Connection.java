package com.example.jaime.indiandroidui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Date;

import laazotea.indi.client.INDIDevice;
import laazotea.indi.client.INDIProperty;
import laazotea.indi.client.INDIServerConnection;
import laazotea.indi.client.INDIServerConnectionListener;

/**
 * Created by Jaime on 17/8/15.
 */
public class Connection implements INDIServerConnectionListener {

    private IndiClient client;
    private String host;
    private String name;
    private boolean autoconnect;
    private boolean blobs_enable;
    private int port;
    private ArrayList<PropertyArrayAdapter> adapters;
    private Context context;
    private IndiConnect thread;
    private boolean connected;
    private boolean error;
    private String log;
    private Settings settings;

    public Connection(String name,String host, int port,boolean autoconnect,boolean blobs_enable,Context context){
        this.name=name;
        this.host=host;
        this.port=port;
        this.autoconnect=autoconnect;
        this.blobs_enable=blobs_enable;
        this.context=context;
        thread = new IndiConnect();
        client=null;
        adapters = new ArrayList<>();
        connected = false;
        error = false;
        settings=Settings.getInstance();
    }

    public ArrayList<PropertyArrayAdapter> getAdapters() {
        return adapters;
    }

    public String getName(){
        return name;
    }

    public String getHost(){
        return host;
    }

    public boolean getAutoconnect(){
        return autoconnect;
    }

    public boolean getBlobsEnable(){
        return blobs_enable;
    }

    public int getPort(){
        return port;
    }
    public boolean hasError(){
        return error;
    }

    public IndiClient getClient(){
        return client;
    }

    public boolean isConnected(){
        return connected;
    }

    public void disconnect(){
        if(thread!=null){
            thread.finishThread();
            thread=null;
        }
        connected=false;
    }

    public void connect(){
        if(thread==null)
            thread=new IndiConnect();
        thread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        connected=true;
    }

    @Override
    public void newDevice(INDIServerConnection connection, INDIDevice device) {
        if(!MainActivity.pause) {
            if(settings.getDialogNotifications()) {
                Alert_dialog alert=Alert_dialog.newInstance(context.getResources().getString(R.string.alert_device_add)+": "+device.getName());
                alert.show(((AppCompatActivity)context).getSupportFragmentManager(), "AlertDialog");
                ((MainActivity) context).set_uichange(true);
            }
        }else {
            if(settings.getAndroidNotificacions()) {
                Intent intent = new Intent(context, MainActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

                Notification notification = new NotificationCompat.Builder(context)
                        .setCategory(Notification.CATEGORY_MESSAGE)
                        .setContentTitle("Aviso")
                        .setContentText(context.getResources().getString(R.string.alert_device_add) + ": " + device.getName())
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.notification))
                        .setContentIntent(pIntent).build();
                NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notification.flags |= Notification.FLAG_AUTO_CANCEL;

                notificationManager.notify(0, notification);
            }
        }

        if(settings.getSoundNotifications()){
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
            r.play();
        }

        if(settings.getVibrateNotifications()){
            Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);
        }
    }

    @Override
    public void removeDevice(INDIServerConnection connection, INDIDevice device) {
        if(!MainActivity.pause) {
            if(settings.getDialogNotifications()) {
                Alert_dialog alert=Alert_dialog.newInstance(context.getResources().getString(R.string.alert_device_remove)+": "+device.getName());
                alert.show(((AppCompatActivity) context).getSupportFragmentManager(), "AlertDialog");
                ((MainActivity) context).set_uichange(true);
            }
        }else {
            if(settings.getAndroidNotificacions()) {
                Intent intent = new Intent(context, MainActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

                Notification notification = new NotificationCompat.Builder(context)
                        .setCategory(Notification.CATEGORY_MESSAGE)
                        .setContentTitle("Aviso")
                        .setContentText(context.getResources().getString(R.string.alert_device_remove) + ": " + device.getName())
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.notification))
                        .setContentIntent(pIntent).build();
                NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notification.flags |= Notification.FLAG_AUTO_CANCEL;

                notificationManager.notify(0, notification);
            }
        }

        if(settings.getSoundNotifications()){
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
            r.play();
        }

        if(settings.getVibrateNotifications()){
            Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);
        }
    }

    @Override
    public void connectionLost(INDIServerConnection connection) {
        if(!MainActivity.pause) {
            if(settings.getDialogNotifications()) {
                Alert_dialog alert = Alert_dialog.newInstance(context.getResources().getString(R.string.alert_connection_lost) + ": " + connection.getHost());
                alert.show(((AppCompatActivity) context).getSupportFragmentManager(), "AlertDialog");
                disconnect();
                ((MainActivity) context).set_uichange(true);
            }
        }else {
            if(settings.getAndroidNotificacions()) {
                Intent intent = new Intent(context, MainActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

                Notification notification = new NotificationCompat.Builder(context)
                        .setCategory(Notification.CATEGORY_MESSAGE)
                        .setContentTitle("Aviso")
                        .setContentText(context.getResources().getString(R.string.alert_connection_lost) + ": " + connection.getHost())
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.notification))
                        .setContentIntent(pIntent).build();
                NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notification.flags |= Notification.FLAG_AUTO_CANCEL;

                notificationManager.notify(0, notification);
            }
        }

        if(settings.getSoundNotifications()){
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
            r.play();
        }

        if(settings.getVibrateNotifications()){
            Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);
        }

    }

    @Override
    public void newMessage(INDIServerConnection connection, Date timestamp, String message) {

    }

    public String getLog(){
        return log;
    }

    class IndiConnect extends AsyncTask<Void, IndiClient, Void> {

        private boolean end;

        public IndiConnect(){
            end=false;
        }

        @Override protected void onPreExecute() {
            error=false;
        }

        @Override protected Void doInBackground(Void... par) {
            try {
                client = new IndiClient(host, port,blobs_enable,Connection.this);
                while(!end){
                    SystemClock.sleep(100);
                    publishProgress(client);
                }
            }catch(Exception e){
                Alert_dialog alert=Alert_dialog.newInstance("Failed to connect to "+host+" (port "+port+")");
                alert.show(((AppCompatActivity)context).getSupportFragmentManager(), "AlertDialog");
                disconnect();
                error=true;
            }
            return null;
        }

        @Override protected void onProgressUpdate(IndiClient... prog) {
            IndiClient c=prog[0];
            log=c.getLog();
            boolean change=c.has_change();
            c.changeRead();
            ArrayList<String> device_names=c.getDevicesNames();
            if(device_names.size()!=adapters.size()){
                adapters.clear();
                for(int i=0;i<device_names.size();i++){
                    SparseArray<Groups_properties> groups=new SparseArray<>();
                    PropertyArrayAdapter adapter = new PropertyArrayAdapter((AppCompatActivity) context,groups);
                    adapters.add(adapter);
                }
            }
            if(device_names.size()>0) {
                if(change){
                    for(int index=0;index<device_names.size();index++){
                        String device_name=device_names.get(index);
                        PropertyArrayAdapter adapter = adapters.get(index);
                        Device device=c.getDevice(device_name);
                        ArrayList<String> groups=device.getGroupsNames();
                        adapter.clear();

                        for(int i=0;i<groups.size();i++){
                            Groups_properties group=new Groups_properties(groups.get(i));
                            ArrayList<INDIProperty> list=device.getGroupProperties(groups.get(i));
                            for(int j=0;j<list.size();j++){
                                group.properties.add(list.get(j));
                            }
                            adapter.add(group);
                        }
                        Groups_properties group=new Groups_properties("");
                        adapter.add(group);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }


        @Override protected void onPostExecute(Void result) {
            client=null;
            adapters.clear();
        }

        public void finishThread(){
            end=true;
        }

    }
}
