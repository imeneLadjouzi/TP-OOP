package com.example.tpoop;

public class ZoneElevage extends Zone {
    Animal[] animals;
    //Capteur[] capteurs;

    public  ZoneElevage( String code,  String name, String status){
        super(code,name,status);
    }

    @Override
    public void display() {
        System.out.println("Zone d'élevage: "+name+" code: "+code+"Status"+status);
    }
    @Override
    public void setStatus(String status){
        this.status = status;
    }
}
//addANIMAL W LOULHRA aDDcULTURE