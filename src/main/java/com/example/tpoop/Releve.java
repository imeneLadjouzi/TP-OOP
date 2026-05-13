package com.example.tpoop;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Releve {
    private static int compteur = 0;
    private int id;
    private String codeCapteur;
    private Map<String, Object> valeurs;
    private LocalDateTime dateHeure;
    private Niveau_gravite niveauReleve;

    public Releve(String codeCapteur, Map<String, Object> valeurs) {
        this.id = ++compteur;
        this.codeCapteur = codeCapteur;
        this.valeurs = valeurs;
        this.dateHeure = LocalDateTime.now();
        this.niveauReleve = Niveau_gravite.INFO;
    }

    public Releve(String codeCapteur, Map<String, Object> valeurs, Niveau_gravite niveau) {
        this(codeCapteur, valeurs);
        this.niveauReleve = niveau;
    }

    public int getId() { return id; }
    public String getCodeCapteur() { return codeCapteur; }
    public Map<String, Object> getValeurs() { return valeurs; }
    public LocalDateTime getDateHeure() { return dateHeure; }
    public Niveau_gravite getNiveauReleve() { return niveauReleve; }
    public void setNiveauReleve(Niveau_gravite niveau) { this.niveauReleve = niveau; }

    @Override
    public String toString() {
        String dateStr = dateHeure.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return "[" + id + "] " + dateStr + " | Capteur: " + codeCapteur
                + " | Valeurs: " + valeurs + " | Niveau: " + niveauReleve;
    }
}