package com.example.tpoop;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║              SIMULATION DE FERME PILOTÉE PAR JSON               ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * Charge farm_simulation.json, reconstruit la ferme, puis rejoue
 * chaque période de la timeline dans l'ordre chronologique.
 *
 * Pour chaque événement d'une période :
 *  • RELEVE_CAPTEUR   → injecte les valeurs dans le capteur puis déclenche
 *                       effectuerReleve() → alerte automatique si hors seuils
 *  • STATUT_CAPTEUR   → change le statut (ACTIF / SUSPENDU / DEFAILLANT)
 *  • EVENEMENT_SANITAIRE → enregistre sur l'animal ciblé
 *  • GPS_UPDATE       → met à jour la position du GPS
 *  • PRODUCTION       → enregistre une production sur la zone
 *  • STADE_CULTURE    → met à jour le stade de croissance d'une ZoneCulture
 *
 * Rapport final imprimé en console :
 *  - résumé des alertes par période
 *  - bilan global des capteurs
 *  - historique des relevés filtrés par plage de dates
 *  - état sanitaire des animaux
 */
@SuppressWarnings("unchecked")
public class SimulationTest {

    // ── état global ───────────────────────────────────────────────────
    private final Ferme ferme;
    private final Gestionnaire g;

    // index de lookup pour injecter les valeurs dans les capteurs
    private final Map<String, Capteurs>  capteurIndex  = new HashMap<>();
    // index zones par code (tel que défini dans le JSON)
    private final Map<String, Zone>      zoneIndex     = new HashMap<>();
    // index animaux par id numérique original du JSON
    private final Map<Integer, Animal>   animalIndex   = new HashMap<>();

    // compteurs assertion
    private int ok  = 0;
    private int nok = 0;

    // ──────────────────────────────────────────────────────────────────

    public SimulationTest(Ferme ferme) {
        this.ferme = ferme;
        this.g = new Gestionnaire(ferme);
    }

    // ================================================================
    // POINT D'ENTRÉE
    // ================================================================

    public static void main(String[] args) {
        // Chemin vers le JSON — peut être passé en argument ou relatif au projet
        String jsonPath = args.length > 0 ? args[0] : "farm_simulation.json";

        SimulationTest sim = new SimulationTest(new Ferme("SimFerme"));
        try {
            sim.run(Path.of(jsonPath));
        } catch (IOException e) {
            System.err.println("Impossible de lire " + jsonPath + " : " + e.getMessage());
            System.err.println("Assurez-vous que farm_simulation.json est dans le repertoire de travail.");
        }
    }

    // ================================================================
    // MOTEUR PRINCIPAL
    // ================================================================

    public void run(Path jsonFile) throws IOException {
        header("CHARGEMENT DU FICHIER : " + jsonFile.toAbsolutePath());
        Map<String, Object> root = MiniJson.parseFile(jsonFile);

        // 1. Construire la ferme depuis la section "ferme"
        Map<String, Object> fermeNode = MiniJson.obj(root, "ferme");
        chargerFerme(fermeNode);

        System.out.println("  Ferme            : " + ferme.getNom());
        System.out.println("  Zones chargees   : " + ferme.getZones().size());
        System.out.println("  Capteurs charges : " + ferme.getTousLesCapteurs().size());
        System.out.println("  Animaux indexes  : " + animalIndex.size());

        // 2. Rejouer la timeline
        List<Object> timeline = MiniJson.list(root, "timeline");
        for (Object periodeObj : timeline) {
            Map<String, Object> periode = (Map<String, Object>) periodeObj;
            rejouerPeriode(periode);
        }

        // 3. Rapport final
        rapportFinal();

        // 4. Résultat des assertions
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.printf ("║  Assertions : %3d OK  %3d ECHEC           ║%n", ok, nok);
        System.out.println("╚══════════════════════════════════════════╝");
    }

    // ================================================================
    // CHARGEMENT DE LA FERME DEPUIS LE JSON
    // ================================================================

