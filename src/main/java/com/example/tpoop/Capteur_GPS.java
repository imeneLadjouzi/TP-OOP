package com.example.tpoop;

import java.util.HashMap;
import java.util.Map;

import static com.example.tpoop.Main.lireDouble;

public class Capteur_GPS extends Capteurs{
    private Animal animal;
    private PositionGeographique position;
    private PlageSeuils longitude= new PlageSeuils(0,0);
    private PlageSeuils latitude=new PlageSeuils(0,0);

    public Capteur_GPS(String code, Zone location, Status status, PositionGeographique position, Animal animal) {
        super(code, location, status, TypeCapteur.GPS);
        this.animal = animal;
        this.position = position;

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