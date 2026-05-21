package com.example.tpoop;

import java.time.LocalDate;
import java.util.List;

/**
 * Classe de test avec scénarios prédéfinis.
 * Aucune bibliothèque externe nécessaire — tout est affiché sur stdout.
 * Chaque méthode peut être appelée indépendamment.
 *
 * Lancer : java com.example.tpoop.TestScenarios
 */
public class TestScenarios {

    // ── Compteur résultat ─────────────────────────────────────────────
    private static int ok  = 0;
    private static int nok = 0;

    public static void main(String[] args) {
        scenario1_GestionZones();
        scenario2_Cultures();
        scenario3_Animaux();
        scenario4_Capteurs();
        scenario5_AlertesEtFiltres();
        scenario6_FiltreReleves();
        scenario7_Production();
        scenario8_StatusCapteurEtReleve();

        System.out.println("\n╔══════════════════════════════╗");
        System.out.printf ("║  Résultats : %d OK  %d ECHEC   ║%n", ok, nok);
        System.out.println("╚══════════════════════════════╝");
    }

    // ================================================================
    // Scénario 1 — Gestion des zones
    // ================================================================
    static void scenario1_GestionZones() {
        titre("SCENARIO 1 : Gestion des zones");

        Ferme ferme = new Ferme("TestFerme");
        Gestionnaire g = new Gestionnaire(ferme);

        g.ajouterZone("Champ A", TypeZone.CULTURE);
        g.ajouterZone("Etable B", TypeZone.ELEVAGE);
        g.ajouterZone("Bassin C", TypeZone.AQUA);

        asserter("3 zones créées", ferme.getZones().size() == 3);

        Zone zc = ferme.getZones().get(0);
        asserter("ZoneCulture instanceof", zc instanceof ZoneCulture);

        Zone ze = ferme.getZones().get(1);
        asserter("ZoneElevage instanceof", ze instanceof ZoneElevage);

        // Désactivation / réactivation
        g.desactiverZone(zc);
        asserter("Zone suspendue", zc.getStatus() == Status.SUSPENDU);
        g.reactiverZone(zc);
        asserter("Zone réactivée", zc.getStatus() == Status.ACTIF);

        // Renommer
        g.modifierNomZone(zc, "Champ Nord");
        asserter("Zone renommée", zc.getName().equals("Champ Nord"));

        System.out.println(g.consulterZones());
    }

    // ================================================================
    // Scénario 2 — Cultures
    // ================================================================
    static void scenario2_Cultures() {
        titre("SCENARIO 2 : Cultures");

        Ferme ferme = new Ferme("TestFerme2");
        Gestionnaire g = new Gestionnaire(ferme);
        g.ajouterZone("Serre", TypeZone.CULTURE);
        ZoneCulture zc = Seeds.premierZoneCulture(ferme);

        ExigPedologiques exig = new ExigPedologiques(6.0, 7.5, 30, 70, 50, 150, 400, 680);
        Culture ble = new Culture("Ble", "2025-10-01", "2026-06-15", StadeCroissance.GERMINATION, exig);
        g.affecterCulture(zc, ble);

        asserter("Culture ajoutée", zc.getCultures() == ble);
        asserter("Stade initial GERMINATION", ble.getStadeCroiss() == StadeCroissance.GERMINATION);

        // Mise à jour du stade
        g.mettreAJourStadeCroissance(zc, StadeCroissance.CROISSANCE);
        asserter("Stade mis à jour CROISSANCE", ble.getStadeCroiss() == StadeCroissance.CROISSANCE);

        // Compatibilité pédologique
        asserter("Sol compatible (pH=6.8, h=50)", exig.estCompatible(6.8, 50, 80, 60));
        asserter("Sol incompatible (pH=5.0)",     !exig.estCompatible(5.0, 50, 95, 60));

        System.out.println(ferme.genererRapportCultures());
    }

