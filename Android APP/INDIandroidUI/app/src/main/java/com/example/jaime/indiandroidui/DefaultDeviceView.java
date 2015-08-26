package com.example.jaime.indiandroidui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import laazotea.indi.Constants;
import laazotea.indi.client.INDILightProperty;
import laazotea.indi.client.INDIProperty;

/**
 * Created by Jaime on 26/8/15.
 */
public class DefaultDeviceView extends Fragment implements AdapterView.OnItemClickListener{

    private ListView list;
    static PropertyArrayAdapter adapter;
    private String title;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.default_device_view, container, false);

        list=(ListView)view.findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        INDIProperty p = (INDIProperty)list.getAdapter().getItem(position);
        if(!(p instanceof INDILightProperty || p.getPermission().equals(Constants.PropertyPermissions.RO))) {
            EditViewPropery editView = EditViewPropery.newInstance();
            editView.setProperty(p);
            editView.show(this.getFragmentManager(), "Property view");
        }else{
            Alert_dialog alert=Alert_dialog.newInstance(getResources().getString(R.string.alert_msg));
            alert.show(this.getFragmentManager(), "AlertDialog");
        }
    }
}
