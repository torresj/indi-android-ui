package com.example.jaime.indiandroidui;

public class ViewProperty {

    private String name;
    private int idle;
    private int permission;
    private int visibility;

    public ViewProperty(String name,int idle, int permission, int visibility){
        this.name=name;
        this.idle=idle;
        this.permission=permission;
        this.visibility=visibility;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setIdle(int idle){
        this.idle = idle;
    }

    public void setPermission(int permission){
        this.permission = permission;
    }

    public void setVisibility(int visibility){
        this.visibility = visibility;
    }


    public String getName(){return name;}
    public int getIdle(){return idle;}
    public int getPermission(){return permission;}
    public int getVisibility(){return visibility;}

}