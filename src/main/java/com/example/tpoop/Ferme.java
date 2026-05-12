package com.example.tpoop;

public class Ferme {
    Zone[] zones;
    private int nbZones;
    //Alerte[] alertes;

    public void AjouterZone(Zone z){
        zones[nbZones]=z;
        nbZones++;
    }
    public void modifZone(Zone z){

    }

    public void affecterCulture(ZoneCulture z){

    }

    public void display(){
        for (Zone z: zones){
            z.display();
        }
    }



}
