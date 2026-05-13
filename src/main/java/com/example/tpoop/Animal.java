package com.example.tpoop;

import java.util.ArrayList;
import java.util.List;

public class Animal {
    private static int compteur = 0;
    protected int ID;
    protected EspeceAnim espece;
    protected int age;
    protected double poids;
    protected EtatSante etat;
    private List<EvenementSanitaire> historiqueSanitaire;

    public Animal(EspeceAnim espece, int age, double poids, EtatSante etat) {
        this.ID = ++compteur;
        this.espece = espece;
        this.age = age;
        this.poids = poids;
        this.etat = etat;
        this.historiqueSanitaire = new ArrayList<>();
    }

    public void enregistrerEvenementSanitaire(EvenementSanitaire evt) {
        historiqueSanitaire.add(evt);
        if (evt.getType() == EvenementSanitaire.TypeEvenement.MALADIE) {
            this.etat = EtatSante.MALADE;
        } else if (evt.getType() == EvenementSanitaire.TypeEvenement.QUARANTAINE) {
            this.etat = EtatSante.EN_QUARANTAINE;
        } else if (evt.getType() == EvenementSanitaire.TypeEvenement.GUERISON) {
            this.etat = EtatSante.SAIN;
        } else if (evt.getType() == EvenementSanitaire.TypeEvenement.PRISE_DE_POIDS) {
            this.poids = evt.getValeur();
        }
    }

    public void enregistrerHealth(EtatSante etat) {
        this.etat = etat;
    }

    public void enregistrerPoids(double poids) {
        this.poids = poids;
    }

    public int getID() { return ID; }
    public EspeceAnim getEspece() { return espece; }
    public int getAge() { return age; }
    public double getPoids() { return poids; }
    public EtatSante getEtat() { return etat; }
    public List<EvenementSanitaire> getHistoriqueSanitaire() { return historiqueSanitaire; }

    public void display() {
        System.out.println("  Animal #" + ID + " | Espece: " + espece
                + " | Age: " + age + " ans | Poids: " + poids + " kg | Sante: " + etat);
    }

    public void displayHistoriqueSanitaire() {
        System.out.println("  Historique sanitaire de l'animal #" + ID + " :");
        if (historiqueSanitaire.isEmpty()) {
            System.out.println("    Aucun evenement enregistre.");
        } else {
            for (EvenementSanitaire e : historiqueSanitaire) {
                System.out.println("    - " + e);
            }
        }
    }
}