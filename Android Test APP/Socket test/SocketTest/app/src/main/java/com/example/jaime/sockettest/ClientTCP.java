package com.example.jaime.sockettest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;

import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class ClientTCP extends Activity {

    private Socket socket;
    private Thread client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_tcp);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.client_tc, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void clickConn(View v) {
        if(((Button)v).getText().toString().equals("Connect")) {
            ((Button) v).setText("Disconnect");
            client=new Thread(new ClientThread());
            client.start();
        }else{
            ((Button) v).setText("Connect");
            client.interrupt();
            client=null;
            try {
                socket.close();
                socket=null;
            }catch(IOException e){
                System.err.println(e.getLocalizedMessage());
            }
        }
    }

    public void clickSend(View v) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            EditText text   = (EditText)findViewById(R.id.sendText);
            String str=text.getText().toString();
            out.println(str);
            text.setText("");

        }catch(Exception e){
            System.err.println(e.getLocalizedMessage());
        }
    }

    class ClientThread implements Runnable {

        @Override

        public void run() {

            try {
                EditText SERVER_IP   = (EditText)findViewById(R.id.ip);
                EditText SERVER_PORT   = (EditText)findViewById(R.id.port);

                InetAddress serverAddr = InetAddress.getByName(SERVER_IP.getText().toString());

                socket = new Socket(serverAddr, Integer.parseInt(SERVER_PORT.getText().toString()));


            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

}
