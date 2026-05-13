package com.example.tpoop;

public class ProgAlimentaire {
    private String typeAliment;
    private double quantiteParRepas;
    private int repasParJour;

    public ProgAlimentaire(String typeAliment, double quantiteParRepas, int repasParJour) {
        this.typeAliment = typeAliment;
        this.quantiteParRepas = quantiteParRepas;
        this.repasParJour = repasParJour;
    }

    public double getQuantiteJournaliere() {
        return quantiteParRepas * repasParJour;
    }

    public String getTypeAliment() { return typeAliment; }
    public double getQuantiteParRepas() { return quantiteParRepas; }
    public int getRepasParJour() { return repasParJour; }

    public void display() {
        System.out.println("  Programme alimentaire : " + typeAliment
                + " | " + quantiteParRepas + " kg/repas x " + repasParJour
                + " repas/jour = " + getQuantiteJournaliere() + " kg/jour");
    }
}