package com.example.tpoop;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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




    public Zone trouverZoneParCode(String code) {
        return zones.stream()
                .filter(z -> z.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    // ==================== GESTION DES CULTURES ====================

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

    // ==================== GESTION DES CAPTEURS ====================

    public void ajouterCapteur(Capteurs capteur) {
        tousLesCapteurs.add(capteur);
    }

    public Capteurs trouverCapteurParCode(String code) {
        return tousLesCapteurs.stream()
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    public void ajouterAlerte(Alerte alerte) {
        alertes.add(alerte);
    }

    /**
     * Effectue un relevé sur un capteur et génère une alerte si hors seuils.
     */
    public Releve effectuerReleve(Capteurs capteur) {
        if (capteur.getStatus() != Status.ACTIF) {
            System.out.println("Capteur '" + capteur.getCode() + "' non actif, releve ignore.");
            return null;
        }
        return capteur.effectuerReleve();
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
                        .append(" (").append(c.getClass().getSimpleName()).append(")");
                        //.append(" | Seuils: ").append(c.getPlageSeuils());
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


    public List<Alerte> filtrer(Zone zone,
                                TypeCapteur type,
                                Niveau_gravite niveau,
                                LocalDate dateDebut,
                                LocalDate dateFin) {
        AlerteSpecification spec = AlerteSpecifications.zoneEquals(zone)
                .and(AlerteSpecifications.typeAlerteEquals(type))
                .and(AlerteSpecifications.niveauEquals(niveau))
                .and(AlerteSpecifications.dateEntre(dateDebut, dateFin));

        return alertes.stream()
                .filter(spec::isSatisfiedBy)
                .collect(Collectors.toList());
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
                        .append(" | Statut: ").append(zc.getStatus()).append(" | Culture: ");
                        if(zc.getCultures() == null) sb.append("Aucune culture enregistree");
                        else
                        sb.append(zc.getCultures().getNom());
                        sb.append(" | Capteurs: ").append(zc.getCapteurs().size()).append("\n");
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