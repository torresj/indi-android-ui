package com.example.jaime.indiservertest;

import java.io.IOException;
import java.util.Date;

import laazotea.indi.client.INDIDevice;
import laazotea.indi.client.INDIDeviceListener;
import laazotea.indi.client.INDIProperty;
import laazotea.indi.client.INDIPropertyListener;
import laazotea.indi.client.INDIServerConnection;
import laazotea.indi.client.INDIServerConnectionListener;

/**
 * Created by jaime on 20/01/2015.
 */
public class IndiClient implements INDIServerConnectionListener, INDIDeviceListener, INDIPropertyListener, Runnable {

    private INDIServerConnection connection;
    private String host;
    private int port;

    public IndiClient(String host, int port){
        this.host=host;
        this.port=port;
    }

    @Override
    public void newProperty(INDIDevice device, INDIProperty property) {

    }

    @Override
    public void removeProperty(INDIDevice device, INDIProperty property) {

    }

    @Override
    public void messageChanged(INDIDevice device) {

    }

    @Override
    public void propertyChanged(INDIProperty property) {

    }

    @Override
    public void newDevice(INDIServerConnection connection, INDIDevice device) {

    }

    @Override
    public void removeDevice(INDIServerConnection connection, INDIDevice device) {

    }

    @Override
    public void connectionLost(INDIServerConnection connection) {

    }

    @Override
    public void newMessage(INDIServerConnection connection, Date timestamp, String message) {

    }

    @Override
    public void run() {
        connection = new INDIServerConnection(host, port);

        connection.addINDIServerConnectionListener(this);  // We listen to all server events

        try {
            connection.connect();
            connection.askForDevices();  // Ask for all the devices.
        } catch (IOException e) {
            System.out.println("Problem with the connection: " + host + ":" + port);
            e.printStackTrace();
        }
    }
}
