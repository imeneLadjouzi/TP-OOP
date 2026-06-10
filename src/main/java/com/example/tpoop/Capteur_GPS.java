package com.example.tpoop;

import java.util.HashMap;
import java.util.Map;


public class Capteur_GPS extends Capteurs{
    private Animal animal;
    private PositionGeographique position;
    private static int nbSeq=1;
    private PlageSeuils longitude= new PlageSeuils(0,0);
    private PlageSeuils latitude=new PlageSeuils(0,0);

    public Capteur_GPS( Zone location, Status status, Animal animal) {
        super("CapGPS0"+nbSeq, location, status, TypeCapteur.GPS);
        this.animal = animal;
        nbSeq++;

    }

    public String display_seuils() {
        StringBuilder sb = new StringBuilder();
        sb.append("===========================================================\n");
        sb.append(String.format("SEUILS DU CAPTEUR GPS - %s\n", this.getCode()));
        sb.append("===========================================================\n\n");

        sb.append("ZONE GEOGRAPHIQUE AUTORISEE\n");
        sb.append(String.format("   * Longitude min  : %.4f deg\n", longitude.getMin()));
        sb.append(String.format("   * Longitude max  : %.4f deg\n", longitude.getMax()));
        sb.append(String.format("   * Latitude min   : %.4f deg\n", latitude.getMin()));
        sb.append(String.format("   * Latitude max   : %.4f deg\n", latitude.getMax()));
        sb.append("\n");

        return sb.toString();
    }

    public void configurer(double minlong, double maxlong, double minlat, double maxlat){
        longitude=new PlageSeuils(minlong,maxlong);
        latitude=new PlageSeuils(minlat,maxlat);

    }

    public void updatePosition(PositionGeographique nouvellePosition) {

        this.position = nouvellePosition;
    }

    public Niveau_gravite evaluerPosition(){
        if (position != null) {
            if (longitude.evaluer(position.getLongitude())==Niveau_gravite.CRITIQUE || latitude.evaluer(position.getLatitude())==Niveau_gravite.CRITIQUE) {
                return Niveau_gravite.CRITIQUE;
            }
            else if (longitude.evaluer(position.getLongitude())==Niveau_gravite.AVERTISSEMENT || latitude.evaluer(position.getLatitude())==Niveau_gravite.AVERTISSEMENT) {
                return Niveau_gravite.AVERTISSEMENT;
            }
        }
        return Niveau_gravite.INFO;
    }

    @Override
    public Map<String, Object> send_values() {
        Map<String, Object> map = new HashMap<>();
        map.put("latitude", position.getLatitude());
        map.put("longitude", position.getLongitude());
        return map;
    }

    public PositionGeographique getPosition() {

        return position;
    }

    public Releve effectuerReleve() {
        Map<String, Object> valeurs = send_values();
        Niveau_gravite niveau = evaluerPosition();
        Releve r = new Releve(this, valeurs, niveau);
        if (niveau == Niveau_gravite.CRITIQUE) {
            Alerte a=new Alerte(r,niveau,"Animal "+animal.getID()+" quitte sa zone!",getLocation());
            getLocation().ferme.ajouterAlerte(a);
        }
        else if (niveau == Niveau_gravite.AVERTISSEMENT) {
            Alerte a=new Alerte(r,niveau, "Animal "+animal.getID()+" se rapproche des limites de sa zone!",getLocation());
            getLocation().ferme.ajouterAlerte(a);
        }
        super.addReleve(r);
        return r;
    }
}