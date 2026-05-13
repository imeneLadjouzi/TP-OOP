package com.example.tpoop;

import java.util.ArrayList;
import java.util.List;

public class Ferme {
    private String nom;
    private List<Zone> zones;
    private List<Alerte> alertes;
    private List<Capteurs> tousLesCapteurs;

    public Ferme(String nom) {
        this.nom = nom;
        this.zones = new ArrayList<>();
        this.alertes = new ArrayList<>();
        this.tousLesCapteurs = new ArrayList<>();
    }

    // ==================== GESTION DES ZONES ====================


    public void ajouterZone(Zone z) {
        zones.add(z);
        System.out.println("Zone '" + z.getName() + "' ajoutee.");
    }

    public void supprimerZone(String code) {
        zones.removeIf(z -> z.getCode().equals(code));
        System.out.println("Zone '" + code + "' supprimee.");
    }

    public void modifierNomZone(Zone z, String nouveauNom) {
        z.setName(nouveauNom);
    }

    public void modifierCodeZone(Zone z, String nouveauCode) {
        z.setCode(nouveauCode);
    }

    public void desactiverZone(Zone z) {
        z.setStatus(Status.SUSPENDU);
        System.out.println("Zone '" + z.getName() + "' suspendue.");
    }

    public void reactiverZone(Zone z) {
        z.setStatus(Status.ACTIF);
        System.out.println("Zone '" + z.getName() + "' reactivee.");
    }

