package com.example.jaime.indiandroidui;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
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
import laazotea.indi.client.INDINumberElement;
import laazotea.indi.client.INDINumberProperty;
import laazotea.indi.client.INDIProperty;
import laazotea.indi.client.INDIValueException;


/**
 * Created by Jaime on 5/8/15.
 */
public class UINumberPropertyManager implements UIPropertyManager,View.OnClickListener {

    //Atributes
    int layout;
    int layout_dialog;
    Context context;

    public UINumberPropertyManager(){
        layout=R.layout.number_property_view_list_item;
        layout_dialog=R.layout.number_property_edit_view;
    }

    @Override
    public boolean handlesProperty(INDIProperty p) {
        return p instanceof INDINumberProperty;
    }

    @Override
    public View getPropertyView(INDIProperty p, LayoutInflater inflater, ViewGroup parent, Context context) {
        this.context=context;
        if (p instanceof INDINumberProperty){
            View v=inflater.inflate(layout, parent, false);
            return v;
        }else{
            return null;
        }
    }


    @Override
    public void updateView(INDIProperty p, View v) {
        if (p instanceof INDINumberProperty){
            setView(v,(INDINumberProperty)p);
        }
    }

    @Override
    public View getUpdateView(INDIProperty p, LayoutInflater inflater,DialogFragment fragment) {
        View v = inflater.inflate(layout_dialog,null);
        TextView name=(TextView)v.findViewById(R.id.property_name);
        TableLayout table = (TableLayout)v.findViewById(R.id.table);
        INDINumberProperty p_n = (INDINumberProperty)p;

        ArrayList<INDIElement> list = p_n.getElementsAsList();

        for(int i=0;i<list.size();i++) {
            TableRow row = (TableRow) LayoutInflater.from(fragment.getActivity()).inflate(R.layout.text_row, null);
            TextView label = (TextView) row.findViewById(R.id.label);
            EditText edit = (EditText) row.findViewById(R.id.edit_text);

            INDINumberElement elem = (INDINumberElement)list.get(i);

            label.setText(elem.getLabel()+"\n[Max: "+elem.getMaxAsString()+", Min: "+elem.getMinAsString()+"]" );
            edit.setText(elem.getValueAsString());

            table.addView(row);
        }

        name.setText(p.getLabel());
        return v;
    }


    @Override
    public void updateProperty(INDIProperty p, View v) {

        TableLayout table = (TableLayout)v.findViewById(R.id.table);

        ArrayList<INDIElement> list = p.getElementsAsList();

        int rows=table.getChildCount();

        try{
            for(int i=0;i<rows;i++){
                TableRow row=(TableRow)table.getChildAt(i);
                EditText value = (EditText)row.findViewById(R.id.edit_text);
                INDINumberElement elem=(INDINumberElement)list.get(i);
                String n=value.getText().toString();

                elem.setDesiredValue(n);
            }

            p.sendChangesToDriver();

        } catch (INDIValueException e) {
            AppCompatActivity act=(AppCompatActivity)context;
            Alert_dialog alert = Alert_dialog.newInstance(e.getMessage()+".");
            alert.show(act.getSupportFragmentManager(), "Alert number out of range");
        } catch (IllegalArgumentException e){
            AppCompatActivity act=(AppCompatActivity)context;
            Alert_dialog alert = Alert_dialog.newInstance(e.getMessage()+".");
            alert.show(act.getSupportFragmentManager(), "Alert number format error");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getPriority() {
        return 2;
    }

    void setView(View v, INDINumberProperty p){
        //Views
        TextView name = (TextView)v.findViewById(R.id.name);
        ImageView idle = (ImageView)v.findViewById(R.id.idle);
        TextView perm = (TextView)v.findViewById(R.id.perm);
        ImageView visibility = (ImageView)v.findViewById(R.id.visibility);
        TextView element = (TextView)v.findViewById(R.id.element);
        visibility.setTag(p);
        visibility.setFocusable(false);
        visibility.setOnClickListener(this);

        //others
        int light_res=0;
        String perm_res="";
        int visibility_res=0;

        ArrayList<INDIElement> list = p.getElementsAsList();

        String text="";
        for(int i=0;i<list.size();i++){
            INDINumberElement elem=(INDINumberElement)list.get(i);
            text=text+"<b>"+elem.getLabel()+": </b>"+elem.getValueAsString()+"<br />";
        }
        element.setText(Html.fromHtml(text));


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

        if(DefaultDeviceView.conn.isPropertyHide(p))
            visibility_res=R.drawable.ic_visibility_off_black_24dp;
        else
            visibility_res=R.drawable.ic_visibility_black_24dp;


        name.setText(p.getLabel());
        idle.setImageResource(light_res);
        perm.setText(perm_res);
        visibility.setImageResource(visibility_res);
    }

    @Override
    public void onClick(View v) {
        INDIProperty p=(INDIProperty)v.getTag();
        Connection conn=DefaultDeviceView.conn;
        if(conn.isPropertyHide(p)){
            conn.showProperty(p);
        }else{
            conn.hideProperty(p);
        }
    }
}
