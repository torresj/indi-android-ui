package com.jtbenavente.jaime.indiandroidui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Jaime on 27/8/15.
 */
public class LogView extends Fragment {

    private TextView text_view;
    private Settings settings;
    private String connection;

    static LogView newInstance(String connection){
        LogView fragment = new LogView();
        Bundle args = new Bundle();
        args.putString("connection", connection);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        connection=getArguments().getString("connection");

        View view = inflater.inflate(R.layout.log_view, container, false);
        text_view=(TextView) view.findViewById(R.id.text);
        settings=Settings.getInstance();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        try {
            File f=new File(settings.getFolderPath()+"/log/"+connection+".txt");
            FileReader log=new FileReader(f);
            BufferedReader br = new BufferedReader(log);
            String s;
            String text="";

            if(f.length()<=512){
                while((s = br.readLine()) != null) {
                    text+=s+" \n ";
                }
            }else {
                br.skip(f.length()-512);
                while ((s = br.readLine()) != null) {
                    text += s + " \n ";
                }
            }
            log.close();
            text_view.setText(text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
