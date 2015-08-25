package com.example.jaime.indiandroidui;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import laazotea.indi.Constants;
import laazotea.indi.client.INDILightProperty;
import laazotea.indi.client.INDIProperty;

/**
 * Created by Jaime on 5/8/15.
 */
public class UILightPropertyManager implements UIPropertyManager {

    //Atributes
    int layout;
    int layout_dialog;

    public UILightPropertyManager(){
        layout=R.layout.light_property_view_list_item;
        layout_dialog=R.layout.light_property_edit_view;
    }

    @Override
    public boolean handlesProperty(INDIProperty p) {
        return p instanceof INDILightProperty;
    }

    @Override
    public View getPropertyView(INDIProperty p, LayoutInflater inflater, ViewGroup parent, Context context) {
        if (p instanceof INDILightProperty){
            View v=inflater.inflate(layout, parent, false);
            return v;
        }else{
            return null;
        }
    }

    @Override
    public void updateView(INDIProperty p, View v) {
        if (p instanceof INDILightProperty){
            setView(v,(INDILightProperty)p);
        }
    }

    @Override
    public View getUpdateView(INDIProperty p, LayoutInflater inflater, DialogFragment fragment) {
        View v = inflater.inflate(layout_dialog,null);
        TextView name=(TextView)v.findViewById(R.id.property_name);
        INDILightProperty p_l= (INDILightProperty) p;
        name.setText(p_l.getLabel());
        return v;
    }

    @Override
    public void updateProperty(INDIProperty p, View v) {

    }

    @Override
    public int getPriority() {
        return 1;
    }

    void setView(View v, INDILightProperty p){
        //Views
        TextView name = (TextView)v.findViewById(R.id.name);
        ImageView idle = (ImageView)v.findViewById(R.id.idle);
        ImageView perm = (ImageView)v.findViewById(R.id.perm);
        ImageView visibility = (ImageView)v.findViewById(R.id.visibility);
        TextView element = (TextView)v.findViewById(R.id.element);

        //others
        int light_res=0;
        int perm_res=0;
        int visibility_res=0;

        element.setText("");


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
            perm_res=R.drawable.read;
        }else if(p.getPermission().equals(Constants.PropertyPermissions.WO)){
            perm_res=R.drawable.write;
        }else{
            perm_res=R.drawable.rw;
        }

        visibility_res=R.drawable.ic_visibility_black_24dp;


        name.setText(p.getLabel());
        idle.setImageResource(light_res);
        perm.setImageResource(perm_res);
        visibility.setImageResource(visibility_res);
    }
}
