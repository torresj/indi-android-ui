package com.jtbenavente.jaime.indiandroidui;


import android.content.Context;
import android.content.SharedPreferences;
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

import org.indilib.i4j.protocol.url.INDIURLStreamHandlerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;





public class MainActivity extends AppCompatActivity implements Add_connect_dialog.Add_connec_dialogListener, Remove_connect_dialog.Remove_connec_dialogListener,Edit_connect_dialg.Edit_connect_dialogListener,Demo_dialog.Demo_dialogListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ArrayList<Connection> connections;
    private Connection demo_conn;
    private ViewPagerAdapter adapter;
    ViewPager viewPager;
    TabLayout tabLayout;
    boolean uichange;
    Settings settings;
    static boolean pause;

    static {
        INDIURLStreamHandlerFactory.init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings=Settings.getInstance();

        uichange=false;

        setUiProperties();


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation);
        setupDrawerContent(navigationView);

        connections = new ArrayList<>();
        demo_conn = null;

        setToolbar();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new HelpView(), getResources().getString(R.string.help));
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);


        createFolder();

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        boolean show_demo_dialog = sharedPref.getBoolean("show_demo_dialog",true);

        if(show_demo_dialog){
            new Demo_dialog().show(getFragmentManager(),"Demo dialog");
        }

        readConnections();

    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
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
                setTitle(getResources().getString(R.string.app_name));
                return true;
            case R.id.action_demo:
                new Demo_dialog().show(getFragmentManager(),"Demo dialog");
                return true;
            case R.id.action_log:
                adapter = new ViewPagerAdapter(getSupportFragmentManager());
                for(Connection conn:connections){
                    LogView logView=LogView.newInstance(conn.getHost());
                    adapter.addFrag(logView, "Log_" + conn.getName());
                }
                viewPager.setAdapter(adapter);
                tabLayout.setupWithViewPager(viewPager);
                setTitle(getResources().getString(R.string.app_name));
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
        if(name.equals("Demo"))
            demo_conn = conn;
        connections.add(conn);
        saveConnections();
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
                                setTitle(getResources().getString(R.string.app_name));
                                drawerLayout.closeDrawers();
                            } else {
                                conn.connect();
                                drawerLayout.closeDrawers();
                            }


                        } else if (menuItem.getOrder() < order) {
                            ArrayList<PropertyArrayAdapter> adapters = conn.getAdapters();
                            //list.setAdapter(adapters.get(menuItem.getOrder()));
                            DefaultDeviceView.adapter = (PropertyArrayAdapter) adapters.get(menuItem.getOrder());
                            DefaultDeviceView.conn=conn;
                            adapter = new ViewPagerAdapter(getSupportFragmentManager());
                            adapter.addFrag(new HelpView(), getResources().getString(R.string.help));
                            adapter.addFrag(new DefaultDeviceView(), getResources().getString(R.string.default_view));
                            viewPager.setAdapter(adapter);
                            tabLayout.setupWithViewPager(viewPager);
                            viewPager.setCurrentItem(1);
                            drawerLayout.closeDrawers();
                            setTitle(conn.getClient().getDevicesNames().get(menuItem.getOrder()));
                        } else {
                        if(conn != demo_conn) {
                            Edit_connect_dialg dialog = Edit_connect_dialg.newInstance(connections.get(menuItem.getGroupId()), menuItem.getGroupId());
                            dialog.show(getSupportFragmentManager(), "Edit connections");
                        }else{
                            Alert_dialog alert = Alert_dialog.newInstance(getResources().getString(R.string.alert_demo_edit));
                            alert.show(getSupportFragmentManager(), "AlertDialog");
                        }
                    }
                        return true;
                    }
                }
        );
    }

    @Override
    public void onDisconnectButtonClick(ArrayList<Integer> itemsSeleccionados) {

        for(int i=itemsSeleccionados.size()-1;i>=0;i--){
            int index=itemsSeleccionados.get(i);
            boolean end=false;
            for(int j=connections.size()-1;j>=0 && !end;j--){
                if(index==j){
                    end=true;
                    File f=new File(settings.getFolderPath()+"/properties/"+connections.get(j).getHost()+".txt");
                    if(f.exists()){
                        f.delete();
                    }
                    Connection conn = connections.remove(j);
                    if (conn != null && demo_conn != null) {
                        if (conn.getName().equals(demo_conn.getName()))
                            demo_conn = null;
                    }
                }
            }
        }
    }

    private void readConnections(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File f = new File(settings.getFolderPath(), "connections.txt");

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
                    if(conn.getName().equals("Demo"))
                        demo_conn = conn;
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
            File f = new File(settings.getFolderPath(), "connections.txt");
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
        saveConnections();
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
        Config.addUiPropertyManager(new UIAbortPropertyManager());

    }

    public void set_uichange(boolean change){
        uichange=change;
    }

    private void createFolder(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sd = Environment.getExternalStorageDirectory();
            File folder = new File(settings.getFolderPath());
            if(!folder.exists())
                folder.mkdir();

            //Properties hide folder
            File porperties=new File(settings.getFolderPath()+"/properties");
            if(!porperties.exists())
                porperties.mkdir();

            //Log folder
            File log=new File(settings.getFolderPath()+"/log");
            if(!log.exists())
                log.mkdir();
        }
    }

    @Override
    public void onOkButtonClick() {
        if(demo_conn == null) {
            onConnectButtonClick("Demo", "217.216.18.195", 7624, false, true);
            Alert_dialog alert = Alert_dialog.newInstance(getResources().getString(R.string.alert_demo));
            alert.show(getSupportFragmentManager(), "AlertDialog");
        }else{
            Alert_dialog alert = Alert_dialog.newInstance(getResources().getString(R.string.alert_demo_added));
            alert.show(getSupportFragmentManager(), "AlertDialog");
        }
    }
}
