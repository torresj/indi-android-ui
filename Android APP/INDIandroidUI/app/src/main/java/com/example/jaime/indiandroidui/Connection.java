package com.example.jaime.indiandroidui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;

import laazotea.indi.client.INDIProperty;

/**
 * Created by Jaime on 17/8/15.
 */
public class Connection {

    private IndiClient client;
    private String host;
    private String name;
    private int port;
    private ArrayList<ArrayAdapter> adapters;
    private Context context;
    private IndiConnect thread;
    private boolean connected;
    private boolean error;

    public Connection(String name,String host, int port,Context context){
        this.name=name;
        this.host=host;
        this.port=port;
        this.context=context;
        thread = new IndiConnect();
        client=null;
        adapters = new ArrayList<>();
        connected = false;
        error = false;
    }

    public ArrayList<ArrayAdapter> getAdapters() {
        return adapters;
    }

    public String getName(){
        return name;
    }

    public String getHost(){
        return host;
    }

    public int getPort(){
        return port;
    }
    public boolean hasError(){
        return error;
    }

    public IndiClient getClient(){
        return client;
    }

    public boolean isConnected(){
        return connected;
    }

    public void disconnect(){
        if(thread!=null){
            thread.finishThread();
            thread=null;
        }
        connected=false;
    }

    public void connect(){
        if(thread==null)
            thread=new IndiConnect();
        thread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        connected=true;
    }

    class IndiConnect extends AsyncTask<Void, IndiClient, Void> {

        private boolean end;

        public IndiConnect(){
            end=false;
        }

        @Override protected void onPreExecute() {
            Toast.makeText(context, "Connecting...", Toast.LENGTH_SHORT).show();
            error=false;
        }

        @Override protected Void doInBackground(Void... par) {
            try {
                client = new IndiClient(host, port);
                while(!end){
                    SystemClock.sleep(100);
                    publishProgress(client);
                }
            }catch(Exception e){
                error=true;
            }
            return null;
        }

        @Override protected void onProgressUpdate(IndiClient... prog) {
            IndiClient c=prog[0];
            int size=c.getDevicesNames().size();
            if(size!=adapters.size()){
                adapters.clear();
                for(int i=0;i<size;i++){
                    ArrayList<INDIProperty > l=new ArrayList();
                    ArrayAdapter adapter = new PropertyArrayAdapter<INDIProperty>(context, l);
                    adapters.add(adapter);
                }
            }
            if(size>0) {
                for(int index=0;index<size;index++){
                    ArrayList<INDIProperty> properties = c.getProperties(c.getDevicesNames().get(index));
                    ArrayAdapter adapter = adapters.get(index);
                    if (adapter.getCount() != properties.size()) {
                        adapter.clear();
                        for (int i = 0; i < properties.size(); i++) {
                            INDIProperty p=properties.get(i);
                            adapter.add(p);
                        }
                    }
                    if(c.has_change()) {
                        adapter.notifyDataSetChanged();
                    }

                }
                c.changeRead();
            }
        }


        @Override protected void onPostExecute(Void result) {
            client=null;
            adapters.clear();
        }

        public void finishThread(){
            end=true;
        }

    }
}
