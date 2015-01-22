package com.example.jaime.indiservertest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MyActivity extends Activity {

    private IndiClient client;
    private Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
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
            EditText SERVER_IP   = (EditText)findViewById(R.id.server);
            EditText SERVER_PORT   = (EditText)findViewById(R.id.port);
            String server=SERVER_IP.getText().toString();
            int port=0;
            try {
                port = Integer.parseInt(SERVER_PORT.getText().toString());
            } catch (NumberFormatException e) {

            }
            client=new IndiClient(server,port);
            t=new Thread(client);
            t.start();

            ((Button) v).setText("Disconnect");
        }else{
            ((Button) v).setText("Connect");

        }
    }
}
