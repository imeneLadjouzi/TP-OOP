package com.example.tpoop;

public class PlageSeuils {
    private double min;
    private double max;
    private double seuilAvertissement;
    private double seuilCritique;

    public PlageSeuils(double min, double max) {
        this.min = min;
        this.max = max;
        this.seuilAvertissement = min + (max - min) * 0.8;
        this.seuilCritique = max;
    }

    public PlageSeuils(double min, double max, double seuilAvertissement, double seuilCritique) {
        this.min = min;
        this.max = max;
        this.seuilAvertissement = seuilAvertissement;
        this.seuilCritique = seuilCritique;
    }

    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getSeuilAvertissement() { return seuilAvertissement; }
    public double getSeuilCritique() { return seuilCritique; }

    public Niveau_gravite evaluer(double valeur) {
        if (valeur < min || valeur > max) return Niveau_gravite.CRITIQUE;
        if (valeur >= seuilAvertissement) return Niveau_gravite.AVERTISSEMENT;
        return Niveau_gravite.INFO;
    }

    @Override
    public String toString() {
        return "[" + min + " - " + max + "]";
    }
}