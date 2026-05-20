package com.example.tpoop;

public class ZoneAqua extends Zone {
    private String espece;
    private int nbAnimaux;
    private ProgAlimentaire progAlim;
    private static int nbSeq = 0;

    public ZoneAqua(String name, Status status, String espece) {
        super(name, status);
        this.espece = espece;
        this.nbAnimaux = 0;
    }

    @Override
    public String genererCode() {
        nbSeq++;
        return "ZA00" + nbSeq;
    }


    public void enregistrerProduction(double val) {
        super.enregistrerProduction(new Prod(val, TypeProd.POIDS_RECOLTE));
    }

    public void setNbAnimaux(int nb) { this.nbAnimaux = nb; }
    public void setProgAlim(ProgAlimentaire prog) { this.progAlim = prog; }
    public String getEspece() { return espece; }
    public int getNbAnimaux() { return nbAnimaux; }
    public ProgAlimentaire getProgAlim() { return progAlim; }

    @Override
    public void display() {
        super.display();
        System.out.println("  Espece aquacole : " + espece + " | Nb individus : " + nbAnimaux);
        if (progAlim != null) progAlim.display();
        displayProduction();
    }
}