    private void chargerFerme(Map<String, Object> fermeNode) {
        List<Object> zonesJson = MiniJson.list(fermeNode, "zones");
        for (Object zObj : zonesJson) {
            Map<String, Object> zNode = (Map<String, Object>) zObj;
            Zone zone = creerZone(zNode);
            if (zone == null) continue;

            // override du code généré automatiquement par le code JSON
            String codeJson = MiniJson.str(zNode, "code");
            zone.setCode(codeJson);
            zoneIndex.put(codeJson, zone);

            ferme.getZones().add(zone);

            // capteurs de la zone
            for (Object cObj : MiniJson.list(zNode, "capteurs")) {
                Map<String, Object> cNode = (Map<String, Object>) cObj;
                Capteurs capteur = creerCapteur(cNode, zone);
                if (capteur == null) continue;
                zone.ajouterCapteur(capteur);
                ferme.getTousLesCapteurs().add(capteur);
                capteurIndex.put(capteur.getCode(), capteur);
            }
        }
    }

    private Zone creerZone(Map<String, Object> zNode) {
        String nom    = MiniJson.str(zNode, "nom");
        String type   = MiniJson.str(zNode, "type");
        String status = MiniJson.str(zNode, "status");
        Status st = Status.valueOf(status);

        return switch (type) {
            case "CULTURE" -> {
                ZoneCulture zc = new ZoneCulture(nom, st,null);
                // cultures
                for (Object cObj : MiniJson.list(zNode, "cultures")) {
                    Map<String, Object> cNode = (Map<String, Object>) cObj;
                    Map<String, Object> exigNode = MiniJson.obj(cNode, "exigences");
                    ExigPedologiques exig = new ExigPedologiques(
                            MiniJson.dbl(exigNode, "phMin"),      MiniJson.dbl(exigNode, "phMax"),
                            MiniJson.dbl(exigNode, "humiditeMin"),MiniJson.dbl(exigNode, "humiditeMax"),
                            MiniJson.dbl(exigNode, "azoteMin"),   MiniJson.dbl(exigNode, "azoteMax"),
                            MiniJson.dbl(exigNode, "pluviMin"), MiniJson.dbl(exigNode, "pluviMax")
                    );
                    Culture culture = new Culture(
                            MiniJson.str(cNode, "nom"),
                            MiniJson.str(cNode, "datePlantation"),
                            MiniJson.str(cNode, "dateRecolte"),
                            StadeCroissance.valueOf(MiniJson.str(cNode, "stadeCroissance")),
                            exig
                    );
                    zc.setCulture(culture);
                }
                yield zc;
            }
            case "ELEVAGE" -> {
                TypeProd tp = TypeProd.valueOf(MiniJson.str(zNode, "typeProd"));
                ZoneElevage ze = new ZoneElevage(nom, st, TypeAnimal.valueOf(MiniJson.str(zNode, "typeAnimal")));
                // programme alimentaire
                Map<String, Object> progNode = MiniJson.obj(zNode, "progAlimentaire");
                if (!progNode.isEmpty()) {
                    ze.setProgAlim(new ProgAlimentaire(
                            MiniJson.str(progNode, "typeAliment"),
                            MiniJson.dbl(progNode, "quantiteParRepas"),
                            MiniJson.integer(progNode, "repasParJour")
                    ));
                }
                // animaux
                for (Object aObj : MiniJson.list(zNode, "animaux")) {
                    Map<String, Object> aNode = (Map<String, Object>) aObj;
                    int idJson = MiniJson.integer(aNode, "id");
                    EspeceAnim esp = new EspeceAnim(
                            TypeAnimal.valueOf(MiniJson.str(aNode, "typeAnimal")),
                            MiniJson.str(aNode, "espece")
                    );
                    Animal animal = new Animal(
                            esp,
                            MiniJson.integer(aNode, "age"),
                            MiniJson.dbl(aNode, "poids"),
                            EtatSante.valueOf(MiniJson.str(aNode, "etat"))
                    );
                    ze.addAnimal(animal);
                    animalIndex.put(idJson, animal);   // clé = id JSON logique
                }
                yield ze;
            }
            case "AQUA" -> {
                ZoneAqua za = new ZoneAqua(nom, st,"Sardine");
                za.setNbAnimaux(MiniJson.integer(zNode, "nbAnimaux"));
                // espece (fix bug ZoneAqua — champ jamais initialisé)
                // On passe par un setter ajouté ici si manquant, sinon on le set via réflexion
                // → on utilise la surcharge compatible de setEspece si elle existe
                setEspeceAqua(za, MiniJson.str(zNode, "espece"));
                // programme alimentaire
                Map<String, Object> progNode = MiniJson.obj(zNode, "progAlimentaire");
                if (!progNode.isEmpty()) {
                    za.setProgAlim(new ProgAlimentaire(
                            MiniJson.str(progNode, "typeAliment"),
                            MiniJson.dbl(progNode, "quantiteParRepas"),
                            MiniJson.integer(progNode, "repasParJour")
                    ));
                }
                yield za;
            }
            default -> {
                System.out.println("  [WARN] Type de zone inconnu : " + type);
                yield null;
            }
        };
    }

