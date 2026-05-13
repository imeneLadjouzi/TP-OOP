package com.example.tpoop;

import java.util.ArrayList;

public abstract class Zone implements Suspendable{
    protected String code;
    protected String name;
    protected Status status;
    protected Prod production;
    //protected LimGeo to ask her the coordinates of the zone
    public ArrayList<Capteurs> capteurs;

    public Zone (String code, String name, Status status){
        this.name = name;
        this.code = code;
        this.status=status;
    }
    public void display(){
        System.out.println("Zone: "+name+" code: "+code+"Status: "+status);
    }
    public void suspendre() {
        this.status = Status.SUSPENDU;
    }
    public void reactiver() {
        this.status = Status.ACTIF;
    }

    public void setCode(String code) {
        this.code=code;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setStatus(Status status){
        this.status = status;
    }
    public String getCode(){
        return code;
    }
    public String getName(){
        return name;
    }

    public Status getStatus(){
        return status;
    }

    public void enregistrerProduction(Prod p){
        production = p;
    }

    //public abstract void suspendCapteurs();


}
