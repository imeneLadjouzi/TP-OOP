package com.example.tpoop;

import java.util.HashMap;
import java.util.Map;

public class Cap_biometrique extends Capteur_num {
    private double temp_corporelle;
    private double activity_per_min;
    private static int nbSeq=1;
    Seuils seuils=new Seuils(38,41,0,260);

    class Seuils{
        PlageSeuils temp_corporelle;
        PlageSeuils activity_per_min;

        Seuils(double tempMin, double tempMax, double activityMin, double activityMax){
            temp_corporelle=new PlageSeuils(tempMin,tempMax);
            activity_per_min=new PlageSeuils(activityMin,activityMax);
        }

    }

    Niveau_gravite evaluertemp(){ return seuils.temp_corporelle.evaluer(temp_corporelle);}
    Niveau_gravite evalueract(){ return seuils.activity_per_min.evaluer(activity_per_min);}


    void configurer(double tempMin, double tempMax, double activityMin, double activityMax){
        seuils.temp_corporelle=new PlageSeuils(tempMin,tempMax);
        seuils.activity_per_min=new PlageSeuils(activityMin,activityMax);
    }
    void configurerTemp(double tempMin, double tempMax){
        seuils.temp_corporelle=new PlageSeuils(tempMin,tempMax);
    }
    void configurerAct(double actMin, double actMax){
        seuils.activity_per_min=new PlageSeuils(actMin,actMax);
    }

    public Niveau_gravite evaluer(){
        if (seuils.temp_corporelle.evaluer(temp_corporelle)== Niveau_gravite.CRITIQUE || seuils.activity_per_min.evaluer(activity_per_min)== Niveau_gravite.CRITIQUE ){
            return Niveau_gravite.CRITIQUE;
        }
        else if (seuils.temp_corporelle.evaluer(temp_corporelle)== Niveau_gravite.AVERTISSEMENT || seuils.activity_per_min.evaluer(activity_per_min)== Niveau_gravite.AVERTISSEMENT){
            return Niveau_gravite.AVERTISSEMENT;
        }
        else return Niveau_gravite.INFO;
    }

    public Cap_biometrique(Zone location, Status status) {
        super("CapBio0"+nbSeq, location, status,TypeCapteur.BIOMETRIQUE);
        nbSeq++;
    }

    public String display_seuils() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append(String.format("SEUILS DU CAPTEUR BIOMÉTRIQUE - %s\n", this.getCode()));
        sb.append("═══════════════════════════════════════════════════════════\n\n");

        sb.append("🌡TEMPÉRATURE CORPORELLE\n");
        sb.append(String.format("   • Seuil bas      : %.1f °C\n", seuils.temp_corporelle.getMin()));
        sb.append(String.format("   • Seuil haut     : %.1f °C\n", seuils.temp_corporelle.getMax()));

        sb.append("\n");

        sb.append("ACTIVITÉ PAR MINUTE\n");
        sb.append(String.format("   • Seuil bas      : %.1f pas/min\n", seuils.activity_per_min.getMin()));
        sb.append(String.format("   • Seuil haut     : %.1f pas/min\n", seuils.activity_per_min.getMax()));
        sb.append("\n");


        return sb.toString();
    }

    public void setTempCorporelle(double temp) { this.temp_corporelle = temp; }
    public void setActivityPerMin(double activity) { this.activity_per_min = activity; }

    @Override
    public Map<String, Object> send_values() {
        Map<String, Object> map = new HashMap<>();
        map.put("temperature_corporelle", temp_corporelle);
        map.put("activite_par_minute", activity_per_min);
        return map;
    }

    public Releve effectuerReleve() {
        Map<String, Object> valeurs = send_values();
        Niveau_gravite niveau = evaluer();
        String msg= "";
        Releve r = new Releve(this, valeurs, niveau);

        if (niveau == Niveau_gravite.CRITIQUE) {
            if ( evaluertemp()==niveau) msg=msg+"Température corporelle";
            if ( evalueract()==niveau) msg=msg+"Activité par minute ";
            Alerte a =new Alerte(r,niveau,"Valeur de "+msg+"est hors seuils!"  ,getLocation());
            getLocation().ferme.ajouterAlerte(a);
        }
        if (niveau == Niveau_gravite.AVERTISSEMENT) {
            if ( evaluertemp()==niveau) msg=msg+"Température ";
            if ( evalueract()==niveau) msg=msg+"Activité par minute ";
            Alerte a =new Alerte(r,niveau,"Valeur de "+msg+"est proche des seuils!"  ,getLocation());
            getLocation().ferme.ajouterAlerte(a);
        }


        super.addReleve(r);
        return r;
    }
}