    /** Workaround pour le champ espece non initialisé dans ZoneAqua. */
    private void setEspeceAqua(ZoneAqua za, String espece) {
        try {
            var field = ZoneAqua.class.getDeclaredField("espece");
            field.setAccessible(true);
            field.set(za, espece);
        } catch (Exception e) {
            // silencieux si le champ est introuvable
        }
    }

    private Capteurs creerCapteur(Map<String, Object> cNode, Zone zone) {
        String code   = MiniJson.str(cNode, "code");
        String type   = MiniJson.str(cNode, "type");
        String status = MiniJson.str(cNode, "status");
        double sMin   = MiniJson.dbl(cNode, "seuilMin");
        double sMax   = MiniJson.dbl(cNode, "seuilMax");
        Status st     = Status.valueOf(status);

        Capteurs capteur = switch (type) {
            case "ENV"        -> new Cap_env(code, zone, st, 0, 0, 0);
            case "SOL"        -> new Cap_sol(code, zone, st, 0, 0, 0);
            case "AQUA"       -> new Cap_aqua(code, zone, st, 0, 0, 0);
            case "BIOMETRIQUE"-> new Cap_biometrique(code, zone, st, 0, 0);
            case "GPS" -> {
                double lat = MiniJson.dbl(cNode, "latitude");
                double lon = MiniJson.dbl(cNode, "longitude");
                yield new Capteur_GPS(code, zone, st, new PositionGeographique(lat, lon));
            }
            default -> {
                System.out.println("  [WARN] Type capteur inconnu : " + type);
                yield null;
            }
        };

        if (capteur != null && sMax > sMin) {
            capteur.configurerSeuils(sMin, sMax);
        }
        return capteur;
    }

    // ================================================================
    // REJOUER UNE PÉRIODE DE LA TIMELINE
    // ================================================================

    private void rejouerPeriode(Map<String, Object> periode) {
        String dateStr = MiniJson.str(periode, "periode");
        String label   = MiniJson.str(periode, "label");
        LocalDate date = LocalDate.parse(dateStr);

        header("PÉRIODE : " + dateStr + " — " + label);

        int alertesAvant = ferme.getAlertes().size();

        List<Object> evenements = MiniJson.list(periode, "evenements");
        for (Object evObj : evenements) {
            Map<String, Object> ev = (Map<String, Object>) evObj;
            String type = MiniJson.str(ev, "type");

            switch (type) {
                case "RELEVE_CAPTEUR"      -> traiterReleve(ev, dateStr);
                case "STATUT_CAPTEUR"      -> traiterStatutCapteur(ev);
                case "EVENEMENT_SANITAIRE" -> traiterEvenementSanitaire(ev, dateStr);
                case "GPS_UPDATE"          -> traiterGpsUpdate(ev);
                case "PRODUCTION"          -> traiterProduction(ev);
                case "STADE_CULTURE"       -> traiterStadeCulture(ev);
                default -> System.out.println("  [WARN] Evenement inconnu : " + type);
            }
        }

        int nouvellesAlertes = ferme.getAlertes().size() - alertesAvant;
        System.out.println("  → " + nouvellesAlertes + " nouvelle(s) alerte(s) generee(s)");

        // Assertions par période
        asserterPeriode(date, nouvellesAlertes);
    }

    // ── Handlers d'événements ────────────────────────────────────────

    /** Injecte les valeurs JSON dans le capteur puis déclenche un relevé. */
    private void traiterReleve(Map<String, Object> ev, String dateStr) {
        String code = MiniJson.str(ev, "capteurCode");
        Capteurs capteur = capteurIndex.get(code);
        if (capteur == null) {
            System.out.printf("  [WARN] Capteur '%s' introuvable%n", code);
            return;
        }

        Map<String, Object> valeurs = MiniJson.obj(ev, "valeurs");
        injecterValeurs(capteur, valeurs);

        Releve r = ferme.effectuerReleve(capteur);
        if (r == null) {
            System.out.printf("  [INFO] Capteur %s non actif — releve ignore%n", code);
        }
    }

