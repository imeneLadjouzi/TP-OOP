package com.example.tpoop;
public class Animal {

    protected int ID;
    protected EspeceAnim espece;
    protected int age;
    protected int poids;
    protected EtatSante etat;

    public Animal(int ID, EspeceAnim espece, int age, int poids, EtatSante etat){
        this.ID=ID;
        this.espece=espece;
        this.age = age;
        this.poids=poids ;
        this.etat=etat;
    }

    public void enregistrerHealth(EtatSante etat){
        this.etat=etat;
    }
    public void enregistrerPoids(int poids){
        this.poids=poids;
    }



}