    // ================================================================
    // Scénario 3 — Animaux et événements sanitaires
    // ================================================================
    static void scenario3_Animaux() {
        titre("SCENARIO 3 : Animaux");

        Ferme ferme = new Ferme("TestFerme3");
        Gestionnaire g = new Gestionnaire(ferme);
        g.ajouterZone("Etable", TypeZone.ELEVAGE);
        ZoneElevage ze = Seeds.premierZoneElevage(ferme);

        EspeceAnim esp = new EspeceAnim(TypeAnimal.RUMINANT, "Vache");
        Animal v = new Animal(esp, 5, 600.0, EtatSante.SAIN);
        g.affecterAnimal(ze, v);

        asserter("Animal ajouté", ze.getAnimaux().size() == 1);
        asserter("Santé initiale SAIN", v.getEtat() == EtatSante.SAIN);

        // Maladie
        v.enregistrerEvenementSanitaire(new EvenementSanitaire(TypeEvenement.MALADIE, "Fievre"));
        asserter("Etat MALADE après maladie", v.getEtat() == EtatSante.MALADE);
        asserter("1 événement sanitaire", v.getHistoriqueSanitaire().size() == 1);

        // Guérison
        v.enregistrerEvenementSanitaire(new EvenementSanitaire(TypeEvenement.GUERISON, "Retabli"));
        asserter("Etat SAIN après guérison", v.getEtat() == EtatSante.SAIN);

        // Prise de poids
        v.enregistrerEvenementSanitaire(new EvenementSanitaire(TypeEvenement.PRISE_DE_POIDS, "Pesee", 620.0));
        asserter("Poids mis à jour", v.getPoids() == 620.0);

        // Programme alimentaire
        g.definirProgAlim(ze, new ProgAlimentaire("Foin", 8.0, 3));
        asserter("Programme défini", ze.getProgAlim() != null);
        asserter("Quantité journalière = 24 kg", ze.getProgAlim().getQuantiteJournaliere() == 24.0);

        // Animaux malades
        Animal p = new Animal(new EspeceAnim(TypeAnimal.VOLAILLE, "Poule"), 1, 2.0, EtatSante.MALADE);
        ze.addAnimal(p);
        asserter("1 animal malade/quarantaine", ze.getNbAnimauxMalades() == 1);
    }

    // ================================================================
    // Scénario 4 — Capteurs et relevés
    // ================================================================
    static void scenario4_Capteurs() {
        titre("SCENARIO 4 : Capteurs");

        Ferme ferme = new Ferme("TestFerme4");
        Gestionnaire g = new Gestionnaire(ferme);
        g.ajouterZone("ZC", TypeZone.CULTURE);
        Zone zc = ferme.getZones().get(0);

        // Capteur environnemental dans les seuils normaux
        Cap_env captOk = new Cap_env("CE_OK", zc, Status.ACTIF, 20.0, 50.0, 10.0);
        captOk.configurerSeuils(0, 40);   // temp 20 → INFO
        g.ajouterCapteur(zc, captOk);
        Releve r1 = ferme.effectuerReleve(captOk);
        asserter("Relevé INFO dans seuils", r1 != null && r1.getNiveauReleve() == Niveau_gravite.INFO);

        // Capteur hors seuil → alerte CRITIQUE
        Cap_env captHors = new Cap_env("CE_HORS", zc, Status.ACTIF, 50.0, 50.0, 10.0);
        captHors.configurerSeuils(0, 40);   // temp 50 > max 40 → CRITIQUE
        g.ajouterCapteur(zc, captHors);
        Releve r2 = ferme.effectuerReleve(captHors);
        asserter("Relevé CRITIQUE hors seuil", r2 != null && r2.getNiveauReleve() == Niveau_gravite.CRITIQUE);
        asserter("Alerte générée", !ferme.getAlertes().isEmpty());

        // Capteur GPS
        Capteur_GPS gps = new Capteur_GPS("GPS_T", zc, Status.ACTIF, new PositionGeographique(36.0, 3.0));
        g.ajouterCapteur(zc, gps);
        asserter("GPS position latitude", gps.getPosition().getLatitude() == 36.0);
        gps.updatePosition(new PositionGeographique(37.0, 4.0));
        asserter("GPS position mise à jour", gps.getPosition().getLatitude() == 37.0);

        // Suspension d'un capteur → relevé ignoré
        g.changerStatusCapteur(captOk, Status.SUSPENDU);
        Releve r3 = ferme.effectuerReleve(captOk);
        asserter("Relevé ignoré si capteur suspendu", r3 == null);

        System.out.println(ferme.tableauDeBordCapteurs());
    }

