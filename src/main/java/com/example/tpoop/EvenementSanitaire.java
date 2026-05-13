package com.example.tpoop;

import java.time.LocalDate;

public class EvenementSanitaire {
    public enum TypeEvenement { MALADIE, GUERISON, VACCINATION, PRISE_DE_POIDS, QUARANTAINE }

    private TypeEvenement type;
    private String description;
    private LocalDate date;
    private double valeur; // poids si pertinent

    public EvenementSanitaire(TypeEvenement type, String description) {
        this.type = type;
        this.description = description;
        this.date = LocalDate.now();
    }

    public EvenementSanitaire(TypeEvenement type, String description, double valeur) {
        this(type, description);
        this.valeur = valeur;
    }

    public TypeEvenement getType() { return type; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }
    public double getValeur() { return valeur; }

    @Override
    public String toString() {
        return date + " | " + type + " : " + description + (valeur > 0 ? " (" + valeur + ")" : "");
    }
}