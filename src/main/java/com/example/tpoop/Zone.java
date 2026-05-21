package com.example.tpoop;

import java.util.ArrayList;
import java.util.List;

public abstract class Zone implements Suspendable {
    protected String code;
    protected String name;
    protected Status status;
    protected List<Prod> productionRecord;
    protected List<Capteurs> capteurs;

    abstract public String genererCode();

    public Zone(String name, Status status) {
        this.name = name;
        this.code = this.genererCode();
        this.status = status;
        this.productionRecord = new ArrayList<>();
        this.capteurs = new ArrayList<>();
    }

    public void display() {
        System.out.println("Zone: " + name + " | Code: " + code + " | Statut: " + status);
    }

    public void suspendre() {
        this.status = Status.SUSPENDU;
        for (Capteurs c: capteurs){
            c.suspendre();
        }
    }

    public void reactiver() {
        this.status = Status.ACTIF;
        for (Capteurs c: capteurs){
            c.activer();
        }
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
    public List<Prod> getProductions() { return productionRecord; }
// =================== Production Record =================
    public void enregistrerProduction(Prod p) {
        if (status == Status.SUSPENDU) throw new IllegalStateException("La Zone est suspendue.");
        else
            productionRecord.add(p);

    }
    public Prod getLatest() {
        return productionRecord.isEmpty()
                ? null
                : productionRecord.getLast();
    }


    public double getTotal() {
        double total = 0.0;
        for (Prod p : productionRecord) {
            total += p.getVal();
        }
        return total;
    }
    public void displayProduction() {
        for (Prod p : productionRecord) {
            p.displayProduction();
        }
        System.out.println("  Production Totale: " + getTotal() + " | Nombre de releves: " + productionRecord.size());
        System.out.println("  Derniere Production: " + getLatest().getVal() + getLatest().getProd().getUnite() + " (" + getLatest().getProd() + ") enregistre le " + getLatest().getDate());
    }

}