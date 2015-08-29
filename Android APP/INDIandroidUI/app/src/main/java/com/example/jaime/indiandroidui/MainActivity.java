package com.example.jaime.indiandroidui;


import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements Add_connect_dialog.Add_connec_dialogListener, Remove_connect_dialog.Remove_connec_dialogListener,Edit_connect_dialg.Edit_connect_dialogListener, TabLayout.OnTabSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ArrayList<Connection> connections;
    private ViewPagerAdapter adapter;
    ViewPager viewPager;
    TabLayout tabLayout;
    boolean uichange;
    String folder_path;
    static boolean pause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uichange=false;

        setUiProperties();


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation);
        setupDrawerContent(navigationView);

        connections = new ArrayList<>();

        setToolbar();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new HelpView(), getResources().getString(R.string.help));
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);


        createFolder();

        readConnections();

    }

    @Override
    protected void onPause(){
        super.onPause();
        MainActivity.pause=true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        MainActivity.pause=false;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        saveConnections();
        for (Connection conn:connections){
            conn.disconnect();
        }
        connections.clear();

    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Poner ícono del drawer toggle
            ab.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
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
                new Add_connect_dialog().show(getSupportFragmentManager(), "New Connection");
                return true;
            case R.id.action_disconnect:
                CharSequence[] items = new CharSequence[connections.size()];
                for(int i=0;i<items.length;i++){
                    items[i]=connections.get(i).getName();
                }
                Remove_connect_dialog dialog= Remove_connect_dialog.newInstance(items);
                dialog.show(getSupportFragmentManager(), "Remove connections");
                return true;
            case R.id.action_exit:
                finish();
                return true;
            case R.id.action_settings:
                adapter = new ViewPagerAdapter(getSupportFragmentManager());
                adapter.addFrag(new SettingsView(),getResources().getString(R.string.action_settings));
                viewPager.setAdapter(adapter);
                tabLayout.setupWithViewPager(viewPager);
                setTitle("INDIandroidUI");
                return true;
            case R.id.action_log:
                adapter = new ViewPagerAdapter(getSupportFragmentManager());
                for(Connection conn:connections){
                    if(conn.getLog()!=null) {
                        System.out.println(conn.getLog());
                        LogView.text = conn.getLog();
                    }
                    adapter.addFrag(new LogView(), "Log_" + conn.getName());
                }
                tabLayout.setOnTabSelectedListener(this);
                viewPager.setAdapter(adapter);
                tabLayout.setupWithViewPager(viewPager);
                setTitle("INDIandroidUI");
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDrawerMenu(){
        int i=0,j=0;
        Menu menu=navigationView.getMenu();
        menu.clear();
        for(i=0;i<connections.size();i++){
            j=0;
            Connection conn=connections.get(i);
            IndiClient client=conn.getClient();
            SubMenu sub= menu.addSubMenu(conn.getName());
            if(client!=null) {
                for (; j < client.getDevicesNames().size(); j++) {
                    String device = client.getDevicesNames().get(j);
                    MenuItem item=sub.add(i, i + j, j, device);
                    item.setTitle(device);
                    item.setIcon(R.drawable.ic_developer_board_black_24dp);
                    item.setEnabled(true);
                    item.setVisible(true);
                    item.setCheckable(true);
                }
            }

            if(conn.isConnected()) {
                MenuItem item = sub.add(i, i + j, j,R.string.menu_disconnect);
                item.setTitle(R.string.menu_disconnect);
                item.setIcon(android.R.drawable.ic_lock_power_off);
                item.setEnabled(true);
                item.setVisible(true);
            }else{
                MenuItem item = sub.add(i, i + j, j,R.string.menu_connect);
                item.setTitle(R.string.menu_connect);
                item.setIcon(android.R.drawable.ic_lock_power_off);
                item.setEnabled(true);
                item.setVisible(true);
                MenuItem item2 = sub.add(i, i + j+1, j+1,R.string.menu_disconnect);
                item2.setTitle(R.string.menu_edit);
                item2.setIcon(R.drawable.ic_mode_edit_black_24dp);
                item2.setEnabled(true);
                item2.setVisible(true);
            }
        }
        menu.add("").setVisible(false);
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onConnectButtonClick(String name,String host, int port,boolean autoconnect, boolean blobs_enable) {
        Connection conn=new Connection(name,host,port,autoconnect,blobs_enable,this);
        //conn.connect();
        connections.add(conn);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        Connection conn = connections.get(menuItem.getGroupId());
                        IndiClient client = conn.getClient();
                        int order = 0;
                        if (client != null) {
                            order = client.getDevicesNames().size();
                        }

                        if (menuItem.getOrder() == order) {
                            if (conn.isConnected()) {
                                conn.disconnect();
                                adapter = new ViewPagerAdapter(getSupportFragmentManager());
                                adapter.addFrag(new HelpView(), getResources().getString(R.string.help));
                                viewPager.setAdapter(adapter);
                                tabLayout.setupWithViewPager(viewPager);
                                setTitle("INDIandroidUI");
                                drawerLayout.closeDrawers();
                            } else {
                                conn.connect();
                                drawerLayout.closeDrawers();
                            }


                        } else if (menuItem.getOrder() < order) {
                            ArrayList<PropertyArrayAdapter> adapters = conn.getAdapters();
                            //list.setAdapter(adapters.get(menuItem.getOrder()));
                            DefaultDeviceView.adapter = (PropertyArrayAdapter) adapters.get(menuItem.getOrder());
                            adapter = new ViewPagerAdapter(getSupportFragmentManager());
                            adapter.addFrag(new HelpView(), getResources().getString(R.string.help));
                            adapter.addFrag(new DefaultDeviceView(), "Default View");
                            viewPager.setAdapter(adapter);
                            tabLayout.setupWithViewPager(viewPager);
                            viewPager.setCurrentItem(1);
                            drawerLayout.closeDrawers();
                            setTitle(conn.getClient().getDevicesNames().get(menuItem.getOrder()));
                        } else {
                            Edit_connect_dialg dialog = Edit_connect_dialg.newInstance(connections.get(menuItem.getGroupId()), menuItem.getGroupId());
                            dialog.show(getSupportFragmentManager(), "Edit connections");
                        }
                        return true;
                    }
                }
        );
    }

    @Override
    public void onDisconnectButtonClick(ArrayList<Integer> itemsSeleccionados) {
        ArrayList<Connection> aux_connections=new ArrayList<>(connections);
        for(int i=0;i<itemsSeleccionados.size();i++){
            connections.remove(aux_connections.get(i));
        }
    }

    private void readConnections(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File f = new File(folder_path, "connections.txt");

            try {
                BufferedReader fin =
                        new BufferedReader(
                                new InputStreamReader(
                                        new FileInputStream(f)));
                String text=fin.readLine();
                while(text!=null){
                    String[] data=text.split(",");
                    String name=data[0];
                    String host=data[1];
                    String autoconnect_value=data[3];
                    String blobs_enable_value=data[4];
                    int port=Integer.parseInt(data[2]);
                    boolean autoconnect,blobs_enable;

                    if(autoconnect_value.equals("false")){
                        autoconnect=false;
                    }else{
                        autoconnect=true;
                    }

                    if(blobs_enable_value.equals("false")){
                        blobs_enable=false;
                    }else{
                        blobs_enable=true;
                    }

                    Connection conn=new Connection(name,host,port,autoconnect,blobs_enable,this);
                    connections.add(conn);
                    text=fin.readLine();
                }
                fin.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        for(Connection conn:connections){
            if(conn.getAutoconnect()){
                conn.connect();
            }
        }
    }


    private void saveConnections(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File f = new File(folder_path, "connections.txt");
            try {
                OutputStreamWriter fout =
                        new OutputStreamWriter(
                                new FileOutputStream(f));

                for (Connection conn:connections){
                    String s=conn.getName()+','+conn.getHost()+','+conn.getPort()+','+conn.getAutoconnect()+','+conn.getBlobsEnable();
                    fout.write(s);
                    fout.write('\n');
                }
                fout.flush();
                fout.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEditButtonClick(String name,String host, int port,boolean autoconnect,boolean blobs_enable,int position) {
        Connection conn=new Connection(name,host,port,autoconnect,blobs_enable,this);
        connections.set(position, conn);
        setDrawerMenu();
    }

    private void setUiProperties() {
        //add UI object
        Config.init();
        Config.addUiPropertyManager(new UIBlobPropertyManager());
        Config.addUiPropertyManager(new UITextPropertyManager());
        Config.addUiPropertyManager(new UISwitchPropertyManager());
        Config.addUiPropertyManager(new UINumberPropertyManager());
        Config.addUiPropertyManager(new UILightPropertyManager());
        Config.addUiPropertyManager(new UIConnecPropertyManager());

    }

    public void set_uichange(boolean change){
        uichange=change;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        String log=connections.get(tab.getPosition()).getLog();
        if(log!=null)
            LogView.text=log;
        else{
            LogView.text="";
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void createFolder(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sd = Environment.getExternalStorageDirectory();
            folder_path=sd.getAbsolutePath()+"/INDIandroidUI";
            File folder = new File(folder_path);
            if(!folder.exists())
                folder.mkdir();

        }
    }
}
