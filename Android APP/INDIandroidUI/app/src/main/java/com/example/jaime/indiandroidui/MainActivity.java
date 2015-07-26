package com.example.jaime.indiandroidui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;

import java.util.ArrayList;

import laazotea.indi.client.INDIProperty;


public class MainActivity extends AppCompatActivity implements Connec_dialog.Connec_dialogListener {

    private ArrayList<IndiClient> clients;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbar();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation);

        clients = new ArrayList<IndiClient>();

    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Poner ícono del drawer toggle
            ab.setHomeAsUpIndicator(R.drawable.ic_list_black_24dp);
            ab.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setDrawerMenu();
                return true;
            case R.id.action_connect:
                new Connec_dialog().show(getSupportFragmentManager(), "New Connection");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDrawerMenu(){
        Menu menu=navigationView.getMenu();
        for(IndiClient client:clients){
            SubMenu sub= menu.addSubMenu(client.getNameConecction());
            for(String device:client.getDevicesNames()){
                sub.add(device);
            }
        }
        menu.add("").setVisible(false);
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onConnectButtonClick(String host, int port) {
        new IndiConnect(clients.size(),host,port).execute();
    }

    class IndiConnect extends AsyncTask<Void, IndiClient, Void> {

        private int client_index;
        private int port;
        private String host;

        public IndiConnect(int client_index, String host, int port){
            this.client_index=client_index;
            this.host=host;
            this.port=port;
        }

        @Override protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "Connecting...", Toast.LENGTH_SHORT).show();
        }

        @Override protected Void doInBackground(Void... par) {
            clients.add(new IndiClient(host,port));
            while(true){
                SystemClock.sleep(1000);
                publishProgress(clients.get(client_index));
            }
        }

        @Override protected void onProgressUpdate(IndiClient... prog) {
            IndiClient c=prog[0];
            if(c.getDevicesNames().size()>0) {

            }
        }

        @Override protected void onPostExecute(Void result) {


        }

    }
}
