package com.example.tpoop;

import java.util.ArrayList;
import java.util.List;

public class ZoneCulture extends Zone {
    private Culture culture;
    static private int nbSeq=0;

    public ZoneCulture( String name, Status status, Culture culture) {
        super( name, status);
        this.culture=culture;
    }

    public String genererCode(){
        nbSeq++;
        return code = "ZC00"+String.valueOf(nbSeq);
    }

    public Culture getCultures() {
         return culture; }
    public void setCulture(Culture c){
        this.culture=c;
    }
    public void updateStadeCroiss(StadeCroissance s) {
        culture.updateStadeCroiss(s);
    }

    public void displayStadeCroiss() {
        culture.displayStadeCroiss();
    }

    public void displayCulture() {
        try{
            culture.display();
        }catch (NullPointerException e){
            System.out.println("Aucune culture assignée à cette zone.");
        }

    }

    public String genererRapport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Rapport Zone Culture: ").append(name).append(" ===\n");
        sb.append("Statut: ").append(status).append("\n");

        sb.append("  - Culture").append(culture.getNom())
                .append(" | Stade de croissance: ").append(culture.getStadeCroiss())
                .append(" | date de Plantation: ").append(culture.getDatePlantation())
                .append(" | Recolte prevue: ").append(culture.getDateRecolte()).append("\n");

        return sb.toString();
    }

    @Override
    public void display() {
        super.display();
        System.out.println("  Culture : ");
        displayCulture();
    }
}