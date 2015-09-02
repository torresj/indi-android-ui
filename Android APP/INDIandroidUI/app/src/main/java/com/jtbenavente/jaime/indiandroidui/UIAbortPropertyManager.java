package com.jtbenavente.jaime.indiandroidui;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import org.indilib.i4j.Constants;
import org.indilib.i4j.client.INDIElement;
import org.indilib.i4j.client.INDIProperty;
import org.indilib.i4j.client.INDISwitchElement;
import org.indilib.i4j.client.INDISwitchProperty;
import org.indilib.i4j.client.INDIValueException;

/**
 * Created by Jaime on 1/9/15.
 */
public class UIAbortPropertyManager implements UIPropertyManager,View.OnClickListener {

    //Atributes
    int layout;
    int layout_dialog;
    Button button;
    Context context;

    public UIAbortPropertyManager(){
        layout=R.layout.abort_property_view_list_item;
        layout_dialog=R.layout.abort_property_edit_view;
    }

    @Override
    public boolean handlesProperty(INDIProperty p) {
        if(p instanceof INDISwitchProperty){
            ArrayList<INDIElement> list =(ArrayList) p.getElementsAsList();
            if(list.size()==1) {
                INDISwitchElement elem = (INDISwitchElement) list.get(0);
                if(elem.getLabel().equals("Abort")){
                    return true;
                }
                else{
                    return false;
                }
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    @Override
    public View getPropertyView(INDIProperty p, LayoutInflater inflater, ViewGroup parent, Context context) {
        this.context=context;
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
        button=(Button)v.findViewById(R.id.abort_button);
        name.setText(p.getLabel());
        return v;
    }

    @Override
    public void updateProperty(INDIProperty p, View v) {
        ArrayList<INDIElement> list =(ArrayList) p.getElementsAsList();
        INDISwitchElement abort=(INDISwitchElement)list.get(0);

        try {
            abort.setDesiredValue(Constants.SwitchStatus.ON);
            p.sendChangesToDriver();
        } catch (INDIValueException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getPriority() {
        return 9;
    }

    @Override
    public Button getUpdateButton() {
        return button;
    }

    void setView(View v, INDIProperty p){
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

        String text=context.getResources().getString(R.string.abort_text);

        element.setText(Html.fromHtml("<strong><font color=\"red\"><u>"+text+"</u></font></strong>"));


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
