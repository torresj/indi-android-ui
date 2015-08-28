package com.example.jaime.indiandroidui;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.Iterator;
import java.util.TreeSet;

import laazotea.indi.client.INDIBLOBProperty;
import laazotea.indi.client.INDIProperty;


public class PropertyArrayAdapter extends BaseExpandableListAdapter {

    private SparseArray<Groups_properties> groups;
    private LayoutInflater inflater;
    private AppCompatActivity activity;
    // Constructor
    public PropertyArrayAdapter(AppCompatActivity act, SparseArray<Groups_properties> groups) {
        activity = act;
        this.groups = groups;
        inflater = act.getLayoutInflater();
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).properties.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).properties.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.group_item_view, null);

        TextView text=(TextView)convertView.findViewById(R.id.text);

        text.setText(groups.get(groupPosition).string);
        if(groups.get(groupPosition).string=="") {
            text.setEnabled(false);
            text.setPadding(0, 0, 0, 0);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //Salvando la referencia del View de la fila
        View listItemView = convertView;

        //Obteniendo instancia de la ViewProperty en la posición actual
        INDIProperty p = groups.get(groupPosition).properties.get(childPosition);

        UIPropertyManager ui=null;

        boolean end = false;
        TreeSet<UIPropertyManager> uiProperties = Config.getUIProperties();
        for( Iterator it = uiProperties.iterator(); it.hasNext() && !end;){
            UIPropertyManager aux=(UIPropertyManager)it.next();
            if(aux.handlesProperty(p)){
                end=true;
                ui=aux;
            }
        }

        listItemView = ui.getPropertyView(p,inflater,parent,activity);

        ui.updateView(p, listItemView);

        return listItemView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        INDIProperty p = (INDIProperty)groups.get(groupPosition).properties.get(childPosition);
        if(p instanceof INDIBLOBProperty){
            return false;
        }else {
            return true;
        }
    }

    public void add(Groups_properties group){
        groups.append(groups.size(),group);
    }

    public void clear(){
        groups.clear();
    }
}