    // ================================================================
    // Scénario 5 — Alertes et filtres
    // ================================================================
    static void scenario5_AlertesEtFiltres() {
        titre("SCENARIO 5 : Alertes & filtres");

        Ferme ferme = new Ferme("TestFerme5");
        Gestionnaire g = new Gestionnaire(ferme);
        g.ajouterZone("ZC", TypeZone.CULTURE);
        Zone zc = ferme.getZones().get(0);

        // Générer 2 alertes : 1 ENV, 1 BIO
        Cap_env captEnv = new Cap_env("CE_A", zc, Status.ACTIF, 99.0, 50.0, 10.0);
        captEnv.configurerSeuils(0, 40);
        g.ajouterCapteur(zc, captEnv);
        ferme.effectuerReleve(captEnv); // 99 > 40 → CRITIQUE

        g.ajouterZone("ZE", TypeZone.ELEVAGE);
        Zone ze = ferme.getZones().get(1);
        Cap_biometrique captBio = new Cap_biometrique("CB_A", ze, Status.ACTIF, 42.0, 80.0);
        captBio.configurerSeuils(37.5, 39.5); // 42 > 39.5 → CRITIQUE
        g.ajouterCapteur(ze, captBio);
        ferme.effectuerReleve(captBio);

        asserter("2 alertes générées", ferme.getAlertes().size() == 2);

        // Filtre par type ENV
        List<Alerte> filtEnv = ferme.filtrer(null, TypeCapteur.ENV, null, null, null);
        asserter("Filtre ENV donne 1 alerte", filtEnv.size() == 1);

        // Filtre par niveau CRITIQUE
        List<Alerte> filtCrit = ferme.filtrer(null, null, Niveau_gravite.CRITIQUE, null, null);
        asserter("Filtre CRITIQUE donne 2 alertes", filtCrit.size() == 2);

        // Filtre par zone
        List<Alerte> filtZone = ferme.filtrer(zc, null, null, null, null);
        asserter("Filtre par zone ZC donne 1 alerte", filtZone.size() == 1);

        // Filtre par date — aujourd'hui inclus
        List<Alerte> filtDate = ferme.filtrer(null, null, null,
                LocalDate.now(), LocalDate.now());
        asserter("Filtre par date aujourd'hui donne 2 alertes", filtDate.size() == 2);

        // Filtre par date future → 0
        List<Alerte> filtFutur = ferme.filtrer(null, null, null,
                LocalDate.now().plusDays(1), null);
        asserter("Filtre date future donne 0", filtFutur.isEmpty());

        // Acquittement / suppression
        Alerte a1 = ferme.getAlertes().get(0);
        g.acquitterAlerte(a1);
        asserter("Alerte acquittée", a1.getStatut() == Alerte.StatutAlerte.ACQUITTEE);
        asserter("Plus active après acquittement", !a1.isActive());

        Alerte a2 = ferme.getAlertes().get(1);
        g.supprimerAlerte(a2);
        asserter("Alerte supprimée", a2.getStatut() == Alerte.StatutAlerte.SUPPRIMEE);

        g.afficherAlertesActives(); // doit afficher "Aucune alerte active."
    }

