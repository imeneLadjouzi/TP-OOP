package com.example.tpoop;
import java.util.Scanner;
import java.util.ArrayList;

public class Ferme {
    ArrayList<Zone> zones;
    private int nbZones;
    //Alerte[] alertes;

    public void AjouterZone(Zone z){
       zones.add(z);
    }
    public void modifZone(Zone z) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Entrez la characteristique de la zone a modifier: ");
        String word = scanner.next();
        if (word.equals("code")) {
            System.out.println("Entrez le nouveau code: ");
            String new_code = scanner.next();
            z.setCode(new_code);
        } else if (word.equals("name")) {
            System.out.println("Entrez le nouveau nom: ");
            String new_name = scanner.next();
            z.setName(new_name);
        }

    }

    public void desactiverZone(Zone z){
        z.setStatus(Status.SUSPENDU);
        }


    public void affecterCulture(Zone zone, Culture culture) {
        if (!(zone instanceof ZoneCulture)) {
            throw new IllegalArgumentException("La zone " + zone.getName() + " n'est pas une zone de culture");
        }
        if (zone.getStatus().equals(Status.SUSPENDU.name())) {
            throw new IllegalStateException("Impossible d'affecter une culture à une zone suspendue");
        }

        ZoneCulture zoneCulture = (ZoneCulture) zone;
        zoneCulture.addCulture(culture);
    }
    public void affecterAnimal(Zone zone, Animal animal) {
        //if (!zones.contains(zone)) {
        //   throw new IllegalArgumentException("Cette zone n'appartient pas à l'exploitation");
        //}
        if (!(zone instanceof ZoneElevage)) {
            throw new IllegalArgumentException("La zone " + zone.getName() + " n'est pas une zone d'élevage");
        }
        if (zone.getStatus().equals(Status.SUSPENDU.name()) ) {
            throw new IllegalStateException("Impossible d'affecter un animal à une zone suspendue");
        }

        ZoneElevage zoneElevage = (ZoneElevage) zone;
        zoneElevage.addAnimal(animal);
    }


    public String getVueEnsembleZones() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Vue d'ensemble des zones ===\n");

        for (Zone z : zones) {
            if (z instanceof ZoneCulture zc) {
                zc.display();
            } else if (z instanceof ZoneElevage ze) {
                ze.display();

            } else if (z instanceof ZoneAqua za) {
                za.display();
            }

            sb.append("---\n");
        }

        return sb.toString();
    }

    public void enregistrerProduction(Prod p){
        for (Zone z : zones){
            z.enregistrerProduction(p);
        }
    }



}