    public Zone trouverZoneParCode(String code) {
        return zones.stream()
                .filter(z -> z.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    // ==================== GESTION DES CULTURES ====================

    public void affecterCulture(Zone zone, Culture culture) {
        if (!(zone instanceof ZoneCulture)) {
            throw new IllegalArgumentException("La zone '" + zone.getName() + "' n'est pas une zone de culture.");
        }
        if (zone.getStatus() == Status.SUSPENDU) {
            throw new IllegalStateException("Impossible d'affecter une culture a une zone suspendue.");
        }
        ((ZoneCulture) zone).addCulture(culture);
        System.out.println("Culture '" + culture.getNom() + "' affectee a la zone '" + zone.getName() + "'.");
    }

    public void mettreAJourStadeCroissance(ZoneCulture zone, StadeCroissance stade) {
        zone.updateStadeCroiss(stade);
        System.out.println("Stade de croissance mis a jour : " + stade);
    }

    public String genererRapportCultures() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== RAPPORT DES CULTURES =====\n");
        boolean found = false;
        for (Zone z : zones) {
            if (z instanceof ZoneCulture zc) {
                sb.append(zc.genererRapport()).append("\n");
                found = true;
            }
        }
        if (!found) sb.append("Aucune zone de culture.\n");
        return sb.toString();
    }

    // ==================== GESTION DES ANIMAUX ====================

    public void affecterAnimal(Zone zone, Animal animal) {
        if (!(zone instanceof ZoneElevage)) {
            throw new IllegalArgumentException("La zone '" + zone.getName() + "' n'est pas une zone d'elevage.");
        }
        if (zone.getStatus() == Status.SUSPENDU) {
            throw new IllegalStateException("Impossible d'affecter un animal a une zone suspendue.");
        }
        ((ZoneElevage) zone).addAnimal(animal);
        System.out.println("Animal #" + animal.getID() + " affecte a la zone '" + zone.getName() + "'.");
    }

    public void definirProgAlimentaireElevage(ZoneElevage zone, ProgAlimentaire prog) {
        zone.setProgAlim(prog);
        System.out.println("Programme alimentaire defini pour la zone '" + zone.getName() + "'.");
    }

    public void definirProgAlimentaireAqua(ZoneAqua zone, ProgAlimentaire prog) {
        zone.setProgAlim(prog);
        System.out.println("Programme alimentaire defini pour la zone aquacole '" + zone.getName() + "'.");
    }

    // ==================== GESTION DE LA PRODUCTION ====================

    public void enregistrerProduction(Zone zone, Prod p) {
        zone.enregistrerProduction(p);
        System.out.println("Production enregistree : " + p + " pour la zone '" + zone.getName() + "'.");
    }

    // ==================== GESTION DES CAPTEURS ====================

    public void ajouterCapteur(Zone zone, Capteurs capteur) {
        zone.ajouterCapteur(capteur);
        tousLesCapteurs.add(capteur);
        System.out.println("Capteur '" + capteur.getCode() + "' ajoute a la zone '" + zone.getName() + "'.");
    }

    public Capteurs trouverCapteurParCode(String code) {
        return tousLesCapteurs.stream()
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    /**
     * Effectue un relevé sur un capteur et génère une alerte si hors seuils.
     */
    public Releve effectuerReleve(Capteurs capteur) {
        if (capteur.getStatus() != Status.ACTIF) {
            System.out.println("Capteur '" + capteur.getCode() + "' non actif, releve ignore.");
            return null;
        }
        Releve r = capteur.effectuerReleve();
        System.out.println("Releve effectue : " + r);
        // Générer une alerte si nécessaire
        if (r.getNiveauReleve() != Niveau_gravite.INFO) {
            String msg = "Capteur " + capteur.getCode() + " hors seuils : " + r.getValeurs();
            Alerte alerte = new Alerte(r, r.getNiveauReleve(), msg);
            alertes.add(alerte);
            System.out.println("  >> ALERTE generee : " + alerte);
        }
        return r;
    }

    public String tableauDeBordCapteurs() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== TABLEAU DE BORD DES CAPTEURS =====\n");
        for (Zone z : zones) {
            sb.append("Zone: ").append(z.getName()).append("\n");
            if (z.getCapteurs().isEmpty()) {
                sb.append("  Aucun capteur.\n");
            }
            for (Capteurs c : z.getCapteurs()) {
                String indicateur = c.getStatus() == Status.ACTIF ? "[ACTIF]" :
                        c.getStatus() == Status.SUSPENDU ? "[SUSP.]" : "[DEFAIL]";
                sb.append("  ").append(indicateur).append(" ").append(c.getCode())
                        .append(" (").append(c.getClass().getSimpleName()).append(")")
                        .append(" | Seuils: ").append(c.getPlageSeuils());
                if (!c.getHistorique().isEmpty()) {
                    Releve dernierReleve = c.getHistorique().get(c.getHistorique().size() - 1);
                    Niveau_gravite niv = dernierReleve.getNiveauReleve();
                    String couleur = niv == Niveau_gravite.CRITIQUE ? "CRITIQUE" :
                            niv == Niveau_gravite.AVERTISSEMENT ? "AVERTISSEMENT" : "NORMAL";
                    sb.append(" | Dernier: ").append(dernierReleve.getValeurs()).append(" [").append(couleur).append("]");
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    // ==================== GESTION DES ALERTES ====================

    public String afficherAlertesActives() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== ALERTES ACTIVES =====\n");
        alertes.stream()
                .filter(Alerte::isActive)
                .sorted((a, b) -> b.getGravite().compareTo(a.getGravite()))
                .forEach(a -> sb.append(a).append("\n"));
        if (alertes.stream().noneMatch(Alerte::isActive)) {
            sb.append("Aucune alerte active.\n");
        }
        return sb.toString();
    }

    public void acquitterAlerte(int id) {
        alertes.stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .ifPresent(a -> {
                    a.acquitter();
                    System.out.println("Alerte #" + id + " acquittee.");
                });
    }

    public void supprimerAlerte(int id) {
        alertes.stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .ifPresent(a -> {
                    a.supprimer();
                    System.out.println("Alerte #" + id + " supprimee.");
                });
    }

    public String historiqueAlertes() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== HISTORIQUE DES ALERTES =====\n");
        if (alertes.isEmpty()) {
            sb.append("Aucune alerte enregistree.\n");
        } else {
            for (Alerte a : alertes) {
                sb.append(a).append("\n");
            }
        }
        return sb.toString();
    }

    // ==================== VUE D'ENSEMBLE ====================

    public String getVueEnsembleZones() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== VUE D'ENSEMBLE DE LA FERME : ").append(nom).append(" =====\n");
        if (zones.isEmpty()) {
            sb.append("Aucune zone enregistree.\n");
            return sb.toString();
        }
        for (Zone z : zones) {
            sb.append("-------------------------------------------\n");
            if (z instanceof ZoneCulture zc) {
                sb.append("[CULTURE] ");
                sb.append(zc.getName()).append(" | Code: ").append(zc.getCode())
                        .append(" | Statut: ").append(zc.getStatus())
                        .append(" | Cultures: ").append(zc.getCultures().size())
                        .append(" | Capteurs: ").append(zc.getCapteurs().size()).append("\n");
            } else if (z instanceof ZoneElevage ze) {
                sb.append("[ELEVAGE] ");
                sb.append(ze.getName()).append(" | Code: ").append(ze.getCode())
                        .append(" | Statut: ").append(ze.getStatus())
                        .append(" | Animaux: ").append(ze.getAnimaux().size())
                        .append(" | Capteurs: ").append(ze.getCapteurs().size()).append("\n");
            } else if (z instanceof ZoneAqua za) {
                sb.append("[AQUA]    ");
                sb.append(za.getName()).append(" | Code: ").append(za.getCode())
                        .append(" | Statut: ").append(za.getStatus())
                        .append(" | Espece: ").append(za.getEspece())
                        .append(" | Capteurs: ").append(za.getCapteurs().size()).append("\n");
            }
        }
        sb.append("-------------------------------------------\n");
        sb.append("Total zones: ").append(zones.size())
                .append(" | Total capteurs: ").append(tousLesCapteurs.size())
                .append(" | Alertes actives: ")
                .append(alertes.stream().filter(Alerte::isActive).count()).append("\n");
        return sb.toString();
    }

    public String getNom() { return nom; }
    public List<Zone> getZones() { return zones; }
    public List<Alerte> getAlertes() { return alertes; }
    public List<Capteurs> getTousLesCapteurs() { return tousLesCapteurs; }
}