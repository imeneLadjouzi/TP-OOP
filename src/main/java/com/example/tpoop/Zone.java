package com.example.tpoop;

abstract public class Zone implements Suspendable{
    protected String code;
    protected String name;
    protected String status;
    //protected LimGeo to ask her the coordinates of the zone
    public Zone (String code, String name, String status){
        this.name = name;
        this.code = code;
        this.status=status;
    }
    public abstract void display();

}
