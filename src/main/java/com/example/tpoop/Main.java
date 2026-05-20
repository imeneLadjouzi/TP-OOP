package com.example.tpoop;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Main interactif — toutes les actions passent par Gestionnaire.
 * Remplace l'ancien Main.java.
 */
public class Main {

    static final Scanner sc = new Scanner(System.in);
    static final Ferme ferme = new Ferme("Ferme Principale");
    static final Gestionnaire g = new Gestionnaire(ferme);

    public static void main(String[] args) {
        Seeds.initialiser(ferme);          // données de démarrage
        System.out.println("\nDonnees initialisees. Bienvenue !\n");

        boolean running = true;
        while (running) {
            printMenu();
            switch (lireInt("Votre choix : ")) {
                case 1 -> menuZones();
                case 2 -> menuCultures();
                case 3 -> menuAnimaux();
                case 4 -> menuCapteurs();
                case 5 -> menuAlertes();
                case 6 -> menuProduction();
                case 0 -> { running = false; System.out.println("Au revoir !"); }
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    // ================================================================
    // MENU PRINCIPAL
    // ================================================================

    static void printMenu() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║    GESTION DE FERME : " + ferme.getNom());
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  1. Zones          5. Alertes        ║");
        System.out.println("║  2. Cultures       6. Production     ║");
        System.out.println("║  3. Animaux        0. Quitter        ║");
        System.out.println("║  4. Capteurs                         ║");
        System.out.println("╚══════════════════════════════════════╝");
    }

    // ================================================================
    // ZONES
    // ================================================================

    static void menuZones() {
        loop: while (true) {
            System.out.println("\n--- Zones ---");
            System.out.println("1. Vue d'ensemble");
            System.out.println("2. Ajouter zone culture");
            System.out.println("3. Ajouter zone élevage");
            System.out.println("4. Ajouter zone aquacole");
            System.out.println("5. Désactiver une zone");
            System.out.println("6. Réactiver une zone");
            System.out.println("7. Renommer une zone");
            System.out.println("0. Retour");
            switch (lireInt("Choix : ")) {
                case 1 -> System.out.println(g.consulterZones());
                case 2 -> {
                    System.out.print("Nom : "); String nom = sc.nextLine();
                    g.ajouterZone(nom, TypeZone.CULTURE);
                }
                case 3 -> {
                    System.out.print("Nom : "); String nom = sc.nextLine();
                    System.out.println("Type de production : 1=LAIT  2=OEUFS  3=POIDS_RECOLTE");
                    TypeProd tp = switch (lireInt("")) {
                        case 2 -> TypeProd.OEUFS;
                        case 3 -> TypeProd.POIDS_RECOLTE;
                        default -> TypeProd.LAIT;
                    };
                    g.ajouterZone(nom, TypeZone.ELEVAGE);   // FIX : tp était ignoré dans l'ancien Main
                }
                case 4 -> {
                    System.out.print("Nom : "); String nom = sc.nextLine();
                    g.ajouterZone(nom, TypeZone.AQUA);
                }
                case 5 -> {
                    Zone z = choisirZone(); if (z != null) g.desactiverZone(z);
                }
                case 6 -> {
                    Zone z = choisirZone(); if (z != null) g.reactiverZone(z);
                }
                case 7 -> {
                    Zone z = choisirZone();
                    if (z != null) {
                        System.out.print("Nouveau nom : "); String n = sc.nextLine();
                        g.modifierNomZone(z, n);
                        System.out.println("Renommee.");
                    }
                }
                case 0 -> { break loop; }
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    // ================================================================
    // CULTURES
    // ================================================================

    static void menuCultures() {
        loop: while (true) {
            System.out.println("\n--- Cultures ---");
            System.out.println("1. Afficher cultures d'une zone");
            System.out.println("2. Ajouter une culture");
            System.out.println("3. Mettre à jour stade de croissance");
            System.out.println("4. Rapport global des cultures");
            System.out.println("0. Retour");
            switch (lireInt("Choix : ")) {
                case 1 -> {
                    ZoneCulture zc = choisirZoneCulture(); if (zc != null) zc.display();
                }
                case 2 -> {
                    ZoneCulture zc = choisirZoneCulture(); if (zc == null) break;
                    System.out.print("Nom : "); String nom = sc.nextLine();
                    System.out.print("Date plantation (AAAA-MM-JJ) : "); String dp = sc.nextLine();
                    System.out.print("Date recolte   (AAAA-MM-JJ) : "); String dr = sc.nextLine();
                    System.out.println("Stade : 0=GERMINATION 1=SEMI 2=CROISSANCE 3=MATURITE 4=RECOLTE");
                    StadeCroissance stade = StadeCroissance.values()[
                            Math.min(lireInt(""), StadeCroissance.values().length - 1)];
                    double phMin = lireDouble("pH min : "), phMax = lireDouble("pH max : ");
                    double hMin  = lireDouble("Humidite min (%) : "), hMax = lireDouble("Humidite max (%) : ");
                    double azMin = lireDouble("Azote min : "), azMax = lireDouble("Azote max : ");
                    double pluvi = lireDouble("Pluviometrie min : ");
                    ExigPedologiques exig = new ExigPedologiques(phMin, phMax, hMin, hMax, azMin, azMax, pluvi);
                    g.affecterCulture(zc, new Culture(nom, dp, dr, stade, exig));
                }
                case 3 -> {
                    ZoneCulture zc = choisirZoneCulture(); if (zc == null) break;
                    System.out.println("Stade : 0=GERMINATION 1=SEMI 2=CROISSANCE 3=MATURITE 4=RECOLTE");
                    StadeCroissance s = StadeCroissance.values()[
                            Math.min(lireInt(""), StadeCroissance.values().length - 1)];
                    g.mettreAJourStadeCroissance(zc, s);
                }
                case 4 -> System.out.println(ferme.genererRapportCultures());
                case 0 -> { break loop; }
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    // ================================================================
    // ANIMAUX
    // ================================================================

    static void menuAnimaux() {
        loop: while (true) {
            System.out.println("\n--- Animaux ---");
            System.out.println("1. Afficher animaux d'une zone");
            System.out.println("2. Ajouter un animal");
            System.out.println("3. Enregistrer un événement sanitaire");
            System.out.println("4. Historique sanitaire d'un animal");
            System.out.println("5. Définir programme alimentaire");
            System.out.println("6. Afficher programme alimentaire");
            System.out.println("0. Retour");
            switch (lireInt("Choix : ")) {
                case 1 -> {
                    ZoneElevage ze = choisirZoneElevage(); if (ze != null) ze.display();
                }
                case 2 -> {
                    ZoneElevage ze = choisirZoneElevage(); if (ze == null) break;
                    System.out.print("Nom espece : "); String espNom = sc.nextLine();
                    System.out.println("Type : 0=RUMINANT 1=VOLAILLE 2=AQUATIQUE");
                    TypeAnimal ta = TypeAnimal.values()[Math.min(lireInt(""), 2)];
                    int age = lireInt("Age (ans) : ");
                    double poids = lireDouble("Poids (kg) : ");
                    g.affecterAnimal(ze, new Animal(new EspeceAnim(ta, espNom), age, poids, EtatSante.SAIN));
                }
                case 3 -> {
                    ZoneElevage ze = choisirZoneElevage();
                    if (ze == null || ze.getAnimaux().isEmpty()) { System.out.println("Aucun animal."); break; }
                    Animal a = choisirAnimal(ze); if (a == null) break;
                    System.out.println("Evenement : 0=MALADIE 1=GUERISON 2=VACCINATION 3=PRISE_DE_POIDS 4=QUARANTAINE");
                    TypeEvenement te = TypeEvenement.values()[Math.min(lireInt(""), 4)];
                    System.out.print("Description : "); String desc = sc.nextLine();
                    double val = te == TypeEvenement.PRISE_DE_POIDS ? lireDouble("Nouveau poids (kg) : ") : 0;
                    a.enregistrerEvenementSanitaire(new EvenementSanitaire(te, desc, val));
                    System.out.println("Enregistre.");
                }
                case 4 -> {
                    ZoneElevage ze = choisirZoneElevage();
                    if (ze == null || ze.getAnimaux().isEmpty()) { System.out.println("Aucun animal."); break; }
                    Animal a = choisirAnimal(ze); if (a != null) a.displayHistoriqueSanitaire();
                }
                case 5 -> {
                    ZoneElevage ze = choisirZoneElevage(); if (ze == null) break;
                    System.out.print("Type d'aliment : "); String ta = sc.nextLine();
                    double q = lireDouble("Quantite par repas (kg) : ");
                    int r = lireInt("Repas par jour : ");
                    g.definirProgAlim(ze, new ProgAlimentaire(ta, q, r));
                    System.out.println("Programme defini.");
                }
                case 6 -> {
                    ZoneElevage ze = choisirZoneElevage(); if (ze == null) break;
                    if (ze.getProgAlim() != null) g.afficherProgAlim(ze);
                    else System.out.println("Aucun programme alimentaire.");
                }
                case 0 -> { break loop; }
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    // ================================================================
    // CAPTEURS
    // ================================================================

    static void menuCapteurs() {
        loop: while (true) {
            System.out.println("\n--- Capteurs ---");
            System.out.println("1. Tableau de bord");
            System.out.println("2. Ajouter un capteur");
            System.out.println("3. Effectuer un relevé");
            System.out.println("4. Historique des relevés (avec filtre optionnel)");
            System.out.println("5. Changer le statut d'un capteur");
            System.out.println("6. Configurer les seuils d'un capteur");
            System.out.println("0. Retour");
            switch (lireInt("Choix : ")) {
                case 1 -> System.out.println(ferme.tableauDeBordCapteurs());
                case 2 -> ajouterCapteur();
                case 3 -> {
                    Capteurs c = choisirCapteur(); if (c != null) ferme.effectuerReleve(c);
                }
                case 4 -> historiqueCapteursAvecFiltre();
                case 5 -> {
                    Capteurs c = choisirCapteur(); if (c == null) break;
                    System.out.println("Statut : 0=ACTIF  1=SUSPENDU  2=DEFAILLANT");
                    Status[] ss = {Status.ACTIF, Status.SUSPENDU, Status.DEFAILLANT};
                    Status s = ss[Math.min(lireInt(""), 2)];
                    g.changerStatusCapteur(c, s);
                    System.out.println("Statut mis a jour : " + c.getStatus());
                }
                case 6 -> {
                    Capteurs c = choisirCapteur(); if (c == null) break;
                    double min = lireDouble("Seuil min : ");
                    double max = lireDouble("Seuil max : ");
                    c.configurerSeuils(min, max);
                    System.out.println("Seuils configures.");
                }
                case 0 -> { break loop; }
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    static void ajouterCapteur() {
        Zone zone = choisirZone(); if (zone == null) return;
        System.out.print("Code du capteur : "); String code = sc.nextLine();
        System.out.println("Type : 1=Env  2=Sol  3=Aqua  4=Biometrique  5=GPS");
        Capteurs capteur = null;
        switch (lireInt("")) {
            case 1 -> capteur = new Cap_env(code, zone, Status.ACTIF,
                    lireDouble("Temp initiale : "),
                    lireDouble("Humidite initiale : "),
                    lireDouble("Pluviometrie initiale : "));
            case 2 -> capteur = new Cap_sol(code, zone, Status.ACTIF,
                    lireDouble("Azote : "), lireDouble("Humidite : "), lireDouble("pH : "));
            case 3 -> capteur = new Cap_aqua(code, zone, Status.ACTIF,
                    lireDouble("Temp eau : "), lireDouble("Oxygene : "), lireDouble("pH : "));
            case 4 -> capteur = new Cap_biometrique(code, zone, Status.ACTIF,
                    lireDouble("Temp corporelle : "), lireDouble("Activite/min : "));
            case 5 -> capteur = new Capteur_GPS(code, zone, Status.ACTIF,
                    new PositionGeographique(lireDouble("Latitude : "), lireDouble("Longitude : ")));
            default -> { System.out.println("Type invalide."); return; }
        }
        g.ajouterCapteur(zone, capteur);
        System.out.println("Capteur " + code + " ajoute a la zone " + zone.getName() + ".");
    }

    /**
     * Affiche l'historique des relevés d'un capteur avec un filtre optionnel par plage de dates.
     * Utilise la même logique que le filtre alertes (Specification pattern via ReleveSpecifications).
     */
    static void historiqueCapteursAvecFiltre() {
        Capteurs c = choisirCapteur(); if (c == null) return;

        System.out.println("Filtrer par date ? (o/n) : ");
        String rep = sc.nextLine().trim().toLowerCase();
        LocalDate debut = null, fin = null;
        if (rep.equals("o")) {
            debut = lireDate("Date debut (AAAA-MM-JJ, vide=aucune) : ");
            fin   = lireDate("Date fin   (AAAA-MM-JJ, vide=aucune) : ");
        }

        List<Releve> releves = ReleveSpecifications.filtrer(c.getHistorique(), debut, fin);
        System.out.println("\n=== Historique capteur [" + c.getCode() + "] ===");
        if (releves.isEmpty()) System.out.println("  Aucun releve pour cette plage.");
        else releves.forEach(r -> System.out.println("  " + r));
    }

    // ================================================================
    // ALERTES
    // ================================================================

    static void menuAlertes() {
        loop: while (true) {
            System.out.println("\n--- Alertes ---");
            System.out.println("1. Alertes actives (triées par gravité)");
            System.out.println("2. Historique filtré des alertes");
            System.out.println("3. Acquitter une alerte");
            System.out.println("4. Supprimer une alerte");
            System.out.println("0. Retour");
            switch (lireInt("Choix : ")) {
                case 1 -> g.afficherAlertesActives();
                case 2 -> {
                    // filtre par zone (optionnel)
                    System.out.println("Filtrer par zone ? (o/n)");
                    Zone z = sc.nextLine().trim().equalsIgnoreCase("o") ? choisirZone() : null;
                    // filtre par type capteur
                    System.out.println("Filtrer par type capteur ? 0=tous 1=ENV 2=SOL 3=AQUA 4=BIO 5=GPS");
                    TypeCapteur[] types = {null, TypeCapteur.ENV, TypeCapteur.SOL,
                            TypeCapteur.AQUA, TypeCapteur.BIOMETRIQUE, TypeCapteur.GPS};
                    TypeCapteur tc = types[Math.min(lireInt(""), 5)];
                    // filtre par niveau
                    System.out.println("Gravite ? 0=toutes 1=INFO 2=AVERTISSEMENT 3=CRITIQUE");
                    Niveau_gravite[] niveaux = {null, Niveau_gravite.INFO,
                            Niveau_gravite.AVERTISSEMENT, Niveau_gravite.CRITIQUE};
                    Niveau_gravite nv = niveaux[Math.min(lireInt(""), 3)];
                    // filtre date
                    LocalDate deb = lireDate("Date debut (AAAA-MM-JJ, vide=aucune) : ");
                    LocalDate fin = lireDate("Date fin   (AAAA-MM-JJ, vide=aucune) : ");
                    System.out.println(g.historiqueAlertes(z, tc, nv, deb, fin));
                }
                case 3 -> {
                    Alerte a = choisirAlerte(); if (a != null) { g.acquitterAlerte(a); System.out.println("Acquittee."); }
                }
                case 4 -> {
                    Alerte a = choisirAlerte(); if (a != null) { g.supprimerAlerte(a); System.out.println("Supprimee."); }
                }
                case 0 -> { break loop; }
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    // ================================================================
    // PRODUCTION
    // ================================================================

    static void menuProduction() {
        loop: while (true) {
            System.out.println("\n--- Production ---");
            System.out.println("1. Enregistrer une production");
            System.out.println("2. Afficher productions d'une zone");
            System.out.println("0. Retour");
            switch (lireInt("Choix : ")) {
                case 1 -> {
                    Zone z = choisirZone(); if (z == null) break;
                    System.out.println("Type : 0=LAIT  1=OEUFS  2=POIDS_RECOLTE  3=RENDEM_CULTURE");
                    TypeProd tp = TypeProd.values()[Math.min(lireInt(""), TypeProd.values().length - 1)];
                    double val = lireDouble("Valeur (" + tp.getUnite() + ") : ");
                    g.enregistrerProduction(z, new Prod(val, tp));
                }
                case 2 -> {
                    Zone z = choisirZone(); if (z == null) break;
                    System.out.println("Productions de '" + z.getName() + "' :");
                    if (z.getProductions().isEmpty()) System.out.println("  Aucune.");
                    else z.getProductions().forEach(p -> System.out.println("  - " + p));
                }
                case 0 -> { break loop; }
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    // ================================================================
    // UTILITAIRES DE SELECTION
    // ================================================================

    static Zone choisirZone() {
        List<Zone> zones = ferme.getZones();
        if (zones.isEmpty()) { System.out.println("Aucune zone."); return null; }
        System.out.println("Zones disponibles :");
        for (int i = 0; i < zones.size(); i++) {
            Zone z = zones.get(i);
            System.out.printf("  %d. [%s] %s (%s)%n", i+1, z.getCode(), z.getName(),
                    z.getClass().getSimpleName());
        }
        int idx = lireInt("Choisir (1-" + zones.size() + ") : ") - 1;
        if (idx < 0 || idx >= zones.size()) { System.out.println("Index invalide."); return null; }
        return zones.get(idx);
    }

    static ZoneCulture choisirZoneCulture() {
        List<Zone> zones = ferme.getZones().stream().filter(z -> z instanceof ZoneCulture).toList();
        if (zones.isEmpty()) { System.out.println("Aucune zone culture."); return null; }
        System.out.println("Zones culture :");
        for (int i = 0; i < zones.size(); i++)
            System.out.printf("  %d. [%s] %s%n", i+1, zones.get(i).getCode(), zones.get(i).getName());
        int idx = lireInt("Choisir : ") - 1;
        if (idx < 0 || idx >= zones.size()) { System.out.println("Index invalide."); return null; }
        return (ZoneCulture) zones.get(idx);
    }

    static ZoneElevage choisirZoneElevage() {
        List<Zone> zones = ferme.getZones().stream().filter(z -> z instanceof ZoneElevage).toList();
        if (zones.isEmpty()) { System.out.println("Aucune zone elevage."); return null; }
        System.out.println("Zones elevage :");
        for (int i = 0; i < zones.size(); i++)
            System.out.printf("  %d. [%s] %s%n", i+1, zones.get(i).getCode(), zones.get(i).getName());
        int idx = lireInt("Choisir : ") - 1;
        if (idx < 0 || idx >= zones.size()) { System.out.println("Index invalide."); return null; }
        return (ZoneElevage) zones.get(idx);
    }

    static Capteurs choisirCapteur() {
        List<Capteurs> capteurs = ferme.getTousLesCapteurs();
        if (capteurs.isEmpty()) { System.out.println("Aucun capteur."); return null; }
        System.out.println("Capteurs disponibles :");
        for (int i = 0; i < capteurs.size(); i++) {
            Capteurs c = capteurs.get(i);
            System.out.printf("  %d. [%s] %s | Zone: %s | %s%n",
                    i+1, c.getCode(), c.getClass().getSimpleName(),
                    c.getLocation() != null ? c.getLocation().getName() : "N/A",
                    c.getStatus());
        }
        int idx = lireInt("Choisir (1-" + capteurs.size() + ") : ") - 1;
        if (idx < 0 || idx >= capteurs.size()) { System.out.println("Index invalide."); return null; }
        return capteurs.get(idx);
    }

    static Animal choisirAnimal(ZoneElevage ze) {
        List<Animal> animaux = ze.getAnimaux();
        System.out.println("Animaux :");
        animaux.forEach(a -> System.out.printf("  #%d %s | %s%n", a.getID(), a.getEspece(), a.getEtat()));
        int id = lireInt("ID animal : ");
        return animaux.stream().filter(a -> a.getID() == id).findFirst().orElse(null);
    }

    static Alerte choisirAlerte() {
        List<Alerte> alertes = ferme.getAlertes().stream().filter(Alerte::isActive).toList();
        if (alertes.isEmpty()) { System.out.println("Aucune alerte active."); return null; }
        System.out.println("Alertes actives :");
        alertes.forEach(a -> System.out.println("  " + a));
        int id = lireInt("ID alerte : ");
        return alertes.stream().filter(a -> a.getId() == id).findFirst().orElse(null);
    }

    // ================================================================
    // LECTURE SECURISEE
    // ================================================================

    static int lireInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("  Entier attendu."); }
        }
    }

    static double lireDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Double.parseDouble(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("  Nombre attendu."); }
        }
    }

    /** Retourne null si l'utilisateur saisit une chaîne vide. */
    static LocalDate lireDate(String prompt) {
        System.out.print(prompt);
        String s = sc.nextLine().trim();
        if (s.isEmpty()) return null;
        try { return LocalDate.parse(s); }
        catch (DateTimeParseException e) {
            System.out.println("  Format invalide, date ignoree.");
            return null;
        }
    }
}