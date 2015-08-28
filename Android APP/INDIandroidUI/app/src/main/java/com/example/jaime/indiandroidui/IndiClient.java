package com.example.jaime.indiandroidui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import laazotea.indi.client.INDIDevice;
import laazotea.indi.client.INDIDeviceListener;
import laazotea.indi.client.INDIProperty;
import laazotea.indi.client.INDIPropertyListener;
import laazotea.indi.client.INDIServerConnection;
import laazotea.indi.client.INDIServerConnectionListener;
import laazotea.indi.Constants;

/**
 * Created by jaime on 20/01/2015.
 */
public class IndiClient implements INDIServerConnectionListener, INDIDeviceListener, INDIPropertyListener{

    private INDIServerConnection connection;
    private String log;
    HashMap<String,Device> devices;
    private boolean change;
    private boolean blobs_enable;

    public IndiClient(String host, int port,boolean blobs_enable, INDIServerConnectionListener l) throws IOException {
        log="";
        this.blobs_enable=blobs_enable;
        connection = new INDIServerConnection(host, port);

        devices=new HashMap<String,Device>();

        change=false;

        connection.addINDIServerConnectionListener(l);
        connection.addINDIServerConnectionListener(this);  // We listen to all server events


        try {
            connection.connect();
            connection.askForDevices();  // Ask for all the devices.
        } catch (IOException e) {
            log+="Problem with connection: " + host + ":" + port+"\n";
            throw e;
        }

    }

    @Override
    public void newDevice(INDIServerConnection connection, INDIDevice device) {
        // We just simply listen to this Device
        log+="New device: " + device.getName()+"\n";
        devices.put(device.getName(),new Device(device.getName()));
        try {
            if(blobs_enable) {
                device.BLOBsEnable(Constants.BLOBEnables.ALSO); // Enable receiving BLOBs from this Device
            }else{
                device.BLOBsEnable(Constants.BLOBEnables.NEVER);
            }
        } catch (IOException e) {
        }
        change=true;
        device.addINDIDeviceListener(this);
    }

    @Override
    public void removeDevice(INDIServerConnection connection, INDIDevice device) {
        // We just remove ourselves as a listener of the removed device
        log+="Device Removed: " + device.getName()+"\n";

        devices.remove(device.getName());

        change=true;
        
        device.removeINDIDeviceListener(this);
    }

    @Override
    public void connectionLost(INDIServerConnection connection) {
        log+="Connection lost. Bye"+"\n";
        devices.clear();
        change=true;
    }

    @Override
    public void newMessage(INDIServerConnection connection, Date timestamp, String message) {
        log+="New Server Message: " + timestamp + " - " + message+"\n";
    }

    @Override
    public void newProperty(INDIDevice device, INDIProperty property) {
        // We just simply listen to this Property
        log+="New Property (" + property.getName() + ") added to device " + device.getName()+"\n";
        devices.get(device.getName()).addProperty(property);
        change=true;
        property.addINDIPropertyListener(this);
    }

    @Override
    public void removeProperty(INDIDevice device, INDIProperty property) {
        // We just remove ourselves as a listener of the removed property
        log+="Property (" + property.getName() + ") removed from device " + device.getName()+"\n";
        devices.get(device.getName()).removeProperty(property);
        change=true;
        property.removeINDIPropertyListener(this);
    }

    @Override
    public void messageChanged(INDIDevice device) {
        log+="New Device Message: " + device.getName() + " - " + device.getTimestamp() + " - " + device.getLastMessage()+"\n";
    }

    @Override
    public void propertyChanged(INDIProperty property) {
        log+="Property Changed: " + property.getNameStateAndValuesAsString()+"\n";
        System.out.println("Property Changed: " + property.getNameStateAndValuesAsString()+"\n");
        String device_name=property.getDevice().getName();
        devices.get(device_name).updateProperty(property);
        change=true;
    }

    public String getLog(){
        return log;
    }

    public HashMap<String,Device> getDevices(){
        return devices;
    }

    public Device getDevice(String device_name){
        return devices.get(device_name);
    }

    public ArrayList<String> getDevicesNames(){
        ArrayList<String> names=new ArrayList<>();

        for (String name : devices.keySet()) {
            names.add(name);
        }
        return names;
    }

    public boolean has_change(){
        return change;
    }

    public void changeRead(){
        change=false;
    }

    public String getNameConecction(){
        return connection.getHost();
    }
}
