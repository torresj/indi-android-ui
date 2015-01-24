package com.example.jaime.indiservertest;


import java.io.IOException;
import java.util.Date;

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

    public IndiClient(String host, int port){
        log="";
        connection = new INDIServerConnection(host, port);

        connection.addINDIServerConnectionListener(this);  // We listen to all server events

        try {
            connection.connect();
            connection.askForDevices();  // Ask for all the devices.
        } catch (IOException e) {
            log+="Problem with the connection: " + host + ":" + port+"\n";
            e.printStackTrace();
        }
    }

    @Override
    public void newDevice(INDIServerConnection connection, INDIDevice device) {
        // We just simply listen to this Device
        log+="New device: " + device.getName()+"\n";
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
        property.addINDIPropertyListener(this);
    }

    @Override
    public void removeProperty(INDIDevice device, INDIProperty property) {
        // We just remove ourselves as a listener of the removed property
        log+="Property (" + property.getName() + ") removed from device " + device.getName()+"\n";
        property.removeINDIPropertyListener(this);
    }

    @Override
    public void messageChanged(INDIDevice device) {
        log+="New Device Message: " + device.getName() + " - " + device.getTimestamp() + " - " + device.getLastMessage()+"\n";
    }

    @Override
    public void propertyChanged(INDIProperty property) {
        log+="Property Changed: " + property.getNameStateAndValuesAsString()+"\n";
    }

    public String getLog(){
        return log;
    }
}
