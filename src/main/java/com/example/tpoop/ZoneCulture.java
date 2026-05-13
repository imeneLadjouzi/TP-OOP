package com.example.tpoop;

import java.util.ArrayList;
import java.util.List;

public class ZoneCulture extends Zone {
    private List<Culture> cultures;
    static private int nbSeq=0;

    public ZoneCulture( String name, Status status) {
        super( name, status);
        this.cultures = new ArrayList<>();
    }

    public String genererCode(){
        nbSeq++;
        return code = "ZC00"+String.valueOf(nbSeq);

    }

    public void addCulture(Culture c) {
        cultures.add(c);
    }

    public void removeCulture(String nom) {
        cultures.removeIf(c -> c.getNom().equalsIgnoreCase(nom));
    }

    public List<Culture> getCultures() { return cultures; }

    public void updateStadeCroiss(StadeCroissance s) {
        for (Culture c : cultures) {
            c.updateStadeCroiss(s);
        }
    }

    public void displayStadeCroiss() {
        for (Culture c : cultures) {
            c.displayStadeCroiss();
        }
    }

    public void displayCultures() {
        if (cultures.isEmpty()) {
            System.out.println("  Aucune culture dans cette zone.");
        } else {
            for (Culture c : cultures) {
                c.display();
                System.out.println();
            }
        }
    }

    public String genererRapport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Rapport Zone Culture: ").append(name).append(" ===\n");
        sb.append("Statut: ").append(status).append(" | Nb cultures: ").append(cultures.size()).append("\n");
        for (Culture c : cultures) {
            sb.append("  - ").append(c.getNom())
                    .append(" | Stade: ").append(c.getStadeCroiss())
                    .append(" | Plantation: ").append(c.getDatePlantation())
                    .append(" | Recolte prevue: ").append(c.getDateRecolte()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public void display() {
        super.display();
        System.out.println("  Nombre de cultures : " + cultures.size());
        displayCultures();
    }
}