    /**
     * Injecte les valeurs du JSON dans le bon type de capteur.
     * Chaque sous-classe expose des setters — on les appelle par nom de champ JSON.
     */
    private void injecterValeurs(Capteurs capteur, Map<String, Object> valeurs) {
        if (capteur instanceof Cap_env c) {
            if (valeurs.containsKey("temperature"))   c.setTemp(dbl(valeurs, "temperature"));
            if (valeurs.containsKey("humidite"))       c.setHumidity(dbl(valeurs, "humidite"));
            if (valeurs.containsKey("pluviometrie"))   c.setPluvi(dbl(valeurs, "pluviometrie"));
        } else if (capteur instanceof Cap_sol c) {
            if (valeurs.containsKey("azote"))          c.setAzote(dbl(valeurs, "azote"));
            if (valeurs.containsKey("humidite"))       c.setHumidity(dbl(valeurs, "humidite"));
            if (valeurs.containsKey("ph"))             c.setPh(dbl(valeurs, "ph"));
        } else if (capteur instanceof Cap_aqua c) {
            if (valeurs.containsKey("temperature"))    c.setTemp(dbl(valeurs, "temperature"));
            if (valeurs.containsKey("oxygene"))        c.setOxygen(dbl(valeurs, "oxygene"));
            if (valeurs.containsKey("ph"))             c.setPh(dbl(valeurs, "ph"));
        } else if (capteur instanceof Cap_biometrique c) {
            if (valeurs.containsKey("temperature_corporelle")) c.setTempCorporelle(dbl(valeurs, "temperature_corporelle"));
            if (valeurs.containsKey("activite_par_minute"))    c.setActivityPerMin(dbl(valeurs, "activite_par_minute"));
        } else if (capteur instanceof Capteur_GPS c) {
            if (valeurs.containsKey("latitude") && valeurs.containsKey("longitude"))
                c.updatePosition(new PositionGeographique(dbl(valeurs, "latitude"), dbl(valeurs, "longitude")));
        }
    }

    private void traiterStatutCapteur(Map<String, Object> ev) {
        String code    = MiniJson.str(ev, "capteurCode");
        String statut  = MiniJson.str(ev, "nouveauStatut");
        Capteurs capteur = capteurIndex.get(code);
        if (capteur == null) { System.out.printf("  [WARN] Capteur '%s' introuvable%n", code); return; }
        Status s = Status.valueOf(statut);
        g.changerStatusCapteur(capteur, s);
        System.out.printf("  [STATUT] %s → %s%n", code, s);
    }

    private void traiterEvenementSanitaire(Map<String, Object> ev, String dateStr) {
        int    animalId   = (int) ((Number) ev.get("animalId")).doubleValue();
        String typeEvt    = MiniJson.str(ev, "typeEvenement");
        String desc       = MiniJson.str(ev, "description");
        double valeur     = ev.containsKey("valeur") ? (double)((Number)ev.get("valeur")).doubleValue() : 0;

        Animal animal = animalIndex.get(animalId);
        if (animal == null) { System.out.printf("  [WARN] Animal id=%d introuvable%n", animalId); return; }

        TypeEvenement te = TypeEvenement.valueOf(typeEvt);
        animal.enregistrerEvenementSanitaire(new EvenementSanitaire(te, desc, valeur));
        System.out.printf("  [SANITAIRE] Animal #%d (%s) : %s — %s%n",
                animalId, animal.getEspece().getName(), te, desc);
    }

    private void traiterGpsUpdate(Map<String, Object> ev) {
        String code = MiniJson.str(ev, "capteurCode");
        double lat  = MiniJson.dbl(ev, "latitude");
        double lon  = MiniJson.dbl(ev, "longitude");
        Capteurs capteur = capteurIndex.get(code);
        if (capteur instanceof Capteur_GPS gps) {
            gps.updatePosition(new PositionGeographique(lat, lon));
            System.out.printf("  [GPS] %s → (%.4f, %.4f)%n", code, lat, lon);
        }
    }