    // ================================================================
    // Scénario 6 — Filtre des relevés par date (ReleveSpecifications)
    // ================================================================
    static void scenario6_FiltreReleves() {
        titre("SCENARIO 6 : Filtre des relevés par date");

        Ferme ferme = new Ferme("TestFerme6");
        Gestionnaire g = new Gestionnaire(ferme);
        g.ajouterZone("ZC", TypeZone.CULTURE);
        Zone zc = ferme.getZones().get(0);

        Cap_env c = new Cap_env("CE_F", zc, Status.ACTIF, 20.0, 50.0, 5.0);
        c.configurerSeuils(0, 40);
        g.ajouterCapteur(zc, c);

        // 3 relevés : tous today
        ferme.effectuerReleve(c);
        ferme.effectuerReleve(c);
        ferme.effectuerReleve(c);
        asserter("3 relevés enregistrés", c.getHistorique().size() == 3);

        // Filtre today → 3
        List<Releve> today = ReleveSpecifications.filtrer(c.getHistorique(),
                LocalDate.now(), LocalDate.now());
        asserter("Filtre today donne 3", today.size() == 3);

        // Filtre hier → 0
        List<Releve> hier = ReleveSpecifications.filtrer(c.getHistorique(),
                null, LocalDate.now().minusDays(1));
        asserter("Filtre hier donne 0", hier.isEmpty());

        // Filtre demain → 0
        List<Releve> demain = ReleveSpecifications.filtrer(c.getHistorique(),
                LocalDate.now().plusDays(1), null);
        asserter("Filtre demain donne 0", demain.isEmpty());

        // Filtre null/null → tout
        List<Releve> tous = ReleveSpecifications.filtrer(c.getHistorique(), null, null);
        asserter("Filtre null/null donne tout", tous.size() == 3);

        // Filtre niveau INFO
        List<Releve> infoOnly = ReleveSpecifications.filtrer(c.getHistorique(),
                null, null, Niveau_gravite.INFO);
        asserter("Filtre INFO donne 3", infoOnly.size() == 3);

        // Filtre niveau CRITIQUE → 0 (capteur dans seuils)
        List<Releve> critOnly = ReleveSpecifications.filtrer(c.getHistorique(),
                null, null, Niveau_gravite.CRITIQUE);
        asserter("Filtre CRITIQUE donne 0", critOnly.isEmpty());

        System.out.println("  Extrait relevés filtrés : " + today);
    }

    // ================================================================
    // Scénario 7 — Production
    // ================================================================
    static void scenario7_Production() {
        titre("SCENARIO 7 : Production");

        Ferme ferme = new Ferme("TestFerme7");
        Gestionnaire g = new Gestionnaire(ferme);
        g.ajouterZone("Etable", TypeZone.ELEVAGE);
        Zone ze = ferme.getZones().get(0);

        g.enregistrerProduction(ze, new Prod(100.0, TypeProd.LAIT));
        g.enregistrerProduction(ze, new Prod(110.0, TypeProd.LAIT));

        asserter("2 productions enregistrées", ze.getProductions().size() == 2);
        asserter("Première production = 100 L", ze.getProductions().get(0).getVal() == 100.0);
    }

    // ================================================================
    // Scénario 8 — Changement de statut et impact sur relevés
    // ================================================================
    static void scenario8_StatusCapteurEtReleve() {
        titre("SCENARIO 8 : Statut capteur & relevés");

        Ferme ferme = new Ferme("TestFerme8");
        Gestionnaire g = new Gestionnaire(ferme);
        g.ajouterZone("ZC", TypeZone.CULTURE);
        Zone zc = ferme.getZones().get(0);

        Cap_env c = new Cap_env("CE_S", zc, Status.ACTIF, 20.0, 60.0, 5.0);
        c.configurerSeuils(0, 40);
        g.ajouterCapteur(zc, c);

        Releve r1 = ferme.effectuerReleve(c);
        asserter("Relevé OK si ACTIF", r1 != null);

        g.changerStatusCapteur(c, Status.DEFAILLANT);
        asserter("Statut DEFAILLANT", c.getStatus() == Status.DEFAILLANT);
        Releve r2 = ferme.effectuerReleve(c);
        asserter("Relevé null si DEFAILLANT", r2 == null);

        g.changerStatusCapteur(c, Status.ACTIF);
        Releve r3 = ferme.effectuerReleve(c);
        asserter("Relevé OK après réactivation", r3 != null);

        // Historique : seuls les 2 relevés réussis sont enregistrés
        asserter("Historique contient 2 relevés", c.getHistorique().size() == 2);
    }

    // ================================================================
    // UTILITAIRES
    // ================================================================

    static void titre(String msg) {
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║  " + msg);
        System.out.println("╚═══════════════════════════════════════════════════╝");
    }

    static void asserter(String description, boolean condition) {
        if (condition) {
            System.out.println("  [OK]  " + description);
            ok++;
        } else {
            System.out.println("  [!!]  ECHEC : " + description);
            nok++;
        }
    }
}