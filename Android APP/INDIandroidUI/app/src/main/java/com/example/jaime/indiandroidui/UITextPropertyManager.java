package com.example.jaime.indiandroidui;

import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import laazotea.indi.Constants;
import laazotea.indi.client.INDIElement;
import laazotea.indi.client.INDIProperty;
import laazotea.indi.client.INDITextElement;
import laazotea.indi.client.INDITextProperty;
import laazotea.indi.client.INDIValueException;

/**
 * Created by Jaime on 5/8/15.
 */
public class UITextPropertyManager implements UIPropertyManager {

    //Atributes
    int layout;
    int layout_dialog;

    public UITextPropertyManager(){
        layout=R.layout.text_property_view_list_item;
        layout_dialog=R.layout.text_property_edit_view;
    }

    @Override
    public boolean handlesProperty(INDIProperty p) {
        return (p instanceof INDITextProperty);
    }

    @Override
    public View getPropertyView(INDIProperty p, LayoutInflater inflater, ViewGroup parent) {
        if (p instanceof INDITextProperty){
            View v=inflater.inflate(layout, parent, false);
            return v;
        }else{
            return null;
        }
    }


    @Override
    public void updateView(INDIProperty p, View v) {
        if (p instanceof INDITextProperty){
            setView(v,(INDITextProperty)p);
        }
    }

    @Override
    public View getUpdateView(INDIProperty p, LayoutInflater inflater,DialogFragment fragment) {
        System.out.println("Text property");
        View v = inflater.inflate(layout_dialog,null);
        TextView name=(TextView)v.findViewById(R.id.property_name);
        TableLayout table = (TableLayout)v.findViewById(R.id.table);
        INDITextProperty p_t = (INDITextProperty)p;

        name.setText(p.getLabel());

        ArrayList<INDIElement> list = p_t.getElementsAsList();

        for(int i=0;i<list.size();i++) {
            TableRow row = (TableRow) LayoutInflater.from(fragment.getActivity()).inflate(R.layout.text_row, null);
            TextView label = (TextView) row.findViewById(R.id.label);
            EditText edit = (EditText) row.findViewById(R.id.edit_text);

            INDITextElement elem = (INDITextElement)list.get(i);

            label.setText(elem.getLabel());
            edit.setText(elem.getValue());

            table.addView(row);
        }


        return v;
    }


    @Override
    public void updateProperty(INDIProperty p, View v) {
        TableLayout table = (TableLayout)v.findViewById(R.id.table);

        ArrayList<INDIElement> list = p.getElementsAsList();

        int rows=table.getChildCount();

        for(int i=0;i<rows;i++){
            TableRow row=(TableRow)table.getChildAt(i);
            EditText value = (EditText)row.findViewById(R.id.edit_text);
            INDITextElement elem=(INDITextElement)list.get(i);
            String text=value.getText().toString();

            try {
                elem.setDesiredValue(text);
            } catch (INDIValueException e) {
                e.printStackTrace();
            }
        }

        try {
            p.sendChangesToDriver();
        } catch (INDIValueException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getPriority() {
        return 4;
    }

    void setView(View v, INDITextProperty p){
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

        ArrayList<INDIElement> list = p.getElementsAsList();

        String text="";
        for(int i=0;i<list.size();i++){
            INDITextElement elem=(INDITextElement)list.get(i);
            text=text+elem.getLabel()+" : "+elem.getValue()+"\n";
        }
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
