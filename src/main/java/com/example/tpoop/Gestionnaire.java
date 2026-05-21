package com.example.tpoop;

//import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Gestionnaire {
    Ferme ferme;

    public Gestionnaire(Ferme ferme) {
        this.ferme = ferme;
    }

    // ---------------------------------Zones

    public void ajouterZone(String nom, TypeZone type) {
        Zone z;
        switch (type) {
            case AQUA -> {
                Scanner sc = new Scanner(System.in);
                System.out.print("Espece habitant la zone: "); String espece = sc.nextLine();
                z = new ZoneAqua(nom, Status.ACTIF,espece);
                ferme.ajouterZone(z);
            }
            case CULTURE -> {

                z = new ZoneCulture(nom, Status.ACTIF,null);
                ferme.ajouterZone(z);
            }
            case ELEVAGE -> {
                Scanner sc = new Scanner(System.in);
                try {
                    System.out.print("Type d'animaux habitant la zone (RUMINANT-VOLAILLE): ");
                    String s = sc.nextLine().trim().toUpperCase();
                    TypeAnimal ta = TypeAnimal.valueOf(s);
                    z = new ZoneElevage(nom, Status.ACTIF, ta);
                    ferme.ajouterZone(z);
                } catch (IllegalArgumentException e) {
                    System.out.println("Type d'animal invalide.");
                }
            }
            default -> {
                System.out.println("Erreur");
                return;
            }
        }

        System.out.println("Zone '" + nom + "' ajoutee.");
    }


    //public void modifier;
    public void modifierNomZone(Zone z, String nom) {
        z.setName(nom);
    }

    public void modifierCodeZone(Zone z, String code) {
        z.setName(code);
    }

    public void desactiverZone(Zone z) {
        z.suspendre();
    }

    public void affecterCulture(ZoneCulture zone, Culture culture) {
        if (zone.getStatus() == Status.SUSPENDU) {
            throw new IllegalStateException("Impossible d'affecter une culture a une zone suspendue.");
        }
        zone.setCulture(culture);
        System.out.println("Culture '" + culture.getNom() + "' affectee a la zone '" + zone.getName() + "'.");
    }

    public void affecterAnimal(ZoneElevage zone, Animal animal) {
        try{
            if (zone.getStatus() == Status.SUSPENDU) {
                throw new IllegalStateException("Impossible d'affecter un animal a une zone suspendue.");
            }
            if (animal.getEspece().getType() != zone.getTypeAnimal()) {
                throw new TypeAnimalIncompatibleException("Type d'animal incompatible avec la zone. Zone attend: " + zone.getTypeAnimal() + ", mais l'animal est de type: " + animal.getEspece().getType());
            }
            zone.addAnimal(animal);
            System.out.println("Animal " + animal.getEspece().getName() + " affecte a la zone '" + zone.getName() + "'.");
        }catch (TypeAnimalIncompatibleException e){
            System.out.println(e.getMessage());}

    }

    public String consulterZones() {
        return ferme.getVueEnsembleZones();
    }

    public void reactiverZone(Zone z) {
        z.reactiver();
        System.out.println("Zone '" + z.getName() + "' reactivee.");
    }

    public void enregistrerProduction(Zone zone, Prod p) {
        zone.enregistrerProduction(p);
        System.out.println("Production enregistree : " + p + " pour la zone '" + zone.getName() + "'.");
    }

    // -----------------------Cultures


    public void displayStadeCroissance(ZoneCulture z) {
        z.displayStadeCroiss();
    }
    public void mettreAJourStadeCroissance(ZoneCulture zone, StadeCroissance stade) {
        zone.updateStadeCroiss(stade);
        System.out.println("Stade de croissance mis a jour : " + stade);
    }


    // -----------------------------Animaux----------------


    public void consignerEvenementsSanitaires(ZoneElevage zone) {
        for (Animal an : zone.getAnimaux()) {
            an.displayHistoriqueSanitaire();
        }
    }

    public void definirProgAlim(Zone zone, ProgAlimentaire progAlimentaire) {
        if (zone instanceof ZoneElevage) {
            ((ZoneElevage)zone).setProgAlim(progAlimentaire);
        }
        if (zone instanceof ZoneAqua){
            ((ZoneAqua) zone).setProgAlim(progAlimentaire);
        }
    }

    public void afficherProgAlim(ZoneElevage zone) {
        zone.getProgAlim().display();
    }
    public void afficherProgAlim(ZoneAqua zone) {
        zone.getProgAlim().display();
    }
    public void supprimerZone(String code) {
        ferme.getZones().removeIf(z -> z.getCode().equals(code));
        System.out.println("Zone '" + code + "' supprimee.");
    }




    // --------------------------- Capteurs -------------------

    public void ajouterCapteur(Zone zone, Capteurs capteur) {
        if (zone.getStatus() == Status.SUSPENDU) {
            throw new IllegalStateException("Impossible d'ajouter un capteur a une zone suspendue.");
        }
        zone.ajouterCapteur(capteur);
        ferme.ajouterCapteur(capteur);
    }

    public String dashboardCapteurs() {

        return ferme.tableauDeBordCapteurs();
    }

    public void historiqueCapteurs(Capteurs capteurs) {
        for (Releve rel : capteurs.getHistorique()) {
            System.out.println(rel.toString());
        }

    }


    public void changerStatusCapteur(Capteurs capteurs, Status status) {

        capteurs.setStatus(status);
    }

    public void afficherAlertesActives() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== ALERTES ACTIVES =====\n");
        ferme.getAlertes().stream()
                .filter(Alerte::isActive)
                .sorted((a, b) -> b.getGravite().compareTo(a.getGravite()))
                .forEach(a -> sb.append(a).append("\n"));
        if (ferme.getAlertes().stream().noneMatch(Alerte::isActive)) {
            sb.append("Aucune alerte active.\n");
        }
        System.out.println(sb.toString());
    }

    public void acquitterAlerte(Alerte alerte) {
        alerte.acquitter();
    }

    public void supprimerAlerte(Alerte alerte) {
        alerte.supprimer();

    }

    public String historiqueAlertes(
            Zone z,
            TypeCapteur type,
            Niveau_gravite niveau,
            LocalDate debut,
            LocalDate fin
    )

    {
        StringBuilder sb = new StringBuilder();
        sb.append("===== HISTORIQUE DES ALERTES =====\n");
        List<Alerte> alertes = ferme.filtrer(z, type, niveau, debut, fin);
        if (ferme.getAlertes().isEmpty()) {
            sb.append("Aucune alerte enregistree.\n");
        } else {
            for (Alerte a : alertes) {
                sb.append(a).append("\n");
            }
        }
        return sb.toString();
    }
}

