package com.example.jaime.indiandroidui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;

import laazotea.indi.Constants;
import laazotea.indi.client.INDILightProperty;
import laazotea.indi.client.INDIProperty;

/**
 * Created by Jaime on 26/8/15.
 */
public class DefaultDeviceView extends Fragment implements ExpandableListView.OnChildClickListener, FloatingActionButton.OnClickListener{

    private ExpandableListView list;
    static PropertyArrayAdapter adapter;
    static Connection conn;
    private String title;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.default_device_view, container, false);

        list=(ExpandableListView)view.findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnChildClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.attachToListView(list);
        fab.setOnClickListener(this);
        if(conn.isShowAll()){
            fab.setImageResource(R.drawable.ic_visibility_black_24dp);
        }else{
            fab.setImageResource(R.drawable.ic_visibility_off_black_24dp);
        }
        return view;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        INDIProperty p = (INDIProperty)adapter.getChild(groupPosition,childPosition);
        if(!(p instanceof INDILightProperty || p.getPermission().equals(Constants.PropertyPermissions.RO))) {
            EditViewPropery editView = EditViewPropery.newInstance();
            editView.setProperty(p);
            editView.show(this.getFragmentManager(), "Property view");
        }else{
            Alert_dialog alert=Alert_dialog.newInstance(getResources().getString(R.string.alert_msg));
            alert.show(this.getFragmentManager(), "AlertDialog");
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        FloatingActionButton fab=(FloatingActionButton) v;
        if(conn.isShowAll()){
            conn.showAll(false);
            fab.setImageResource(R.drawable.ic_visibility_off_black_24dp);
        }else{
            conn.showAll(true);
            fab.setImageResource(R.drawable.ic_visibility_black_24dp);
        }
    }
}