    private void traiterProduction(Map<String, Object> ev) {
        String zoneCode = MiniJson.str(ev, "zoneCode");
        TypeProd tp     = TypeProd.valueOf(MiniJson.str(ev, "typeProd"));
        double   valeur = MiniJson.dbl(ev, "valeur");
        Zone zone = zoneIndex.get(zoneCode);
        if (zone == null) { System.out.printf("  [WARN] Zone '%s' introuvable%n", zoneCode); return; }
        g.enregistrerProduction(zone, new Prod(valeur, tp));
    }

    private void traiterStadeCulture(Map<String, Object> ev) {
        String zoneCode    = MiniJson.str(ev, "zoneCode");
        String nouveauStade = MiniJson.str(ev, "nouveauStade");
        Zone zone = zoneIndex.get(zoneCode);
        if (zone instanceof ZoneCulture zc) {
            StadeCroissance stade = StadeCroissance.valueOf(nouveauStade);
            g.mettreAJourStadeCroissance(zc, stade);
            System.out.printf("  [CULTURE] Zone %s → stade %s%n", zoneCode, stade);
        }
    }

    // ================================================================
    // ASSERTIONS PAR PÉRIODE
    // ================================================================

    private void asserterPeriode(LocalDate date, int nouvellesAlertes) {
        String dateStr = date.toString();
        switch (dateStr) {
            case "2026-05-01" -> {
                // Tous les capteurs actifs, conditions normales → 0 alertes
                asserter("05-01 : 0 alerte en conditions normales", nouvellesAlertes == 0);
                asserter("05-01 : capteur CE01 a un releve",
                        capteurIndex.get("CE01") != null &&
                                !capteurIndex.get("CE01").getHistorique().isEmpty());
            }
            case "2026-05-08" -> {
                // CE02 temp=33 vs seuil max=35 → AVERTISSEMENT (pas d'alerte si dans seuils stricts)
                // CB01 temp=39.2 vs seuil [37.5–39.5] → AVERTISSEMENT (>80% de la plage)
                asserter("05-08 : au moins 1 alerte (chaleur naissante)", nouvellesAlertes >= 1);
                // Stade culture mis à jour
                Zone zc1 = zoneIndex.get("ZC001");
                asserter("05-08 : stade culture = MATURITE",
                        zc1 instanceof ZoneCulture zc &&
                                !(zc.getCultures()==null )&&
                                zc.getCultures().getStadeCroiss() == StadeCroissance.MATURITE);
            }
            case "2026-05-15" -> {
                // CE01 temp=43 > seuil 40 → CRITIQUE → alerte
                // CB01 temp=40.1 > 39.5 → CRITIQUE → alerte
                // CA01 temp=32.5 < 35 mais approaching → check niveau
                // CS01 DEFAILLANT → son relevé est ignoré → pas de double-alerte
                asserter("05-15 : >= 2 alertes (pic chaleur)", nouvellesAlertes >= 2);
                // Animal #1 MALADE
                Animal a1 = animalIndex.get(1);
                asserter("05-15 : animal 1 MALADE", a1 != null && a1.getEtat() == EtatSante.MALADE);
                // Animal #4 EN_QUARANTAINE
                Animal a4 = animalIndex.get(4);
                asserter("05-15 : animal 4 EN_QUARANTAINE",
                        a4 != null && a4.getEtat() == EtatSante.EN_QUARANTAINE);
                // CS01 défaillant → relevé ignoré
                Capteurs cs01 = capteurIndex.get("CS01");
                asserter("05-15 : CS01 DEFAILLANT", cs01 != null && cs01.getStatus() == Status.DEFAILLANT);
            }
            case "2026-05-20" -> {
                // CS01 réactivé
                Capteurs cs01 = capteurIndex.get("CS01");
                asserter("05-20 : CS01 réactivé ACTIF", cs01 != null && cs01.getStatus() == Status.ACTIF);
                // Animal #1 guéri
                Animal a1 = animalIndex.get(1);
                asserter("05-20 : animal 1 SAIN après guérison", a1 != null && a1.getEtat() == EtatSante.SAIN);
                // Poids animal #1 = 545
                asserter("05-20 : poids animal 1 = 545", a1 != null && a1.getPoids() == 545.0);
                // Stade blé = RECOLTE
                Zone zc1 = zoneIndex.get("ZC001");
                asserter("05-20 : stade Ble = RECOLTE",
                        zc1 instanceof ZoneCulture zc &&
                                !(zc.getCultures()==null) &&
                                zc.getCultures().getStadeCroiss() == StadeCroissance.RECOLTE);
                // Production enregistrée
                asserter("05-20 : production ZC001 non vide",
                        zc1 != null && !zc1.getProductions().isEmpty());
            }
        }
    }

