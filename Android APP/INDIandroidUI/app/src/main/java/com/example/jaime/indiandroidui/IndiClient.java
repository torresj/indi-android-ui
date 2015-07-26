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
    private Map<String, ArrayList<INDIProperty>> devices;
    private ArrayList<String> name_devices;
    private boolean change;

    public IndiClient(String host, int port){
        log="";
        connection = new INDIServerConnection(host, port);

        devices=new HashMap<String, ArrayList<INDIProperty>>();
        name_devices=new ArrayList();

        change=false;

        connection.addINDIServerConnectionListener(this);  // We listen to all server events

        try {
            connection.connect();
            connection.askForDevices();  // Ask for all the devices.
        } catch (IOException e) {
            log+="Problem with connection: " + host + ":" + port+"\n";
            e.printStackTrace();
        }

    }

    @Override
    public void newDevice(INDIServerConnection connection, INDIDevice device) {
        // We just simply listen to this Device
        log+="New device: " + device.getName()+"\n";
        name_devices.add(device.getName());
        devices.put(device.getName(), new ArrayList<INDIProperty>());
        try {
            device.BLOBsEnable(Constants.BLOBEnables.ALSO); // Enable receiving BLOBs from this Device
        } catch (IOException e) {
        }
        device.addINDIDeviceListener(this);
    }

    @Override
    public void removeDevice(INDIServerConnection connection, INDIDevice device) {
        // We just remove ourselves as a listener of the removed device
        log+="Device Removed: " + device.getName()+"\n";
        name_devices.remove(device.getName());
        devices.remove(device.getName());
        device.removeINDIDeviceListener(this);
    }

    @Override
    public void connectionLost(INDIServerConnection connection) {
        log+="Connection lost. Bye"+"\n";

    }

    @Override
    public void newMessage(INDIServerConnection connection, Date timestamp, String message) {
        log+="New Server Message: " + timestamp + " - " + message+"\n";
    }

    @Override
    public void newProperty(INDIDevice device, INDIProperty property) {
        // We just simply listen to this Property
        log+="New Property (" + property.getName() + ") added to device " + device.getName()+"\n";
        devices.get(device.getName()).add(property);
        property.addINDIPropertyListener(this);
    }

    @Override
    public void removeProperty(INDIDevice device, INDIProperty property) {
        // We just remove ourselves as a listener of the removed property
        log+="Property (" + property.getName() + ") removed from device " + device.getName()+"\n";
        devices.get(device.getName()).remove(property);
        property.removeINDIPropertyListener(this);
    }

    @Override
    public void messageChanged(INDIDevice device) {
        log+="New Device Message: " + device.getName() + " - " + device.getTimestamp() + " - " + device.getLastMessage()+"\n";
    }

    @Override
    public void propertyChanged(INDIProperty property) {
        log+="Property Changed: " + property.getNameStateAndValuesAsString()+"\n";
        boolean fin=false;
        for(int i=0;i<devices.get(property.getDevice().getName()).size() && !fin;i++){
            INDIProperty p=devices.get(property.getDevice().getName()).get(i);
            if(property.getName().equals(p.getName())){
                devices.get(property.getDevice().getName()).set(i,property);
                fin=true;
            }
        }
    }

    public String getLog(){
        return log;
    }

    public ArrayList<INDIProperty> getProperties(String device){
        return devices.get(device);
    }

    public ArrayList<String> getDevicesNames(){
        return name_devices;
    }

    public boolean has_change(){
        if(change){
            change=false;
            return true;
        }else{
            return false;
        }
    }

    public String getNameConecction(){
        return connection.getHost();
    }
}
