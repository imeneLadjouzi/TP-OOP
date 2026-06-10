package com.example.tpoop;

import java.util.HashMap;
import java.util.Map;

public class Cap_env extends Capteur_num {
    private double temp;
    private double humidity;
    private double pluvi;
    private static int nbSeq=0;
    Seuils seuils=new Seuils(8,32,40,80,500,1200);

    class Seuils{
        PlageSeuils temp;
        PlageSeuils humidity;
        PlageSeuils pluvi;

        Seuils(double tempMin, double tempMax, double humidityMin, double humidityMax, double pluviMin, double pluviMax){
            temp=new PlageSeuils(tempMin,tempMax);
            humidity=new PlageSeuils(humidityMin,humidityMax);
            pluvi=new PlageSeuils(pluviMin,pluviMax);
        }
    }

    Niveau_gravite evaluertemp(){ return seuils.temp.evaluer(temp);}
    Niveau_gravite evaluerhum(){ return seuils.humidity.evaluer(humidity);}
    Niveau_gravite evaluerplu(){ return seuils.pluvi.evaluer(pluvi);}

    public String display_seuils() {
        StringBuilder sb = new StringBuilder();
        sb.append("===========================================================\n");
        sb.append(String.format("SEUILS DU CAPTEUR ENVIRONNEMENTAL - %s\n", this.getCode()));
        sb.append("===========================================================\n\n");

        sb.append("TEMPERATURE\n");
        sb.append(String.format("   * Seuil bas      : %.1f C\n", seuils.temp.getMin()));
        sb.append(String.format("   * Seuil haut     : %.1f C\n", seuils.temp.getMax()));
        sb.append("\n");

        sb.append("HUMIDITE\n");
        sb.append(String.format("   * Seuil bas      : %.1f %%\n", seuils.humidity.getMin()));
        sb.append(String.format("   * Seuil haut     : %.1f %%\n", seuils.humidity.getMax()));
        sb.append("\n");

        sb.append("PLUVIOMETRIE\n");
        sb.append(String.format("   * Seuil bas      : %.1f mm\n", seuils.pluvi.getMin()));
        sb.append(String.format("   * Seuil haut     : %.1f mm\n", seuils.pluvi.getMax()));
        sb.append("\n");

        return sb.toString();
    }

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

    public Cap_env(Zone location, Status status) {
        super("CapEnv0"+nbSeq, location, status,TypeCapteur.ENV);
        nbSeq++;
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