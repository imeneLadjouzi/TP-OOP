package com.example.tpoop;

import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
                z = new ZoneAqua(nom, Status.ACTIF);
            }
            case CULTURE -> {
                z = new ZoneCulture(nom, Status.ACTIF);
            }
            case ELEVAGE -> {
                z = new ZoneElevage(nom, Status.ACTIF);
            }
            default -> {
                System.out.println("Erreur");
                return;
            }
        }
        ferme.ajouterZone(z);
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
        zone.addCulture(culture);
        System.out.println("Culture '" + culture.getNom() + "' affectee a la zone '" + zone.getName() + "'.");
    }

    public void affecterAnimal(ZoneElevage zone, Animal animal) {
        /*if (zone.getStatus() == Status.SUSPENDU) {
            throw new IllegalStateException("Impossible d'affecter une culture a une zone suspendue.");
        }*/
        zone.addAnimal(animal);
        System.out.println("Animal '" + "' affectee a la zone '" + zone.getName() + "'.");
    }

    public String consulterZones() {
        return ferme.getVueEnsembleZones();
    }

    public void reactiverZone(Zone z) {
        z.setStatus(Status.ACTIF);
        System.out.println("Zone '" + z.getName() + "' reactivee.");
    }

    public void enregistrerProduction(Zone zone, Prod p) {
        zone.enregistrerProduction(p);
        System.out.println("Production enregistree : " + p + " pour la zone '" + zone.getName() + "'.");
    }

    // -----------------------Cultures

    public void enregistrerCulture(ZoneCulture z, Culture c) {
        z.addCulture(c);
    }

    public void mettreAJourStadeCroissance(ZoneCulture zone, StadeCroissance stade) {
        zone.updateStadeCroiss(stade);
        System.out.println("Stade de croissance mis a jour : " + stade);
    }

    public void rapportCultures(ZoneCulture zone) {
        System.out.println(zone.genererRapport());
    }

    // -----------------------------Animaux

    public void enregistrerAnimal(ZoneElevage z, Animal c) {
        z.addAnimal(c);
    }

    public void consignerEvenementsSanitaires(ZoneElevage zone) {
        for (Animal an : zone.getAnimaux()) {
            an.displayHistoriqueSanitaire();

        }

    }

    public void definirProgAlim(ZoneElevage zone, ProgAlimentaire progAlimentaire) {
        zone.setProgAlim(progAlimentaire);
    }

    public void afficherProgAlim(ZoneElevage zone) {
        zone.getProgAlim().display();
    }

    // --------------------------- Capteurs -------------------

    public void ajouterCapteur(Zone zone, Capteurs capteur) {
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

    public void historiqueCapteurs(Capteurs cap, LocalDateTime date) {

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

