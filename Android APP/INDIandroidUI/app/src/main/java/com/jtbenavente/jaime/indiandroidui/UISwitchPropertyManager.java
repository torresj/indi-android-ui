package com.jtbenavente.jaime.indiandroidui;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;

import laazotea.indi.Constants;
import laazotea.indi.client.INDIElement;
import laazotea.indi.client.INDIProperty;
import laazotea.indi.client.INDISwitchElement;
import laazotea.indi.client.INDISwitchProperty;
import laazotea.indi.client.INDIValueException;

/**
 * Created by Jaime on 5/8/15.
 */
public class UISwitchPropertyManager implements UIPropertyManager, View.OnClickListener {

    //Atributes
    int layout;
    int layout_dialog;
    Button button;
    ArrayList<ToggleButton> values;

    public UISwitchPropertyManager(){
        layout=R.layout.switch_property_view_list_item;
        layout_dialog=R.layout.switch_property_edit_view;

    }

    @Override
    public boolean handlesProperty(INDIProperty p) {
        return p instanceof INDISwitchProperty;
    }

    @Override
    public View getPropertyView(INDIProperty p, LayoutInflater inflater, ViewGroup parent, Context context) {
        if (p instanceof INDISwitchProperty){
            View v=inflater.inflate(layout, parent, false);
            return v;
        }else{
            return null;
        }
    }

    @Override
    public void updateView(INDIProperty p, View v) {
        if (p instanceof INDISwitchProperty){
            setView(v,(INDISwitchProperty)p);
        }
    }

    @Override
    public View getUpdateView(INDIProperty p, LayoutInflater inflater,DialogFragment fragment) {

        ArrayList<INDIElement> list = p.getElementsAsList();
        values=new ArrayList<>();

        View v = inflater.inflate(layout_dialog,null);
        TextView name=(TextView)v.findViewById(R.id.property_name);
        TableLayout table = (TableLayout)v.findViewById(R.id.table);
        button=(Button)v.findViewById(R.id.update_button);
        INDISwitchProperty s = (INDISwitchProperty)p;


        for(int i=0;i<list.size();i++) {
            TableRow row = (TableRow) LayoutInflater.from(fragment.getActivity()).inflate(R.layout.switch_row, null);
            TextView label = (TextView) row.findViewById(R.id.label);
            ToggleButton value = (ToggleButton) row.findViewById(R.id.value);

            if(s.getRule()==Constants.SwitchRules.ONE_OF_MANY || s.getRule()==Constants.SwitchRules.AT_MOST_ONE) {
                value.setOnClickListener(this);
            }

            INDISwitchElement elem=(INDISwitchElement)list.get(i);

            label.setText(elem.getLabel());
            if(elem.getValue()==Constants.SwitchStatus.ON)
                value.setChecked(true);
            else
                value.setChecked(false);

            table.addView(row);
            values.add(value);
        }

        name.setText(p.getLabel());
        return v;
    }

    @Override
    public void updateProperty(INDIProperty p, View v) {

        INDISwitchProperty s = (INDISwitchProperty) p;
        TableLayout table = (TableLayout)v.findViewById(R.id.table);

        ArrayList<INDIElement> list = p.getElementsAsList();

        int rows=table.getChildCount();

        for(int i=0;i<rows;i++){
            TableRow row=(TableRow)table.getChildAt(i);
            ToggleButton value = (ToggleButton) row.findViewById(R.id.value);
            INDISwitchElement elem=(INDISwitchElement)list.get(i);

            try {
                if(value.isChecked()){
                    elem.setDesiredValue(Constants.SwitchStatus.ON);
                }else{
                    elem.setDesiredValue(Constants.SwitchStatus.OFF);
                }

                p.sendChangesToDriver();

            } catch (INDIValueException e) {
                System.out.println(e.getLocalizedMessage());
            } catch (IOException e) {
                System.out.println(e.getLocalizedMessage());
            }
        }

    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public Button getUpdateButton() {
        return button;
    }

    void setView(View v, INDISwitchProperty p){
        //Views
        TextView name = (TextView)v.findViewById(R.id.name);
        ImageView idle = (ImageView)v.findViewById(R.id.idle);
        TextView perm = (TextView)v.findViewById(R.id.perm);
        ImageView visibility = (ImageView)v.findViewById(R.id.visibility);
        TextView element = (TextView)v.findViewById(R.id.element);

        visibility.setTag(p);
        visibility.setFocusable(false);
        visibility.setOnClickListener(new View.OnClickListener() {
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
        });

        //others
        int light_res=0;
        String perm_res="";
        int visibility_res=0;

        ArrayList<INDIElement> list = p.getElementsAsList();

        String text="";
        for(int i=0;i<list.size();i++){
            INDISwitchElement elem=(INDISwitchElement)list.get(i);
            if(elem.getValue().equals(Constants.SwitchStatus.ON)) {
                text = text + "<b>" +elem.getLabel() + ": </b>" + elem.getValue() + "<br />";
            }else{
                text = text + "<font color=\"grey\"><b>" +elem.getLabel() + ": </b>" + elem.getValue() + "</font><br />";
            }
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
        ToggleButton b=(ToggleButton)v;

        for(ToggleButton value:values){
            if(!b.equals(value))
                value.setChecked(false);
        }
    }
}
