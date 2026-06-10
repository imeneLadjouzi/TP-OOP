package com.example.tpoop;

import java.util.List;

/**
 * Initialise la ferme avec un jeu de données cohérent.
 * Utilisé par MainInteractif et TestScenarios.
 */
public class Seeds {

    public static void initialiser(Ferme ferme) {
        // ── ZONES ──────────────────────────────────────────────────────────
        ZoneCulture zc1 = new ZoneCulture("Champ Ble Nord",  Status.ACTIF,null,ferme);
        ZoneCulture zc2 = new ZoneCulture("Serre Tomates",   Status.ACTIF,null,ferme);
        ZoneElevage ze1 = new ZoneElevage("Etable Vaches",   Status.ACTIF,TypeAnimal.RUMINANT,ferme);
        ZoneElevage ze2 = new ZoneElevage("Poulailler",      Status.ACTIF,TypeAnimal.VOLAILLE,ferme);
        ZoneAqua    za1 = new ZoneAqua   ("Bassin Tilapia",  Status.ACTIF,"sardine",ferme);

        // ── CULTURES ───────────────────────────────────────────────────────
        ExigPedologiques exigBle    = new ExigPedologiques(6.0, 7.5, 30, 70, 50, 150);
        ExigPedologiques exigTomate = new ExigPedologiques(5.5, 7.0, 60, 80, 80, 200);
        Culture ble    = new Culture("Ble",    "2025-10-01", "2026-06-15", StadeCroissance.CROISSANCE, exigBle);
        Culture tomate = new Culture("Tomate", "2026-03-01", "2026-07-30", StadeCroissance.SEMI,       exigTomate);
        zc1.setCulture(ble);
        zc2.setCulture(tomate);

        // ── ANIMAUX ────────────────────────────────────────────────────────
        EspeceAnim vache = new EspeceAnim(TypeAnimal.RUMINANT, "Vache laitiere");
        EspeceAnim poule = new EspeceAnim(TypeAnimal.VOLAILLE,  "Poule pondeuse");
        Animal v1 = new Animal(vache, 4, 550.0, EtatSante.SAIN);
        Animal v2 = new Animal(vache, 3, 520.0, EtatSante.SAIN);
        Animal p1 = new Animal(poule, 1,   2.5, EtatSante.SAIN);
        Animal p2 = new Animal(poule, 1,   2.3, EtatSante.MALADE);
        ze1.addAnimal(v1); ze1.addAnimal(v2);
        ze2.addAnimal(p1); ze2.addAnimal(p2);

        // ── PROGRAMMES ALIMENTAIRES ────────────────────────────────────────
        ze1.setProgAlim(new ProgAlimentaire("Foin + Concentre",   8.0,  3));
        ze2.setProgAlim(new ProgAlimentaire("Grains de ble",      0.15, 2));
        za1.setProgAlim(new ProgAlimentaire("Granules poissons",  0.5,  3));
        za1.setNbAnimaux(500);

        // ── CAPTEURS ───────────────────────────────────────────────────────
        Cap_env       captEnv  = new Cap_env(  zc1, Status.ACTIF);

        Cap_sol       captSol  = new Cap_sol( zc1, Status.ACTIF);

        Cap_biometrique captBio = new Cap_biometrique( ze1, Status.ACTIF);

        Cap_aqua      captAqua = new Cap_aqua(za1, Status.ACTIF);

        Capteur_GPS   captGPS  = new Capteur_GPS( zc1, Status.ACTIF
                ,new Animal(new EspeceAnim(TypeAnimal.VOLAILLE,"vache"),5,50,EtatSante.SAIN));

        // Affectation capteurs → zones
        zc1.ajouterCapteur(captEnv);
        zc1.ajouterCapteur(captSol);
        zc1.ajouterCapteur(captGPS);
        ze1.ajouterCapteur(captBio);
        za1.ajouterCapteur(captAqua);

        // ── ENREGISTREMENT DANS LA FERME ──────────────────────────────────
        ferme.getZones().addAll(List.of(zc1, zc2, ze1, ze2, za1));
        ferme.getTousLesCapteurs().addAll(List.of(captEnv, captSol, captBio, captAqua, captGPS));

        // Premiers relevés automatiques
        ferme.effectuerReleve(captEnv);
        ferme.effectuerReleve(captSol);
        ferme.effectuerReleve(captBio);
        ferme.effectuerReleve(captAqua);

        // Productions initiales
        ze1.enregistrerProduction(new Prod(120.0, TypeProd.LAIT));
        ze2.enregistrerProduction(new Prod(200.0, TypeProd.OEUFS));
    }

    // Accesseurs pratiques pour les tests
    public static ZoneCulture premierZoneCulture(Ferme f) {
        return (ZoneCulture) f.getZones().stream()
                .filter(z -> z instanceof ZoneCulture).findFirst().orElseThrow();
    }
    public static ZoneElevage premierZoneElevage(Ferme f) {
        return (ZoneElevage) f.getZones().stream()
                .filter(z -> z instanceof ZoneElevage).findFirst().orElseThrow();
    }
    public static ZoneAqua premierZoneAqua(Ferme f) {
        return (ZoneAqua) f.getZones().stream()
                .filter(z -> z instanceof ZoneAqua).findFirst().orElseThrow();
    }
    public static Capteurs capteurParCode(Ferme f, String code) {
        return f.getTousLesCapteurs().stream()
                .filter(c -> c.getCode().equals(code)).findFirst().orElseThrow();
    }
}