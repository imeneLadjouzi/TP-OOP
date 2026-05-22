package com.example.tpoop;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

public class Cap_aqua extends Capteur_num {
    private double temp;
    private double oxygen;
    private double ph;
    private seuils seuils= new seuils(20,30,5.5,6.5,6,143);

    class seuils{
        private PlageSeuils temp;
        private PlageSeuils oxygen;
        private PlageSeuils ph;

        seuils(double tempMin, double tempMax, double oxygenMin, double oxygenMax, double phMin, double phMax){
            temp=new PlageSeuils(tempMin,tempMax);
            oxygen=new PlageSeuils(oxygenMin,oxygenMax);
            ph=new PlageSeuils(phMin,phMax);
        }
    }

    Niveau_gravite evaluertemp(){ return seuils.temp.evaluer(temp);}
    Niveau_gravite evalueroxy(){ return seuils.oxygen.evaluer(oxygen);}
    Niveau_gravite evaluerph(){ return seuils.ph.evaluer(ph);}


    public Cap_aqua(String code, Zone location, Status status, double temp, double oxygen, double ph) {
        super(code, location, status,TypeCapteur.AQUA);
        this.temp = temp;
        this.oxygen = oxygen;
        this.ph = ph;
    }

    void configurer(double tempMin, double tempMax, double oxygenMin, double oxygenMax, double phMin, double phMax){
        seuils.temp=new PlageSeuils(tempMin,tempMax);
        seuils.oxygen=new PlageSeuils(oxygenMin,oxygenMax);
        seuils.ph=new PlageSeuils(phMin,phMax);
    }
    void configurerTemp(double tempMin, double tempMax) {
        seuils.temp=new PlageSeuils(tempMin,tempMax);
    }
    void configurerOxygen(double oxygenMin, double oxygenMax) {
        seuils.oxygen=new PlageSeuils(oxygenMin,oxygenMax);
    }
    void configurerPh(double phMin, double phMax) {
        seuils.ph=new PlageSeuils(phMin,phMax);
    }

    public Niveau_gravite evaluer(){
        if (seuils.temp.evaluer(temp)== Niveau_gravite.CRITIQUE || seuils.oxygen.evaluer(oxygen)== Niveau_gravite.CRITIQUE || seuils.ph.evaluer(ph)== Niveau_gravite.CRITIQUE){
            return Niveau_gravite.CRITIQUE;
        }
        else if (seuils.temp.evaluer(temp)== Niveau_gravite.AVERTISSEMENT || seuils.oxygen.evaluer(oxygen)== Niveau_gravite.AVERTISSEMENT || seuils.ph.evaluer(ph)== Niveau_gravite.AVERTISSEMENT){
            return Niveau_gravite.AVERTISSEMENT;
        }
        else return Niveau_gravite.INFO;
    }

    public void setTemp(double temp) { this.temp = temp; }
    public void setOxygen(double oxygen) { this.oxygen = oxygen; }
    public void setPh(double ph) { this.ph = ph; }

    @Override
    public Map<String, Object> send_values() {
        Map<String, Object> map = new HashMap<>();
        map.put("temperature", temp);
        map.put("oxygene", oxygen);
        map.put("ph", ph);
        return map;
    }

    public Releve effectuerReleve() {
        Map<String, Object> valeurs = send_values();
        Niveau_gravite niveau = evaluer();
        String msg= "";
        Releve r = new Releve(this, valeurs, niveau);

        if (niveau == Niveau_gravite.CRITIQUE) {
            if ( evaluertemp()==niveau) msg=msg+"Température ";
            if ( evaluerph()==niveau) msg=msg+"Ph ";
            if ( evalueroxy()==niveau) msg=msg+"Oxygène ";
            Alerte a =new Alerte(r,niveau,"Valeur de "+msg+"est hors seuils!"  ,getLocation());
            getLocation().ferme.ajouterAlerte(a);
        }
        if (niveau == Niveau_gravite.AVERTISSEMENT) {
            if ( evaluertemp()==niveau) msg=msg+"Température ";
            if ( evaluerph()==niveau) msg=msg+"Ph ";
            if ( evalueroxy()==niveau) msg=msg+"Oxygène ";
            Alerte a =new Alerte(r,niveau,"Valeur de "+msg+"est proche des seuils!"  ,getLocation());
            getLocation().ferme.ajouterAlerte(a);
        }
        super.addReleve(r);
        return r;
    }
}