package com.example.jaime.indiandroidui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import laazotea.indi.client.INDIBLOBProperty;
import laazotea.indi.client.INDIProperty;


public class PropertyArrayAdapter<T> extends ArrayAdapter<T> {


    public PropertyArrayAdapter(Context context, List<T> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        //Obteniendo una instancia del inflater
        LayoutInflater inflater = (LayoutInflater)getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Salvando la referencia del View de la fila
        View listItemView = convertView;

        //Obteniendo instancia de la ViewProperty en la posición actual
        INDIProperty p = (INDIProperty)getItem(position);

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

        listItemView = ui.getPropertyView(p,inflater,parent,this.getContext());

        ui.updateView(p,listItemView);
        
        return listItemView;

    }
}
