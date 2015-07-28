package com.example.jaime.indiandroidui;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import laazotea.indi.client.INDIProperty;


public class MainActivity extends AppCompatActivity implements Connec_dialog.Connec_dialogListener, Disconnec_dialog.Disconnec_dialogListener {

    private ArrayList<IndiClient> clients;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ListView list;
    private ArrayList<ArrayList<ArrayAdapter>> adapters;
    private ArrayList<IndiConnect> threads;
    private int index_device;
    private int index_conect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbar();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation);
        setupDrawerContent(navigationView);

        clients = new ArrayList<IndiClient>();
        threads = new ArrayList<IndiConnect>();

        //Instancia del ListView
        list = (ListView)findViewById(R.id.list);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToListView(list);
        adapters = new ArrayList<ArrayList<ArrayAdapter>>();

        index_device=0;
        index_conect=0;

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
            case R.id.action_disconnect:
                CharSequence[] items = new CharSequence[clients.size()];
                for(int i=0;i<items.length;i++){
                    items[i]=clients.get(i).getNameConecction();
                }
                Disconnec_dialog dialog=Disconnec_dialog.newInstance(items);
                dialog.show(getSupportFragmentManager(), "Remove connections");
                return true;
            case R.id.action_exit:
                for (IndiConnect t:threads){
                    t.finishThread();
                }
                threads=null;
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDrawerMenu(){
        Menu menu=navigationView.getMenu();
        menu.clear();
        for(int i=0;i<clients.size();i++){
            IndiClient client=clients.get(i);
            SubMenu sub= menu.addSubMenu(client.getNameConecction());
            for(int j=0;j<client.getDevicesNames().size();j++){
                String device=client.getDevicesNames().get(j);
                sub.add(i,i+j,j,device).setCheckable(true);
            }
        }
        menu.add("").setVisible(false);
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onConnectButtonClick(String host, int port) {
        adapters.add(new ArrayList<ArrayAdapter>());
        index_conect=adapters.size()-1;
        index_device=0;
        ArrayList<ViewProperty> l=new ArrayList<ViewProperty>();
        adapters.get(index_conect).add(new PropertyArrayAdapter<ViewProperty>(this, l));
        list.setAdapter(adapters.get(index_conect).get(index_device));
        threads.add(new IndiConnect(index_conect, host, port));
        threads.get(threads.size()-1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        list.setAdapter(adapters.get(menuItem.getGroupId()).get(menuItem.getOrder()));
                        drawerLayout.closeDrawers();
                        return true;
                    }
                }
        );
    }

    @Override
    public void onDisconnectButtonClick(ArrayList<String> itemsSeleccionados) {
        for(int i=0;i<itemsSeleccionados.size();i++){
            String item=itemsSeleccionados.get(i);
            for(int j=0;j<clients.size();j++){
                IndiClient client=clients.get(j);
                if(client.getNameConecction().equals(item)){
                    threads.get(j).finishThread();
                    threads.remove(j);
                }
            }
        }
    }

    class IndiConnect extends AsyncTask<Void, IndiClient, Void> {

        private int client_index;
        private int port;
        private String host;
        private boolean fin;

        public IndiConnect(int client_index, String host, int port){
            this.client_index=client_index;
            this.host=host;
            this.port=port;
            fin=false;
        }

        @Override protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "Connecting...", Toast.LENGTH_SHORT).show();
        }

        @Override protected Void doInBackground(Void... par) {
            clients.add(new IndiClient(host, port));
            while(!fin){
                SystemClock.sleep(1000);
                publishProgress(clients.get(client_index));
            }
            return null;
        }

        @Override protected void onProgressUpdate(IndiClient... prog) {
            IndiClient c=prog[0];
            int size=c.getDevicesNames().size();
            if(size!=adapters.get(client_index).size()){
                adapters.get(client_index).clear();
                for(int i=0;i<size;i++){
                    ArrayList<ViewProperty> l=new ArrayList();
                    ArrayAdapter adapter = new PropertyArrayAdapter<ViewProperty>(MainActivity.this, l);
                    adapters.get(client_index).add(adapter);
                }
                list.setAdapter(adapters.get(index_conect).get(0));
            }
            if(size>0) {
                for(int index=0;index<size;index++){
                    ArrayList<INDIProperty> properties = c.getProperties(c.getDevicesNames().get(index));
                    ArrayAdapter adapter = adapters.get(client_index).get(index);
                    if (adapter.getCount() != properties.size() || c.has_change()) {
                        adapter.clear();
                        for (int i = 0; i < properties.size(); i++) {
                            INDIProperty p=properties.get(i);
                            int light=0;
                            int perm=0;
                            int visibility=0;

                            //State
                            if(p.getState().name().equals("IDLE")){
                                light=R.drawable.grey_light_48;
                            }else if(p.getState().name().equals("OK")){
                                light=R.drawable.green_light_48;
                            }else if(p.getState().name().equals("BUSY")){
                                light=R.drawable.yellow_light_48;
                            }else{
                                light=R.drawable.red_light_48;
                            }

                            //Permission
                            if(p.getPermission().name().equals("RO")){
                                perm=R.drawable.read;
                            }else if(p.getPermission().name().equals("WO")){
                                perm=R.drawable.write;
                            }else{
                                perm=R.drawable.rw;
                            }

                            //Visibility

                            adapter.add(new ViewProperty(p.getLabel(),light,perm,R.drawable.ic_visibility_black_24dp));
                        }
                    }
                }
            }
        }

        @Override protected void onPostExecute(Void result) {
            clients.remove(client_index);
            adapters.remove(client_index);
        }

        public void finishThread(){
            fin=true;
        }

    }
}
