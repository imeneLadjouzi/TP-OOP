package com.example.tpoop;

import java.util.ArrayList;
import java.util.List;

public abstract class Zone implements Suspendable {
    protected String code;
    protected String name;
    protected Status status;
    protected List<Prod> productions;
    protected List<Capteurs> capteurs;

    abstract public String genererCode();

    public Zone(String name, Status status) {
        this.name = name;
        this.code = this.genererCode();
        this.status = status;
        this.productions = new ArrayList<>();
        this.capteurs = new ArrayList<>();
    }

    public void display() {
        System.out.println("Zone: " + name + " | Code: " + code + " | Statut: " + status);
    }

    public void suspendre() {
        this.status = Status.SUSPENDU;
    }

    public void reactiver() {
        this.status = Status.ACTIF;
    }

    public void ajouterCapteur(Capteurs c) {
        capteurs.add(c);
    }

    public void supprimerCapteur(String code) {
        capteurs.removeIf(c -> c.getCode().equals(code));
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public String getName() { return name; }
    public Status getStatus() { return status; }
    public List<Capteurs> getCapteurs() { return capteurs; }
    public List<Prod> getProductions() { return productions; }

    public void enregistrerProduction(Prod p) {
        productions.add(p);
    }
}