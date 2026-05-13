package com.example.tpoop;

import java.util.ArrayList;
import java.util.List;

public class ZoneElevage extends Zone {
    private List<Animal> animaux;
    private ProgAlimentaire progAlim;
    private ProductionRecord productionRecord;
    static private int nbSeq=0;

    public ZoneElevage(String name, Status status, TypeProd typeProd) {
        super(name, status);
        this.animaux = new ArrayList<>();
        this.productionRecord = new ProductionRecord(typeProd);
    }

    public String genererCode(){
        nbSeq++;
        return code = "ZE00"+String.valueOf(nbSeq);

    }

    public void addAnimal(Animal a) {
        animaux.add(a);
    }

    public void removeAnimal(int id) {
        animaux.removeIf(a -> a.getID() == id);
    }

    public void setProgAlim(ProgAlimentaire prog) {
        this.progAlim = prog;
    }

    public ProgAlimentaire getProgAlim() { return progAlim; }
    public List<Animal> getAnimaux() { return animaux; }
    public ProductionRecord getProductionRecord() { return productionRecord; }

    public void enregistrerProductionZone(double val) {
        productionRecord.record(val);
    }

    public int getNbAnimauxMalades() {
        return (int) animaux.stream().filter(a -> a.getEtat() != EtatSante.SAIN).count();
    }

    @Override
    public void display() {
        super.display();
        System.out.println("  Nombre d'animaux : " + animaux.size()
                + " (malades/quarantaine: " + getNbAnimauxMalades() + ")");
        if (progAlim != null) progAlim.display();
        productionRecord.display();
        for (Animal a : animaux) a.display();
    }
}