package com.example.jaime.indiandroidui;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import laazotea.indi.Constants;
import laazotea.indi.client.INDIElement;
import laazotea.indi.client.INDIProperty;
import laazotea.indi.client.INDISwitchElement;
import laazotea.indi.client.INDIValueException;

/**
 * Created by Jaime on 18/8/15.
 */
public class UIConnecPropertyManager implements UIPropertyManager {
    //Atributes
    int layout;
    int layout_dialog;

    public UIConnecPropertyManager(){
        layout=R.layout.connec_property_view_list_item;
        layout_dialog=R.layout.connec_property_edit_view;
    }

    @Override
    public boolean handlesProperty(INDIProperty p) {

        if(p.getName().equals("CONNECTION"))
            return true;
        else
            return false;
    }

    @Override
    public View getPropertyView(INDIProperty p, LayoutInflater inflater, ViewGroup parent, Context context) {
        View v=inflater.inflate(layout, parent, false);
        return v;
    }


    @Override
    public void updateView(INDIProperty p, View v) {
        setView(v, p);
    }

    @Override
    public View getUpdateView(INDIProperty p, LayoutInflater inflater, DialogFragment fragment) {
        View v = inflater.inflate(layout_dialog,null);
        TextView name=(TextView)v.findViewById(R.id.property_name);
        Switch s=(Switch)v.findViewById(R.id.conn_switch);

        ArrayList<INDIElement> list = p.getElementsAsList();
        INDISwitchElement elem=(INDISwitchElement)list.get(0);

        if (elem.getValue().equals(Constants.SwitchStatus.ON))
            s.setChecked(true);
        else
            s.setChecked(false);

        name.setText(p.getLabel());
        return v;
    }


    @Override
    public void updateProperty(INDIProperty p, View v) {
        Switch s=(Switch)v.findViewById(R.id.conn_switch);

        ArrayList<INDIElement> list = p.getElementsAsList();
        INDISwitchElement conect=(INDISwitchElement)list.get(0);
        INDISwitchElement disconect=(INDISwitchElement)list.get(1);

        try {
            if(s.isChecked()){
                disconect.setDesiredValue(Constants.SwitchStatus.OFF);
                conect.setDesiredValue(Constants.SwitchStatus.ON);
            }else{
                disconect.setDesiredValue(Constants.SwitchStatus.ON);
                conect.setDesiredValue(Constants.SwitchStatus.OFF);
            }

        p.sendChangesToDriver();

        } catch (INDIValueException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getPriority() {
        return 5;
    }

    void setView(View v, INDIProperty p){
        //Views
        TextView name = (TextView)v.findViewById(R.id.name);
        ImageView idle = (ImageView)v.findViewById(R.id.idle);
        TextView perm = (TextView)v.findViewById(R.id.perm);
        ImageView visibility = (ImageView)v.findViewById(R.id.visibility);
        TextView element = (TextView)v.findViewById(R.id.element);

        //others
        int light_res=0;
        String perm_res="";
        int visibility_res=0;

        ArrayList<INDIElement> list = p.getElementsAsList();

        String text="";

        INDISwitchElement elem=(INDISwitchElement)list.get(0);

        if (elem.getValue().equals(Constants.SwitchStatus.ON))
            text="Connected";
        else
            text="Disconnected";

        element.setText(text);


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
}
