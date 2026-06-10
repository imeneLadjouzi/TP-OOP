package com.example.tpoop;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Alerte {
    public enum StatutAlerte { ACTIVE, ACQUITTEE, SUPPRIMEE }

    private static int compteur = 0;
    private int id;
    private Releve releve;
    private StatutAlerte statut;
    private Niveau_gravite gravite;
    private LocalDateTime dateCreation;
    private String message;
    private Zone zone;

    public Alerte(Releve releve, Niveau_gravite gravite, String message, Zone zone) {
        this.id = ++compteur;
        this.releve = releve;
        this.gravite = gravite;
        this.message = message;
        this.statut = StatutAlerte.ACTIVE;
        this.dateCreation = LocalDateTime.now();
        this.zone = zone;
    }

    public void acquitter() {
        this.statut = StatutAlerte.ACQUITTEE;
    }


    public boolean isActive() {
        return statut == StatutAlerte.ACTIVE;
    }

    public int getId() { return id; }
    public Releve getReleve() { return releve; }
    public StatutAlerte getStatut() { return statut; }
    public Niveau_gravite getGravite() { return gravite; }
    public String getMessage() { return message; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public Zone getZone() { return zone; }

    @Override
    public String toString() {
        String dateStr = dateCreation.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String indicateur = gravite == Niveau_gravite.CRITIQUE ? "RED" :
                gravite == Niveau_gravite.AVERTISSEMENT ? "YELLOW" : "GREEN";
        return "[" + id + "] " + indicateur + " " + gravite + " | " + dateStr
                + " | " + message + " | Statut: " + statut;
    }
}