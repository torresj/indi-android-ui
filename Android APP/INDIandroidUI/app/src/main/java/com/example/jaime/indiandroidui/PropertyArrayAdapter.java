package com.example.jaime.indiandroidui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

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

        //Comprobando si el View no existe
        if (null == convertView) {
            //Si no existe, entonces inflarlo con two_line_list_item.xml
            listItemView = inflater.inflate(
                    R.layout.view_property_item,
                    parent,
                    false);
        }

        //Obteniendo instancias de los text views
        TextView name = (TextView)listItemView.findViewById(R.id.name);
        TextView element = (TextView)listItemView.findViewById(R.id.element);
        ImageView idle = (ImageView)listItemView.findViewById(R.id.idle);
        ImageView perm = (ImageView)listItemView.findViewById(R.id.perm);
        ImageView visibility = (ImageView)listItemView.findViewById(R.id.visibility);

        //Obteniendo instancia de la ViewProperty en la posición actual
        ViewProperty item = (ViewProperty)getItem(position);


        name.setText(item.getName());
        element.setText("");
        idle.setImageResource(item.getIdle());
        perm.setImageResource(item.getPermission());
        visibility.setImageResource(item.getVisibility());

        //Devolver al ListView la fila creada
        return listItemView;

    }
}
