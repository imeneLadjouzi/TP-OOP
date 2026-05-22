package com.example.tpoop;

import java.util.HashMap;
import java.util.Map;

import static com.example.tpoop.Main.lireDouble;

public class Cap_sol extends Capteur_num{
    private double azote;
    private double humidity;
    private double ph;
    private ExigPedologiques seuils=new ExigPedologiques(5.8, 6.8,15,22,25,50);



    void configurerTemp(double phMin, double phMax) {
        seuils.configurerTemp(phMin,phMax);
    }
    void configurerHum(double humiditeMin, double humiditeMax) {
        seuils.configurerOxygen(humiditeMin,humiditeMax);
    }
    void configurerPh(double azoteMin, double azoteMax) {
        seuils.configurerPh(azoteMin,azoteMax);
    }

    public Niveau_gravite evaluer(){

        return seuils.evaluer(ph,humidity,azote);
    }

    public Cap_sol(String code, Zone location, Status status, double azote, double humidity, double ph) {
        super(code, location, status,TypeCapteur.SOL);
        this.azote = azote;
        this.humidity = humidity;
        this.ph = ph;
    }

    public void setAzote(double azote) { this.azote = azote; }
    public void setHumidity(double humidity) { this.humidity = humidity; }
    public void setPh(double ph) { this.ph = ph; }

    @Override
    public Map<String, Object> send_values() {
        Map<String, Object> map = new HashMap<>();
        map.put("azote", azote);
        map.put("humidite", humidity);
        map.put("ph", ph);
        return map;
    }

    public Releve effectuerReleve() {
        Map<String, Object> valeurs = send_values();
        Niveau_gravite niveau = evaluer();
        String msg= "";
        Releve r = new Releve(this, valeurs, niveau);

        if (niveau == Niveau_gravite.CRITIQUE) {
            if ( seuils.evaluerPh(ph)==niveau) msg=msg+"Ph ";
            if ( seuils.evaluerhum(humidity)==niveau) msg=msg+"Humidité ";
            if ( seuils.evalueraz(azote)==niveau) msg=msg+"Azote ";
            Alerte a =new Alerte(r,niveau,"Valeur de "+msg+"est hors seuils!"  ,getLocation());
            getLocation().ferme.ajouterAlerte(a);
        }
        else if (niveau == Niveau_gravite.AVERTISSEMENT) {
            if ( seuils.evaluerPh(ph)==niveau) msg=msg+"Ph ";
            if ( seuils.evaluerhum(humidity)==niveau) msg=msg+"Humidité ";
            if ( seuils.evalueraz(azote)==niveau) msg=msg+"Azote ";
            Alerte a =new Alerte(r,niveau,"Valeur de "+msg+"est proche des seuils",getLocation());
            getLocation().ferme.ajouterAlerte(a);
        }

        super.addReleve(r);
        return r;
    }
}