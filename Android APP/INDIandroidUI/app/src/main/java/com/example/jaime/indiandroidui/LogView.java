package com.example.jaime.indiandroidui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Jaime on 27/8/15.
 */
public class LogView extends Fragment {

    static String text;
    private TextView text_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.log_view, container, false);
        text_view=(TextView) view.findViewById(R.id.text);
        text_view.setText(text);
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        text_view.setText(text);
    }

}
