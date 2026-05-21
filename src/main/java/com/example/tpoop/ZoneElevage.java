package com.example.tpoop;

import java.util.ArrayList;
import java.util.List;

public class ZoneElevage extends Zone {
    private List<Animal> animaux;
    private ProgAlimentaire progAlim;
    private TypeAnimal ta;
    private static int nbSeq = 0;

    public ZoneElevage(String name, Status status, TypeAnimal ta) {
        super(name, status);
        this.animaux = new ArrayList<>();
        this.ta=ta;
    }

    @Override
    public String genererCode() {
        nbSeq++;
        return "ZE00" + nbSeq;
    }

    public void addAnimal(Animal a) { animaux.add(a); }
    public void removeAnimal(int id) { animaux.removeIf(a -> a.getID() == id); }
    public void setProgAlim(ProgAlimentaire prog) { this.progAlim = prog; }
    public ProgAlimentaire getProgAlim() { return progAlim; }
    public List<Animal> getAnimaux() { return animaux; }
    public TypeAnimal getTypeAnimal() { return ta; }

    public int getNbAnimauxMalades() {
        return (int) animaux.stream()
                .filter(a -> a.getEtat() != EtatSante.SAIN)
                .count();
    }

    @Override
    public void display() {
        super.display();
        System.out.println(" | Nb animaux : " + animaux.size()
                + " (malades/quarantaine: " + getNbAnimauxMalades() + ")");
        if (progAlim != null) progAlim.display();
        displayProduction();
        System.out.println("Les animaux dans la zone: ");
        for (Animal a : animaux) a.display();
    }
}