package com.example.tpoop;

import java.util.ArrayList;
import java.util.List;

public class ZoneAqua extends Zone {
    private String espece;
    private int nbAnimaux;
    private ProgAlimentaire progAlim;
    private ProductionRecord productionRecord;
    private static int nbSeq=0;

    public ZoneAqua( String name, Status status) {
        super( name, status);
        this.nbAnimaux = 0;
        this.productionRecord = new ProductionRecord(TypeProd.POIDS_RECOLTE);
    }

    public String genererCode(){
        nbSeq++;
        return code = "ZA00"+String.valueOf(nbSeq);

    }

    public void setNbAnimaux(int nb) { this.nbAnimaux = nb; }
    public void setProgAlim(ProgAlimentaire prog) { this.progAlim = prog; }
    public String getEspece() { return espece; }
    public int getNbAnimaux() { return nbAnimaux; }
    public ProgAlimentaire getProgAlim() { return progAlim; }
    public ProductionRecord getProductionRecord() { return productionRecord; }

    public void enregistrerProduction(double val) {
        productionRecord.record(val);
    }

    @Override
    public void display() {
        super.display();
        System.out.println("  Espece aquacole : " + espece + " | Nb individus : " + nbAnimaux);
        if (progAlim != null) progAlim.display();
        productionRecord.display();
    }
}