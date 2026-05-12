package com.example.tpoop;

import java.util.Map;
abstract public class Capteurs {
    private String code;
    private Object location; //zone
    private Status status;
    private Object[] historique; //releve
    private int[] plage_seuil;
    public Capteurs(String code, Object location,  Status status) {
        this.status = status;
        this.code = code;
        this.location = location;
        status = Status.SUSPENDU;
        this.historique = new Object[]{};
        this.plage_seuil = new int[]{0, 0};
    }
    abstract public Map <String, Object> send_values();
    public void suspendre() {
        status=Status.SUSPENDU;
    }
    public void activer() {
        status=Status.ACTIF;
    }
    public void defaulter() {
        status=Status.DEFAILLANT;
    }
}