    // ================================================================
    // RAPPORT FINAL
    // ================================================================

    private void rapportFinal() {
        header("RAPPORT FINAL DE SIMULATION");

        // 1. Vue d'ensemble
        System.out.println(ferme.getVueEnsembleZones());

        // 2. Tableau de bord capteurs
        System.out.println(ferme.tableauDeBordCapteurs());

        // 3. Bilan alertes
        System.out.println("=== BILAN ALERTES ===");
        long actives     = ferme.getAlertes().stream().filter(Alerte::isActive).count();
        long critiques   = ferme.getAlertes().stream().filter(a -> a.getGravite() == Niveau_gravite.CRITIQUE).count();
        long avertissements = ferme.getAlertes().stream().filter(a -> a.getGravite() == Niveau_gravite.AVERTISSEMENT).count();
        System.out.printf("  Total alertes   : %d%n", ferme.getAlertes().size());
        System.out.printf("  Actives         : %d%n", actives);
        System.out.printf("  Critiques       : %d%n", critiques);
        System.out.printf("  Avertissements  : %d%n", avertissements);
        System.out.println("\n  Detail :");
        ferme.getAlertes().forEach(a -> System.out.println("    " + a));

        // 4. Historique des relevés CE01 filtré (toute la simulation)
        System.out.println("\n=== HISTORIQUE RELEVES CE01 (toute la periode) ===");
        Capteurs ce01 = capteurIndex.get("CE01");
        if (ce01 != null) {
            List<Releve> tous = ReleveSpecifications.filtrer(ce01.getHistorique(), null, null);
            tous.forEach(r -> System.out.println("  " + r));

            // Sous-filtre : uniquement la 2e quinzaine
            System.out.println("  -- Filtre : 2026-05-15 → 2026-05-20 --");
            List<Releve> quinzaine = ReleveSpecifications.filtrer(ce01.getHistorique(),
                    LocalDate.of(2026,5,15), LocalDate.of(2026,5,20));
            if (quinzaine.isEmpty()) System.out.println("    (aucun dans cet intervalle)");
            else quinzaine.forEach(r -> System.out.println("    " + r));
        }

        // 5. Etat sanitaire final des animaux
        System.out.println("\n=== ETAT SANITAIRE FINAL ===");
        for (Zone z : ferme.getZones()) {
            if (z instanceof ZoneElevage ze) {
                System.out.println("  Zone : " + ze.getName());
                ze.getAnimaux().forEach(a -> {
                    System.out.printf("    Animal #%d (%s) | Poids: %.1f kg | Etat: %s%n",
                            a.getID(), a.getEspece().getName(), a.getPoids(), a.getEtat());
                    a.displayHistoriqueSanitaire();
                });
            }
        }

        // 6. Productions par zone
        System.out.println("\n=== PRODUCTIONS ENREGISTREES ===");
        for (Zone z : ferme.getZones()) {
            if (!z.getProductions().isEmpty()) {
                System.out.println("  " + z.getName() + " [" + z.getCode() + "] :");
                z.getProductions().forEach(p -> System.out.println("    " + p));
            }
        }

        // Assertions finales sur le bilan global
        asserter("Bilan : au moins 3 alertes générées sur la simulation",
                ferme.getAlertes().size() >= 3);
        asserter("Bilan : CE01 a 4 relevés (1 par période)",
                ce01 != null && ce01.getHistorique().size() == 4);
        asserter("Bilan : productions ZE001 non vide",
                zoneIndex.get("ZE001") != null &&
                        !zoneIndex.get("ZE001").getProductions().isEmpty());
    }

    // ================================================================
    // UTILITAIRES
    // ================================================================

    private double dbl(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v == null ? 0.0 : ((Number) v).doubleValue();
    }

    private void asserter(String desc, boolean condition) {
        if (condition) { System.out.println("  [OK]  " + desc); ok++; }
        else           { System.out.println("  [!!]  ECHEC : " + desc); nok++; }
    }

    private void header(String msg) {
        int w = Math.max(msg.length() + 4, 60);
        String line = "═".repeat(w);
        System.out.println("\n╔" + line + "╗");
        System.out.printf("║  %-" + (w-2) + "s║%n", msg);
        System.out.println("╚" + line + "╝");
    }
}