package com.example.jaime.indiandroidui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import laazotea.indi.client.INDIElement;
import laazotea.indi.client.INDILightProperty;
import laazotea.indi.client.INDINumberElement;
import laazotea.indi.client.INDINumberProperty;
import laazotea.indi.client.INDIProperty;
import laazotea.indi.client.INDISwitchElement;
import laazotea.indi.client.INDISwitchProperty;
import laazotea.indi.client.INDITextElement;
import laazotea.indi.client.INDITextProperty;

public class PropertyArrayAdapter<T> extends ArrayAdapter<T> {

    private TreeSet<UIPropertyManager> uiProperties;

    public PropertyArrayAdapter(Context context, List<T> objects) {
        super(context, 0, objects);
        setUiProperties();
    }

    private void setUiProperties() {
        //Create order set
        uiProperties = new TreeSet<>(new UIPropertyManagerOrder());

        //add UI object
        uiProperties.add(new UIBlobPropertyManager());
        uiProperties.add(new UITextPropertyManager());
        uiProperties.add(new UISwitchPropertyManager());
        uiProperties.add(new UINumberPropertyManager());
        uiProperties.add(new UILightPropertyManager());
        uiProperties.add(new UIConnecPropertyManager());

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
        for( Iterator it = uiProperties.iterator(); it.hasNext() && !end;){
            UIPropertyManager aux=(UIPropertyManager)it.next();
            if(aux.handlesProperty(p)){
                end=true;
                ui=aux;
            }
        }

        if(convertView == null){
            listItemView = ui.getPropertyView(p,inflater,parent);
        }

        ui.updateView(p,listItemView);
        
        return listItemView;

    }
}
