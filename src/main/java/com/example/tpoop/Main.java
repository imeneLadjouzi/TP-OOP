package com.example.tpoop;

import java.util.List;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static Ferme ferme = new Ferme("Ferme Principale");

    public static void main(String[] args) {
        initialiserDonnees();
        boolean running = true;
        while (running) {
            afficherMenuPrincipal();
            int choix = lireInt("Votre choix : ");
            switch (choix) {
                case 1 -> menuGestionZones();
                case 2 -> menuGestionCultures();
                case 3 -> menuGestionAnimaux();
                case 4 -> menuGestionCapteurs();
                case 5 -> menuGestionAlertes();
                case 6 -> menuProduction();
                case 0 -> { running = false; System.out.println("Au revoir !"); }
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    // ================================================================
    //  DONNEES DE DEMONSTRATION
    // ================================================================

    static void initialiserDonnees() {
        System.out.println("Initialisation des donnees de demonstration...");

        // Cultures
        ExigPedologiques exigBle = new ExigPedologiques(6.0, 7.5, 30, 70, 50, 150, 400);
        ExigPedologiques exigTomate = new ExigPedologiques(5.5, 7.0, 60, 80, 80, 200, 600);
        Culture ble = new Culture("Ble", "2025-10-01", "2026-06-15", StadeCroissance.CROISSANCE, exigBle);
        Culture tomate = new Culture("Tomate", "2026-03-01", "2026-07-30", StadeCroissance.SEMI, exigTomate);

        // Zones
        ZoneCulture zc1 = new ZoneCulture( "Champ Ble Nord", Status.ACTIF,ble);
        ZoneCulture zc2 = new ZoneCulture( "Serre Tomates", Status.ACTIF,tomate);
        ZoneElevage ze1 = new ZoneElevage( "Etable Vaches", Status.ACTIF);
        ZoneElevage ze2 = new ZoneElevage( "Poulailler", Status.ACTIF);
        ZoneAqua za1 = new ZoneAqua( "Bassin Tilapia", Status.ACTIF, "Tilapia");



        // Animaux
        EspeceAnim vache = new EspeceAnim(TypeAnimal.RUMINANT, "Vache laitiere");
        EspeceAnim poule = new EspeceAnim(TypeAnimal.VOLAILLE, "Poule pondeuse");
        Animal v1 = new Animal(vache, 4, 550, EtatSante.SAIN);
        Animal v2 = new Animal(vache, 3, 520, EtatSante.SAIN);
        Animal p1 = new Animal(poule, 1, 2.5, EtatSante.SAIN);
        Animal p2 = new Animal(poule, 1, 2.3, EtatSante.MALADE);
        ze1.addAnimal(v1); ze1.addAnimal(v2);
        ze2.addAnimal(p1); ze2.addAnimal(p2);

        // Programmes alimentaires
        ze1.setProgAlim(new ProgAlimentaire("Foin + Concentre", 8.0, 3));
        ze2.setProgAlim(new ProgAlimentaire("Grains de ble", 0.15, 2));
        za1.setProgAlim(new ProgAlimentaire("Granules poissons", 0.5, 3));
        za1.setNbAnimaux(500);

        // Capteurs
        Cap_env captEnv = new Cap_env("CE01", zc1, Status.ACTIF, 22.0, 65.0, 12.0);
        captEnv.configurerSeuils(0, 40);
        Cap_sol captSol = new Cap_sol("CS01", zc1, Status.ACTIF, 80.0, 55.0, 6.8);
        captSol.configurerSeuils(0, 14);
        Cap_biometrique captBio = new Cap_biometrique("CB01", ze1, Status.ACTIF, 38.5, 45);
        captBio.configurerSeuils(37.5, 39.5);
        Cap_aqua captAqua = new Cap_aqua("CA01", za1, Status.ACTIF, 26.0, 7.5, 7.2);
        captAqua.configurerSeuils(0, 35);
        Capteur_GPS captGPS = new Capteur_GPS("GPS01", zc1, Status.ACTIF, new PositionGeographique(36.7, 3.1));

        zc1.ajouterCapteur(captEnv);
        zc1.ajouterCapteur(captSol);
        zc1.ajouterCapteur(captGPS);
        ze1.ajouterCapteur(captBio);
        za1.ajouterCapteur(captAqua);

        ferme.getTousLesCapteurs().addAll(List.of(captEnv, captSol, captBio, captAqua, captGPS));

        // Enregistrement des zones
        ferme.getZones().add(zc1);
        ferme.getZones().add(zc2);
        ferme.getZones().add(ze1);
        ferme.getZones().add(ze2);
        ferme.getZones().add(za1);

        // Quelques relevés et production
        ferme.effectuerReleve(captEnv);
        ferme.effectuerReleve(captSol);
        ferme.effectuerReleve(captBio);
        ferme.effectuerReleve(captAqua);

        ze1.enregistrerProduction(new Prod(120.0, TypeProd.LAIT));
        ze2.enregistrerProduction(new Prod(200.0, TypeProd.OEUFS));

        System.out.println("Donnees de demonstration initialisees avec succes !\n");
    }

    // ================================================================
    //  MENUS
    // ================================================================

    static void afficherMenuPrincipal() {
        System.out.println("\n======================================");
        System.out.println("  GESTION DE FERME : " + ferme.getNom());
        System.out.println("======================================");
        System.out.println("1. Gestion des zones");
        System.out.println("2. Gestion des cultures");
        System.out.println("3. Gestion des animaux");
        System.out.println("4. Gestion des capteurs");
        System.out.println("5. Gestion des alertes");
        System.out.println("6. Production");
        System.out.println("0. Quitter");
    }

    // ---- ZONES ----

    static void menuGestionZones() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Gestion des Zones ---");
            System.out.println("1. Vue d'ensemble des zones");
            System.out.println("2. Ajouter une zone de culture");
            System.out.println("3. Ajouter une zone d'elevage");
            System.out.println("4. Ajouter une zone aquacole");
            System.out.println("5. Desactiver une zone");
            System.out.println("6. Reactiver une zone");
            System.out.println("7. Supprimer une zone");
            System.out.println("8. Modifier le nom d'une zone");
            System.out.println("0. Retour");
            int ch = lireInt("Choix : ");
            switch (ch) {
                case 1 -> System.out.println(ferme.getVueEnsembleZones());
                case 2 -> {
                    try {
                        System.out.print("Nom : ");
                        String nom = sc.nextLine();

                        if (nom == null || nom.trim().isEmpty()) {
                            throw new IllegalArgumentException("Le nom ne peut pas etre vide.");
                        }

                        if (!nom.matches("[a-zA-Z ]+")) {
                            throw new IllegalArgumentException("Le nom contient des caracteres invalides.");
                        }
                        ferme.ajouterZone(new ZoneCulture(nom, Status.ACTIF, null));
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }

                }
                case 3 -> {
                    try {
                        System.out.print("Nom : ");
                        String nom = sc.nextLine();

                        if (nom == null || nom.trim().isEmpty()) {
                            throw new IllegalArgumentException("Le nom ne peut pas etre vide.");
                        }

                        if (!nom.matches("[a-zA-Z ]+")) {
                            throw new IllegalArgumentException("Le nom contient des caracteres invalides.");
                        }
                        ferme.ajouterZone(new ZoneElevage(nom, Status.ACTIF));
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                }
                case 4 -> {

                    try {
                        System.out.print("Nom : ");
                        String nom = sc.nextLine();
                        if (nom == null || nom.trim().isEmpty()) {
                            throw new IllegalArgumentException("Le nom ne peut pas etre vide.");
                        }

                        if (!nom.matches("[a-zA-Z ]+")) {
                            throw new IllegalArgumentException("Le nom contient des caracteres invalides.");
                        }
                        System.out.print("Espece : "); String esp = sc.nextLine();
                        ferme.ajouterZone(new ZoneAqua( nom, Status.ACTIF, esp));
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                }
                case 5 -> {
                    Zone z = choisirZone();
                    if (z != null) ferme.desactiverZone(z);
                }
                case 6 -> {
                    Zone z = choisirZone();
                    if (z != null) ferme.reactiverZone(z);
                }
                case 7 -> {
                    Zone z = choisirZone();
                    if (z != null) {
                        System.out.print("Confirmer suppression de '" + z.getName() + "' ? (O/N) : ");
                        String conf = sc.nextLine();
                        if (conf.equalsIgnoreCase("O")) {
                            ferme.supprimerZone(z.getCode());
                            System.out.println("Zone supprimee.");
                        } else {
                            System.out.println("Suppression annulee.");
                        }
                    }
                }

                case 8 -> {
                    Zone z = choisirZone();
                    if (z != null) {
                        System.out.print("Nouveau nom : "); String n = sc.nextLine();
                        ferme.modifierNomZone(z, n);
                        System.out.println("Nom mis a jour.");
                    }
                }
                case 0 -> back = true;
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    // ---- CULTURES ----

    static void menuGestionCultures() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Gestion des Cultures ---");
            System.out.println("1. Afficher cultures par zone");
            System.out.println("2. Ajouter une culture a une zone");
            System.out.println("3. Mettre a jour stade de croissance");
            System.out.println("4. Rapport global des cultures");
            System.out.println("0. Retour");
            int ch = lireInt("Choix : ");
            switch (ch) {
                case 1 -> {
                    ZoneCulture zc = choisirZoneCulture();
                    if (zc != null) zc.display();
                }
                case 2 -> {
                    ZoneCulture zc = choisirZoneCulture();
                    if (zc == null) break;
                    System.out.print("Nom de la culture : "); String nom = sc.nextLine();
                    System.out.print("Date plantation (AAAA-MM-JJ) : "); String dp = sc.nextLine();
                    System.out.print("Date recolte (AAAA-MM-JJ)    : "); String dr = sc.nextLine();
                    System.out.println("Stade (0=GERMINATION 1=SEMI 2=CROISSANCE 3=MATURITE 4=RECOLTE) : ");
                    int si = lireInt("");
                    StadeCroissance stade = StadeCroissance.values()[Math.min(si, StadeCroissance.values().length - 1)];
                    System.out.print("pH min / max (ex: 6.0 7.5) : ");
                    double phMin = lireDouble("pH min : ");
                    double phMax = lireDouble("pH max : ");
                    System.out.print("Humidity min / max (ex: 6.0 7.5) : ");
                    double humiditeMin = lireDouble("Humidite min : ");
                    double humiditeMax = lireDouble("humdite max: ");
                    ExigPedologiques exig = new ExigPedologiques(phMin, phMax, humiditeMin, humiditeMax, 50, 150, 400);
                    Culture c = new Culture(nom, dp, dr, stade, exig);
                    try {
                        ferme.affecterCulture(zc, c);
                    } catch (Exception e) {
                        System.out.println("Erreur : " + e.getMessage());
                    }
                }
                case 3 -> {
                    ZoneCulture zc = choisirZoneCulture();
                    if (zc == null) break;
                    zc.displayStadeCroiss();
                    System.out.println("Stade (0=GERMINATION 1=SEMI 2=CROISSANCE 3=MATURITE 4=RECOLTE) : ");
                    int si = lireInt("");
                    StadeCroissance stade = StadeCroissance.values()[Math.min(si, StadeCroissance.values().length - 1)];
                    ferme.mettreAJourStadeCroissance(zc, stade);
                }
                case 4 -> System.out.println(ferme.genererRapportCultures());
                case 0 -> back = true;
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    // ---- ANIMAUX ----

    static void menuGestionAnimaux() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Gestion des Animaux ---");
            System.out.println("1. Afficher animaux par zone d'elevage");
            System.out.println("2. Ajouter un animal");
            System.out.println("3. Enregistrer un evenement sanitaire");
            System.out.println("4. Afficher historique sanitaire d'un animal");
            System.out.println("5. Definir programme alimentaire");
            System.out.println("6. Afficher programme alimentaire");
            System.out.println("0. Retour");
            int ch = lireInt("Choix : ");
            switch (ch) {
                case 1 -> {
                    ZoneElevage ze = choisirZoneElevage();
                    if (ze != null) ze.display();
                }
                case 2 -> {
                    ZoneElevage ze = choisirZoneElevage();
                    if (ze == null) break;
                    System.out.print("Espece (ex: Vache, Poule) : "); String espNom = sc.nextLine();
                    System.out.println("Type (0=RUMINANT 1=VOLAILLE) : ");
                    int ti = lireInt("");
                    TypeAnimal type = ti == 1 ? TypeAnimal.VOLAILLE : TypeAnimal.RUMINANT;
                    int age = lireInt("Age (ans) : ");
                    double poids = lireDouble("Poids (kg) : ");
                    Animal a = new Animal(new EspeceAnim(type, espNom), age, poids, EtatSante.SAIN);
                    try {
                        ferme.affecterAnimal(ze, a);
                    } catch (Exception e) {
                        System.out.println("Erreur : " + e.getMessage());
                    }
                }
                case 3 -> {
                    ZoneElevage ze = choisirZoneElevage();
                    if (ze == null || ze.getAnimaux().isEmpty()) { System.out.println("Aucun animal."); break; }
                    System.out.println("Animaux disponibles :");
                    ze.getAnimaux().forEach(a -> System.out.println("  #" + a.getID() + " " + a.getEspece()));
                    int id = lireInt("ID animal : ");
                    Animal animal = ze.getAnimaux().stream().filter(a -> a.getID() == id).findFirst().orElse(null);
                    if (animal == null) { System.out.println("Animal non trouve."); break; }
                    System.out.println("Type (0=MALADIE 1=GUERISON 2=VACCINATION 3=PRISE_DE_POIDS 4=QUARANTAINE) : ");
                    int ti = lireInt("");
                    EvenementSanitaire.TypeEvenement te = EvenementSanitaire.TypeEvenement.values()[Math.min(ti, 4)];
                    System.out.print("Description : "); String desc = sc.nextLine();
                    double val = 0;
                    if (te == EvenementSanitaire.TypeEvenement.PRISE_DE_POIDS) val = lireDouble("Nouveau poids (kg) : ");
                    animal.enregistrerEvenementSanitaire(new EvenementSanitaire(te, desc, val));
                    System.out.println("Evenement enregistre.");
                }
                case 4 -> {
                    ZoneElevage ze = choisirZoneElevage();
                    if (ze == null || ze.getAnimaux().isEmpty()) { System.out.println("Aucun animal."); break; }
                    ze.getAnimaux().forEach(a -> System.out.println("  #" + a.getID() + " " + a.getEspece()));
                    int id = lireInt("ID animal : ");
                    ze.getAnimaux().stream().filter(a -> a.getID() == id).findFirst()
                            .ifPresentOrElse(Animal::displayHistoriqueSanitaire, () -> System.out.println("Non trouve."));
                }
                case 5 -> {
                    ZoneElevage ze = choisirZoneElevage();
                    if (ze == null) break;
                    System.out.print("Type d'aliment : "); String ta = sc.nextLine();
                    double qr = lireDouble("Quantite par repas (kg) : ");
                    int rj = lireInt("Repas par jour : ");
                    ferme.definirProgAlimentaireElevage(ze, new ProgAlimentaire(ta, qr, rj));
                }
                case 6 -> {
                    ZoneElevage ze = choisirZoneElevage();
                    if (ze != null) {
                        if (ze.getProgAlim() != null) ze.getProgAlim().display();
                        else System.out.println("Aucun programme alimentaire defini.");
                    }
                }
                case 0 -> back = true;
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    // ---- CAPTEURS ----

    static void menuGestionCapteurs() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Gestion des Capteurs ---");
            System.out.println("1. Tableau de bord des capteurs");
            System.out.println("2. Ajouter un capteur");
            System.out.println("3. Effectuer un releve");
            System.out.println("4. Historique des releves d'un capteur");
            System.out.println("5. Changer le statut d'un capteur");
            System.out.println("6. Configurer seuils d'un capteur");
            System.out.println("0. Retour");
            int ch = lireInt("Choix : ");
            switch (ch) {
                case 1 -> System.out.println(ferme.tableauDeBordCapteurs());
                case 2 -> ajouterCapteur();
                case 3 -> {
                    Capteurs c = choisirCapteur();
                    if (c != null) ferme.effectuerReleve(c);
                }
                case 4 -> {
                    Capteurs c = choisirCapteur();
                    if (c != null) {
                        System.out.println("Historique du capteur " + c.getCode() + " :");
                        if (c.getHistorique().isEmpty()) System.out.println("  Aucun releve.");
                        else c.getHistorique().forEach(r -> System.out.println("  " + r));
                    }
                }
                case 5 -> {
                    Capteurs c = choisirCapteur();
                    if (c == null) break;
                    System.out.println("Statut (0=ACTIF 1=SUSPENDU 2=DEFAILLANT) : ");
                    int si = lireInt("");
                    Status[] statuts = {Status.ACTIF, Status.SUSPENDU, Status.DEFAILLANT};
                    c.setStatus(statuts[Math.min(si, 2)]);
                    System.out.println("Statut mis a jour : " + c.getStatus());
                }
                case 6 -> {
                    Capteurs c = choisirCapteur();
                    if (c == null) break;
                    double min = lireDouble("Seuil min : ");
                    double max = lireDouble("Seuil max : ");
                    c.configurerSeuils(min, max);
                    System.out.println("Seuils configures.");
                }
                case 0 -> back = true;
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    static void ajouterCapteur() {
        Zone zone = choisirZone();
        if (zone == null) return;
        System.out.print("Code du capteur : "); String code = sc.nextLine();
        System.out.println("Type (1=Env 2=Sol 3=Aqua 4=Biometrique 5=GPS) : ");
        int ti = lireInt("");
        Capteurs capteur = null;
        switch (ti) {
            case 1 -> {
                double t = lireDouble("Temp initiale : ");
                double h = lireDouble("Humidite initiale : ");
                double p = lireDouble("Pluviometrie initiale : ");
                capteur = new Cap_env(code, zone, Status.ACTIF, t, h, p);
            }
            case 2 -> {
                double az = lireDouble("Azote : "); double h = lireDouble("Humidite : "); double ph = lireDouble("pH : ");
                capteur = new Cap_sol(code, zone, Status.ACTIF, az, h, ph);
            }
            case 3 -> {
                double t = lireDouble("Temp eau : "); double ox = lireDouble("Oxygene : "); double ph = lireDouble("pH : ");
                capteur = new Cap_aqua(code, zone, Status.ACTIF, t, ox, ph);
            }
            case 4 -> {
                double tc = lireDouble("Temp corporelle : "); double act = lireDouble("Activite/min : ");
                capteur = new Cap_biometrique(code, zone, Status.ACTIF, tc, act);
            }
            case 5 -> {
                double lat = lireDouble("Latitude : "); double lon = lireDouble("Longitude : ");
                capteur = new Capteur_GPS(code, zone, Status.ACTIF, new PositionGeographique(lat, lon));
            }
            default -> { System.out.println("Type invalide."); return; }
        }
        ferme.ajouterCapteur(zone, capteur);
    }

    // ---- ALERTES ----

    static void menuGestionAlertes() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Gestion des Alertes ---");
            System.out.println("1. Alertes actives (triees par gravite)");
            System.out.println("2. Historique de toutes les alertes");
            System.out.println("3. Acquitter une alerte");
            System.out.println("4. Supprimer une alerte");
            System.out.println("0. Retour");
            int ch = lireInt("Choix : ");
            switch (ch) {
                case 1 -> System.out.println(ferme.afficherAlertesActives());
                case 2 -> System.out.println(ferme.historiqueAlertes());
                case 3 -> { int id = lireInt("ID alerte a acquitter : "); ferme.acquitterAlerte(id); }
                case 4 -> { int id = lireInt("ID alerte a supprimer : "); ferme.supprimerAlerte(id); }
                case 0 -> back = true;
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    // ---- PRODUCTION ----

    static void menuProduction() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Gestion de la Production ---");
            System.out.println("1. Enregistrer une production");
            System.out.println("2. Afficher productions d'une zone");
            System.out.println("0. Retour");
            int ch = lireInt("Choix : ");
            switch (ch) {
                case 1 -> {
                    Zone z = choisirZone();
                    if (z == null) break;
                    System.out.println("Type (0=LAIT 1=OEUFS 2=POIDS_RECOLTE 3=RENDEM_CULTURE) : ");
                    int ti = lireInt("");
                    TypeProd tp = TypeProd.values()[Math.min(ti, TypeProd.values().length - 1)];
                    double val = lireDouble("Valeur (" + tp.getUnite() + ") : ");
                    ferme.enregistrerProduction(z, new Prod(val, tp));
                }
                case 2 -> {
                    Zone z = choisirZone();
                    if (z != null) {
                        System.out.println("Productions de la zone '" + z.getName() + "' :");
                        if (z.getProductions().isEmpty()) System.out.println("  Aucune production enregistree.");
                        else z.displayProduction();
                    }
                }
                case 0 -> back = true;
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    // ================================================================
    //  UTILITAIRES DE SELECTION
    // ================================================================

    static Zone choisirZone() {
        List<Zone> zones = ferme.getZones();
        if (zones.isEmpty()) { System.out.println("Aucune zone disponible."); return null; }
        System.out.println("Zones disponibles :");
        for (int i = 0; i < zones.size(); i++) {
            Zone z = zones.get(i);
            System.out.println("  " + (i + 1) + ". [" + z.getCode() + "] " + z.getName() + " (" + z.getClass().getSimpleName() + ")");
        }
        try{
            int idx = lireInt("Choisir (1-" + zones.size() + ") : ") - 1;
            return zones.get(idx);
        }
        catch (IndexOutOfBoundsException e){
            System.out.println("Index invalide.");
            return null;
        }
    }

    static ZoneCulture choisirZoneCulture() {
        List<Zone> zones = ferme.getZones().stream().filter(z -> z instanceof ZoneCulture).toList();
        if (zones.isEmpty()) { System.out.println("Aucune zone de culture."); return null; }
        System.out.println("Zones de culture :");
        for (int i = 0; i < zones.size(); i++) {
            System.out.println("  " + (i + 1) + ". [" + zones.get(i).getCode() + "] " + zones.get(i).getName());
        }
        try{
            int idx = lireInt("Choisir (1-" + zones.size() + ") : ") - 1;
            return (ZoneCulture) zones.get(idx);
        }
        catch (IndexOutOfBoundsException e){
            System.out.println("Index invalide.");
            return null;
        }
    }

    static ZoneElevage choisirZoneElevage() {
        List<Zone> zones = ferme.getZones().stream().filter(z -> z instanceof ZoneElevage).toList();
        if (zones.isEmpty()) { System.out.println("Aucune zone d'elevage."); return null; }
        System.out.println("Zones d'elevage :");
        for (int i = 0; i < zones.size(); i++) {
            System.out.println("  " + (i + 1) + ". [" + zones.get(i).getCode() + "] " + zones.get(i).getName());
        }
        try{
            int idx = lireInt("Choisir (1-" + zones.size() + ") : ") - 1;
            return (ZoneElevage) zones.get(idx);
        }
        catch (IndexOutOfBoundsException e){
            System.out.println("Index invalide.");
            return null;
        }
    }

    static Capteurs choisirCapteur() {
        List<Capteurs> capteurs = ferme.getTousLesCapteurs();
        if (capteurs.isEmpty()) { System.out.println("Aucun capteur."); return null; }
        System.out.println("Capteurs disponibles :");
        for (int i = 0; i < capteurs.size(); i++) {
            Capteurs c = capteurs.get(i);
            System.out.println("  " + (i + 1) + ". [" + c.getCode() + "] "
                    + c.getClass().getSimpleName() + " | " + c.getStatus());
        }
        try{
            int idx = lireInt("Choisir (1-" + capteurs.size() + ") : ") - 1;
            return capteurs.get(idx);
        }
        catch (IndexOutOfBoundsException e){
            System.out.println("Index invalide.");
            return null;
        }
    }


    //  ================= Additional ====================


    static int lireInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = sc.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Entree invalide, veuillez entrer un entier.");
            }
        }
    }

    static double lireDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = sc.nextLine().trim();
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Entree invalide, veuillez entrer un nombre.");
            }
        }
    }
}