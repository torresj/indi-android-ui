package com.jtbenavente.jaime.indiandroidui;

import java.util.TreeSet;

/**
 * Created by Jaime on 24/8/15.
 */
public class Config {
    static TreeSet<UIPropertyManager> uiProperties;

    static void init(){
        uiProperties= new TreeSet<>(new UIPropertyManagerOrder());
    }

    static void addUiPropertyManager(UIPropertyManager ui){
        uiProperties.add(ui);
    }

    static TreeSet<UIPropertyManager> getUIProperties(){
        return uiProperties;
    }
}
