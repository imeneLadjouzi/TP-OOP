package com.example.tpoop;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Gestionnaire {

    Ferme ferme;

    /**
     * Injected by MainController after both objects are constructed.
     * Nullable — all call sites guard with a null-check via the log() helper.
     */
    private NotificationService notifService;

    public Gestionnaire(Ferme ferme) {
        this.ferme = ferme;
    }

    /** Called by MainController once NotificationService is ready. */
    public void setNotificationService(NotificationService ns) {
        this.notifService = ns;
    }

    // ── private helper — logs an ACTION notification if service is wired ──
    private void log(String title, String detail) {
        if (notifService != null) notifService.logAction(title, detail);
    }

    // ---------------------------------Zones

    public void ajouterZone(String nom, TypeZone type) {
        Zone z;
        switch (type) {
            case AQUA -> {
                Scanner sc = new Scanner(System.in);
                System.out.print("Espece habitant la zone: ");
                String espece = sc.nextLine();
                z = new ZoneAqua(nom, Status.ACTIF, espece, ferme);
                ferme.ajouterZone(z);
                log("Zone ajoutée", "Zone " + z.getCode() + " — " + nom + " (AQUA)");
            }
            case CULTURE -> {
                z = new ZoneCulture(nom, Status.ACTIF, null, ferme);
                ferme.ajouterZone(z);
                log("Zone ajoutée", "Zone " + z.getCode() + " — " + nom + " (CULTURE)");
            }
            case ELEVAGE -> {
                Scanner sc = new Scanner(System.in);
                try {
                    System.out.print("Type d'animaux habitant la zone (RUMINANT-VOLAILLE): ");
                    String s = sc.nextLine().trim().toUpperCase();
                    TypeAnimal ta = TypeAnimal.valueOf(s);
                    z = new ZoneElevage(nom, Status.ACTIF, ta, ferme);
                    ferme.ajouterZone(z);
                    log("Zone ajoutée", "Zone " + z.getCode() + " — " + nom + " (ELEVAGE " + ta + ")");
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

    public void modifierNomZone(Zone z, String nom) {
        String ancien = z.getName();
        z.setName(nom);
        log("Zone renommée", z.getCode() + " : « " + ancien + " » → « " + nom + " »");
    }

    public void modifierCodeZone(Zone z, String code) {
        z.setName(code);
    }

    public void desactiverZone(Zone z) {
        z.suspendre();
        log("Zone désactivée", z.getCode() + " — " + z.getName());
    }

    public void affecterCulture(ZoneCulture zone, Culture culture) {
        if (zone.getStatus() == Status.SUSPENDU) {
            throw new IllegalStateException("Impossible d'affecter une culture a une zone suspendue.");
        }
        zone.setCulture(culture);
        log("Culture affectée", "Zone " + zone.getCode() + " — culture : " + culture.getNom());
        System.out.println("Culture '" + culture.getNom() + "' affectee a la zone '" + zone.getName() + "'.");
    }

    public void affecterAnimal(ZoneElevage zone, Animal animal) {
        try {
            if (zone.getStatus() == Status.SUSPENDU) {
                throw new IllegalStateException("Impossible d'affecter un animal a une zone suspendue.");
            }
            if (animal.getEspece().getType() != zone.getTypeAnimal()) {
                throw new TypeAnimalIncompatibleException(
                        "Type d'animal incompatible avec la zone. Zone attend: "
                                + zone.getTypeAnimal()
                                + ", mais l'animal est de type: " + animal.getEspece().getType());
            }
            zone.addAnimal(animal);
            log("Animal ajouté",
                    "Zone " + zone.getCode() + " — #" + animal.getID()
                            + " " + animal.getEspece().getName());
            System.out.println("Animal " + animal.getEspece().getName()
                    + " affecte a la zone '" + zone.getName() + "'.");
        } catch (TypeAnimalIncompatibleException e) {
            System.out.println(e.getMessage());
        }
    }

    public String consulterZones() {
        return ferme.getVueEnsembleZones();
    }

    public void reactiverZone(Zone z) {
        z.reactiver();
        log("Zone réactivée", z.getCode() + " — " + z.getName());
        System.out.println("Zone '" + z.getName() + "' reactivee.");
    }

    public void enregistrerProduction(Zone zone, Prod p) {
        zone.enregistrerProduction(p);
        log("Production enregistrée",
                "Zone " + zone.getCode() + " — " + p.getVal()
                        + " " + p.getProd().getUnite() + " (" + p.getProd() + ")");
        System.out.println("Production enregistree : " + p + " pour la zone '" + zone.getName() + "'.");
    }

    // -----------------------Cultures

    public void displayStadeCroissance(ZoneCulture z) {
        z.displayStadeCroiss();
    }

    public void mettreAJourStadeCroissance(ZoneCulture zone, StadeCroissance stade) {
        StadeCroissance ancien = zone.getCultures() != null ? zone.getCultures().getStadeCroiss() : null;
        zone.updateStadeCroiss(stade);
        log("Stade de croissance mis à jour",
                "Zone " + zone.getCode()
                        + (ancien != null ? " : " + ancien + " → " : " : ") + stade);
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
            ((ZoneElevage) zone).setProgAlim(progAlimentaire);
        }
        if (zone instanceof ZoneAqua) {
            ((ZoneAqua) zone).setProgAlim(progAlimentaire);
        }
        log("Programme alimentaire défini",
                "Zone " + zone.getCode() + " — " + progAlimentaire.getTypeAliment()
                        + ", " + progAlimentaire.getQuantiteParRepas() + " kg × "
                        + progAlimentaire.getRepasParJour() + " repas/j");
    }

    public void afficherProgAlim(ZoneElevage zone) {
        zone.getProgAlim().display();
    }

    public void afficherProgAlim(ZoneAqua zone) {
        zone.getProgAlim().display();
    }

    public void supprimerZone(String code) {
        ferme.getZones().stream()
                .filter(z -> z.getCode().equals(code))
                .findFirst()
                .ifPresent(z -> log("Zone supprimée", z.getCode() + " — " + z.getName()));
        ferme.getZones().removeIf(z -> z.getCode().equals(code));
    }

    // --------------------------- Capteurs -------------------

    public void ajouterCapteur(Zone zone, Capteurs capteur) {
        if (zone.getStatus() == Status.SUSPENDU) {
            throw new IllegalStateException("Impossible d'ajouter un capteur a une zone suspendue.");
        }
        zone.ajouterCapteur(capteur);
        ferme.ajouterCapteur(capteur);
        log("Capteur ajouté",
                capteur.getCode() + " (" + capteur.getType() + ") → Zone " + zone.getCode());
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
        Status ancien = capteurs.getStatus();
        capteurs.setStatus(status);
        log("Statut capteur modifié",
                capteurs.getCode() + " : " + ancien + " → " + status);
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
        String zone = alerte.getZone() != null ? alerte.getZone().getName() : "—";
        log("Alerte acquittée",
                "Alerte #" + alerte.getId() + " [" + alerte.getGravite() + "] — "
                        + alerte.getMessage() + " (zone : " + zone + ")");
    }

    public void supprimerAlerte(Alerte alerte) {
        String zone = alerte.getZone() != null ? alerte.getZone().getName() : "—";
        log("Alerte supprimée",
                "Alerte #" + alerte.getId() + " [" + alerte.getGravite() + "] — "
                        + alerte.getMessage() + " (zone : " + zone + ")");
        ferme.supprimerAlerte(alerte);
    }

    public String historiqueAlertes(
            Zone z,
            TypeCapteur type,
            Niveau_gravite niveau,
            LocalDate debut,
            LocalDate fin) {
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