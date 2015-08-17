package com.example.jaime.indiandroidui;

import java.util.Comparator;

/**
 * Created by Jaime on 12/8/15.
 */
public class UIPropertyManagerOrder implements Comparator<UIPropertyManager> {

    @Override
    public int compare(UIPropertyManager o1, UIPropertyManager o2) {
        if (o1.getPriority()<o2.getPriority())
            return -1;
        else if(o1.getPriority()==o2.getPriority())
            return 0;
        else
            return 1;
    }
}
