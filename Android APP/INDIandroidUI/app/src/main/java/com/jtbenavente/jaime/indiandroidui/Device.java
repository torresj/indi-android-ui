package com.jtbenavente.jaime.indiandroidui;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.indilib.i4j.client.INDIProperty;

/**
 * Created by Jaime on 27/8/15.
 */
public class Device {

    private String name;
    private LinkedHashMap<String,ArrayList<INDIProperty>> groups;

    public Device(String name){
    	this.name=name;
    	groups=new LinkedHashMap<String, ArrayList<INDIProperty>>();
    }

    public String getName(){
        return name;
    }

    public void addProperty(INDIProperty p){
    	if(p!=null){
    		String group_name=p.getGroup();
            if(groups.containsKey(group_name)){
               groups.get(group_name).add(p);
            }else{
                groups.put(group_name, new ArrayList<INDIProperty>());
                groups.get(group_name).add(p);
            }
    	}
    }

    public void removeProperty(INDIProperty p){
        if(p!=null){
        	String group_name=p.getGroup();
            ArrayList<INDIProperty> list=groups.get(group_name);
            if(list!=null && list.contains(p)){
                list.remove(p);
                if(list.size()==0){
                    groups.remove(group_name);
                }
            }
        }
    }

    public void updateProperty(INDIProperty p){
        if(p!=null){
            String group_name=p.getGroup();
            ArrayList<INDIProperty> list=groups.get(group_name);
            for(int i=0;i<list.size();i++){
                if(list.get(i).getName().equals(p.getName())){
                    list.set(i,p);
                }
            }
        }
    }

    public void clear(){
        groups.clear();
    }

    public ArrayList<INDIProperty> getGroupProperties(String group){
        return groups.get(group);
    }

    public ArrayList<String> getGroupsNames(){
    	ArrayList<String> names = new ArrayList<>();
        for (String name : groups.keySet()) {
            names.add(name);
        }

        return names;
    }

    public ArrayList<INDIProperty> getAllProperties(){
        ArrayList<INDIProperty> properties = new ArrayList<>();
        for (String name : groups.keySet()) {
            properties.addAll(groups.get(name));
        }

        return properties;
    }

}
