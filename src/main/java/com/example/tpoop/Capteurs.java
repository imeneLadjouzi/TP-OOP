package com.example.tpoop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Capteurs implements Suspendable {
    private String code;
    private Zone location;
    private Status status;
    private List<Releve> historique;
    TypeCapteur type;

    public Capteurs(String code, Zone location, Status status, TypeCapteur type) {
        this.code = code;
        this.location = location;
        this.status = status;
        this.historique = new ArrayList<>();
        this.type = type;
    }

    public abstract Map<String, Object> send_values();

    /**
     * Effectue un relevé, l'enregistre dans l'historique et génère une alerte si nécessaire.
     */
    abstract public Releve effectuerReleve();

    public void suspendre() {
        status = Status.SUSPENDU;
    }

    public void activer() {
        status = Status.ACTIF;
    }

    public void defaulter() {
        status = Status.DEFAILLANT;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    public void addReleve(Releve releve) {
        historique.add(releve);
    }
    public String getCode() { return code; }
    public Zone getLocation() { return location; }
    public Status getStatus() { return status; }
    public List<Releve> getHistorique() { return historique; }
    public TypeCapteur getType() { return type; }

    @Override
    public String toString() {
        return "Capteur[" + code + "] Type: " + getClass().getSimpleName()
                + " | Zone: " + (location != null ? location.getName() : "N/A")
                + " | Status: " + status;
    }
}