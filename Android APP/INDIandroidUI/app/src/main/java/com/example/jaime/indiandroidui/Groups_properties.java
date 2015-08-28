package com.example.jaime.indiandroidui;

import java.util.ArrayList;
import java.util.List;

import laazotea.indi.client.INDIProperty;

/**
 * Created by Jaime on 26/8/15.
 */
public class Groups_properties {
    public String string;
    public List<INDIProperty> properties = new ArrayList<INDIProperty>();
    public Groups_properties(String string) {
        this.string = string;
    }
}
