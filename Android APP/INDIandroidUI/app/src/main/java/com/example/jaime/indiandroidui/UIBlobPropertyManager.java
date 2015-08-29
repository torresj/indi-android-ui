package com.example.jaime.indiandroidui;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.GregorianCalendar;

import laazotea.indi.Constants;
import laazotea.indi.INDIBLOBValue;
import laazotea.indi.client.INDIBLOBElement;
import laazotea.indi.client.INDIBLOBProperty;
import laazotea.indi.client.INDIElement;
import laazotea.indi.client.INDIProperty;

/**
 * Created by Jaime on 5/8/15.
 */
public class UIBlobPropertyManager implements UIPropertyManager {

    //Atributes
    private int layout;
    private int layout_dialog;
    private Map<String, INDIBLOBValue> blobs;
    private Context context;

    public UIBlobPropertyManager(){
        layout=R.layout.blob_property_view_list_item;
        layout_dialog=R.layout.blob_property_edit_view;
        blobs=new HashMap<String, INDIBLOBValue>();
    }

    @Override
    public boolean handlesProperty(INDIProperty p) {
        return p instanceof INDIBLOBProperty;
    }

    @Override
    public View getPropertyView(INDIProperty p, LayoutInflater inflater, ViewGroup parent , Context context) {
        this.context=context;
        if (p instanceof INDIBLOBProperty){
            View v=inflater.inflate(layout, parent, false);
            return v;
        }else{
            return null;
        }
    }

    @Override
    public void updateView(INDIProperty p, View v) {
        if (p instanceof INDIBLOBProperty){
            setView(v,(INDIBLOBProperty)p);
        }
    }

    @Override
    public View getUpdateView(INDIProperty p, LayoutInflater inflater, DialogFragment fragment) {
        View v = inflater.inflate(layout_dialog,null);
        TextView name=(TextView)v.findViewById(R.id.property_name);
        name.setText(p.getLabel());
        return v;
    }

    @Override
    public void updateProperty(INDIProperty p, View v) {

    }

    @Override
    public int getPriority() {
        return 0;
    }

    void setView(View v, INDIBLOBProperty p){
        //Views
        TextView name = (TextView)v.findViewById(R.id.name);
        ImageView idle = (ImageView)v.findViewById(R.id.idle);
        TextView perm = (TextView)v.findViewById(R.id.perm);
        ImageView visibility = (ImageView)v.findViewById(R.id.visibility);
        TextView label = (TextView)v.findViewById(R.id.label);
        TextView type = (TextView)v.findViewById(R.id.type);
        final ImageButton save =(ImageButton)v.findViewById(R.id.save_button);
        ImageButton view =(ImageButton)v.findViewById(R.id.view_button);

        save.setTag(label.getText().toString());
        view.setTag(label.getText().toString());

        //others
        int light_res=0;
        String perm_res="";
        int visibility_res=0;

        ArrayList<INDIElement> list = p.getElementsAsList();
        if(list.size()>0) {
            INDIBLOBElement elem = (INDIBLOBElement) list.get(0);
            INDIBLOBValue blob = elem.getValue();
            label.setText(Html.fromHtml("<b>" + elem.getLabel() + ": </b> " + blob.getSize() + " Bytes"));
            if(blob.getFormat().equals(""))
                type.setText(Html.fromHtml("<b>Type: </b>None"));
            else
                type.setText(Html.fromHtml("<b>Type: </b> " + blob.getFormat()));
            blobs.put(elem.getLabel(), blob);
            save.setTag(elem.getLabel());
            view.setTag(elem.getLabel());
        }

        save.setFocusable(false);
        view.setFocusable(false);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBlob(v);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewBlob(v);
            }
        });


        //State
        if(p.getState().name().equals("IDLE")){
            light_res=R.drawable.grey_light_48;
        }else if(p.getState().name().equals("OK")){
            light_res=R.drawable.green_light_48;
        }else if(p.getState().name().equals("BUSY")){
            light_res=R.drawable.yellow_light_48;
        }else{
            light_res=R.drawable.red_light_48;
        }

        //Permission
        if(p.getPermission().equals(Constants.PropertyPermissions.RO)){
            perm_res="RO";
        }else if(p.getPermission().equals(Constants.PropertyPermissions.WO)){
            perm_res="WO";
        }else{
            perm_res="RW";
        }

        visibility_res=R.drawable.ic_visibility_black_24dp;


        name.setText(p.getLabel());
        idle.setImageResource(light_res);
        perm.setText(perm_res);
        visibility.setImageResource(visibility_res);
    }

    private void saveBlob(View v){
        INDIBLOBValue blob=blobs.get((String) v.getTag());
        if(blob.getSize()>0) {
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File sd = Environment.getExternalStorageDirectory();
                String folder_path = sd.getAbsolutePath() + "/INDIandroidUI/"+blob.getFormat().substring(1);
                File folder = new File(folder_path);
                if (!folder.exists())
                    folder.mkdir();

                Calendar calendar=GregorianCalendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
                File file = new File(folder.getAbsolutePath(), "/" + (String) v.getTag()+ sdf.format(calendar.getTime()) + blob.getFormat());
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(blob.getBLOBData());
                    fos.close();
                    Toast.makeText(context, context.getResources().getString(R.string.save_text) + ": " + file.getName(), Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }else{
            AppCompatActivity act=(AppCompatActivity)context;
            Alert_dialog alert = Alert_dialog.newInstance(context.getResources().getString(R.string.not_data_save));
            alert.show(act.getSupportFragmentManager(),"Alert No Data");

        }
    }

    private void viewBlob(View v) {
        INDIBLOBValue blob=blobs.get((String) v.getTag());
        if(blob.getSize()>0) {
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            File file = new File(path, "/" + (String) v.getTag() + blob.getFormat());
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(blob.getBLOBData());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "image/" + blob.getFormat().substring(1));
            context.startActivity(intent);
        }else{
            AppCompatActivity act=(AppCompatActivity)context;
            Alert_dialog alert = Alert_dialog.newInstance(context.getResources().getString(R.string.not_data));
            alert.show(act.getSupportFragmentManager(),"Alert No Data");

        }
    }
}
