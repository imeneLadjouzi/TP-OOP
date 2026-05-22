package com.example.tpoop;

import java.util.HashMap;
import java.util.Map;

public class Cap_env extends Capteur_num {
    private double temp;
    private double humidity;
    private double pluvi;
    private Seuils seuils=new Seuils(8,32,40,80,500,1200);

    class Seuils{
        private PlageSeuils temp;
        private PlageSeuils humidity;
        private PlageSeuils pluvi;

        Seuils(double tempMin, double tempMax, double humidityMin, double humidityMax, double pluviMin, double pluviMax){
            temp=new PlageSeuils(tempMin,tempMax);
            humidity=new PlageSeuils(humidityMin,humidityMax);
            pluvi=new PlageSeuils(pluviMin,pluviMax);
        }
    }

    Niveau_gravite evaluertemp(){ return seuils.temp.evaluer(temp);}
    Niveau_gravite evaluerhum(){ return seuils.humidity.evaluer(humidity);}
    Niveau_gravite evaluerplu(){ return seuils.pluvi.evaluer(pluvi);}

    void configurer(double tempMin, double tempMax, double humidityMin, double humidityMax, double pluviMin, double pluviMax){
        seuils.temp=new PlageSeuils(tempMin,tempMax);
        seuils.humidity=new PlageSeuils(humidityMin,humidityMax);
        seuils.pluvi=new PlageSeuils(pluviMin,pluviMax);
    }
    void configurerTemp(double tempMin, double tempMax) {
        seuils.temp=new PlageSeuils(tempMin,tempMax);
    }
    void configurerOxygen(double humidityMin, double humidityMax) {
        seuils.humidity=new PlageSeuils(humidityMin,humidityMax);
    }
    void configurertemp(double pluviMin, double pluviMax) {
        seuils.pluvi=new PlageSeuils(pluviMin,pluviMax);
    }

    public Niveau_gravite evaluer(){
        if (seuils.temp.evaluer(temp)== Niveau_gravite.CRITIQUE || seuils.humidity.evaluer(humidity)== Niveau_gravite.CRITIQUE || seuils.pluvi.evaluer(pluvi)== Niveau_gravite.CRITIQUE){
            return Niveau_gravite.CRITIQUE;
        }
        else if (seuils.temp.evaluer(temp)== Niveau_gravite.AVERTISSEMENT || seuils.humidity.evaluer(humidity)== Niveau_gravite.AVERTISSEMENT || seuils.pluvi.evaluer(pluvi)== Niveau_gravite.AVERTISSEMENT){
            return Niveau_gravite.AVERTISSEMENT;
        }
        else return Niveau_gravite.INFO;
    }

    public Cap_env(String code, Zone location, Status status, double temp, double humidity, double pluvi) {
        super(code, location, status,TypeCapteur.ENV);
        this.temp = temp;
        this.humidity = humidity;
        this.pluvi = pluvi;
    }

    public void setTemp(double temp) { this.temp = temp; }
    public void setHumidity(double humidity) { this.humidity = humidity; }
    public void setPluvi(double pluvi) { this.pluvi = pluvi; }

    @Override
    public Map<String, Object> send_values() {
        Map<String, Object> map = new HashMap<>();
        map.put("temperature", temp);
        map.put("humidite", humidity);
        map.put("pluviometrie", pluvi);
        return map;
    }

    public Releve effectuerReleve() {
        Map<String, Object> valeurs = send_values();
        Niveau_gravite niveau = evaluer();
        String msg= "";
        Releve r = new Releve(this, valeurs, niveau);

        if (niveau == Niveau_gravite.CRITIQUE) {
            if ( evaluertemp()==niveau) msg=msg+"Température ";
            if ( evaluerhum()==niveau) msg=msg+"Humidité ";
            if ( evaluerplu()==niveau) msg=msg+"Pluviométrie ";
            Alerte a =new Alerte(r,niveau,"Valeur de "+msg+"est hors seuils!"  ,getLocation());
            getLocation().ferme.ajouterAlerte(a);
        }
        if (niveau == Niveau_gravite.AVERTISSEMENT) {
            if ( evaluertemp()==niveau) msg=msg+"Température ";
            if ( evaluerhum()==niveau) msg=msg+"Humidité ";
            if ( evaluerplu()==niveau) msg=msg+"Pluviométrie ";
            Alerte a =new Alerte(r,niveau,"Valeur de "+msg+"est proche des seuils!"  ,getLocation());
            getLocation().ferme.ajouterAlerte(a);
        }
        super.addReleve(r);
        return r;
    }
}