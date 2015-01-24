package com.example.jaime.indiservertest;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MyActivity extends ActionBarActivity {

    private IndiClient client;
    private TextView logView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        logView=(TextView)findViewById(R.id.log);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clickConn(View v) {
        if(((Button)v).getText().toString().equals("Connect")) {
            new UpdateLog().execute();
            ((Button) v).setText("Disconnect");
        }else{
            ((Button) v).setText("Connect");

        }
    }

    class UpdateLog extends AsyncTask<Void, String, Void> {

        private int port;
        private String host;


        @Override protected void onPreExecute() {
            EditText SERVER_IP   = (EditText)findViewById(R.id.server);
            EditText SERVER_PORT   = (EditText)findViewById(R.id.port);
            host=SERVER_IP.getText().toString();
            port=0;
            try {
                port = Integer.parseInt(SERVER_PORT.getText().toString());
            } catch (NumberFormatException e) {

            }
            logView.setText("Connecting...");
        }

        @Override protected Void doInBackground(Void... par) {
            client=new IndiClient(host,port);
            while(true){
                publishProgress(client.getLog());
                SystemClock.sleep(1000);
            }
        }

        @Override protected void onProgressUpdate(String... prog) {
            logView.setText(prog[0]);
        }

        @Override protected void onPostExecute(Void result) {


        }

    }
}
