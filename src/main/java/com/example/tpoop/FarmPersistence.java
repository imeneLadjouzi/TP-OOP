package com.example.tpoop;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * FarmPersistence — sérialisation / désérialisation JSON de toute la ferme.
 *
 * Deux modes (choisis au démarrage par l'utilisateur) :
 *   • SEED  → charger farm_data.json  (données de départ fixes)
 *   • SAVE  → charger farm_save.json  (dernière sauvegarde de session)
 *
 * Méthodes publiques :
 *   load(Path)  → reconstruit un objet Ferme complet
 *   save(Ferme, Path) → écrit l'état courant dans un fichier JSON
 */
@SuppressWarnings("unchecked")
public class FarmPersistence {

    // ─────────────────────────────────────────────────────────────────
    // CHARGEMENT
    // ─────────────────────────────────────────────────────────────────

    public static Ferme load(Path path) throws IOException {
        Map<String, Object> root = MiniJson.parseFile(path);

        Map<String, Object> fermeNode = MiniJson.obj(root, "ferme");
        String nomFerme = MiniJson.str(fermeNode, "nom");
        if (nomFerme == null || nomFerme.isBlank()) nomFerme = "Ferme Principale";
        Ferme ferme = new Ferme(nomFerme);

        List<Object> zonesRaw = MiniJson.list(root, "zones");
        for (Object zRaw : zonesRaw) {
            Map<String, Object> zNode = (Map<String, Object>) zRaw;
            loadZone(ferme, zNode);
        }
        return ferme;
    }

    // ── Zone dispatcher ───────────────────────────────────────────────

    private static void loadZone(Ferme ferme, Map<String, Object> zNode) {
        String type   = MiniJson.str(zNode, "type");
        String nom    = MiniJson.str(zNode, "nom");
        String statStr = MiniJson.str(zNode, "status");
        Status status = parseStatus(statStr);

        Zone zone;
        switch (type) {
            case "CULTURE" -> zone = loadZoneCulture(nom, status, ferme, zNode);
            case "ELEVAGE" -> zone = loadZoneElevage(nom, status, ferme, zNode);
            case "AQUA"    -> zone = loadZoneAqua(nom, status, ferme, zNode);
            default        -> { System.err.println("Type de zone inconnu: " + type); return; }
        }

        // Override code generated automatically
        String savedCode = MiniJson.str(zNode, "code");
        if (savedCode != null && !savedCode.isBlank()) zone.setCode(savedCode);

        // Productions
        for (Object pRaw : MiniJson.list(zNode, "productions")) {
            Map<String, Object> pNode = (Map<String, Object>) pRaw;
            double val  = MiniJson.dbl(pNode, "valeur");
            String tp   = MiniJson.str(pNode, "typeProd");
            try { zone.enregistrerProduction(new Prod(val, TypeProd.valueOf(tp))); }
            catch (Exception e) { System.err.println("TypeProd inconnu: " + tp); }
        }

        // Capteurs
        loadCapteurs(ferme, zone, MiniJson.list(zNode, "capteurs"));

        ferme.getZones().add(zone);
    }

    // ── Zone Culture ──────────────────────────────────────────────────

    private static ZoneCulture loadZoneCulture(String nom, Status status,
                                               Ferme ferme, Map<String, Object> zNode) {
        ZoneCulture zc = new ZoneCulture(nom, status, null, ferme);

        Map<String, Object> cultNode = MiniJson.obj(zNode, "culture");
        if (!cultNode.isEmpty()) {
            String cNom      = MiniJson.str(cultNode, "nom");
            String datePlant = MiniJson.str(cultNode, "datePlantation");
            String dateRec   = MiniJson.str(cultNode, "dateRecolte");
            String stadeStr  = MiniJson.str(cultNode, "stadeCroissance");
            StadeCroissance stade = stadeStr != null ? StadeCroissance.valueOf(stadeStr) : StadeCroissance.GERMINATION;

            ExigPedologiques ep = null;
            Map<String, Object> epNode = MiniJson.obj(cultNode, "exigPedologiques");
            if (!epNode.isEmpty()) {
                ep = new ExigPedologiques(
                        MiniJson.dbl(epNode, "phMin"), MiniJson.dbl(epNode, "phMax"),
                        MiniJson.dbl(epNode, "humiditeMin"), MiniJson.dbl(epNode, "humiditeMax"),
                        MiniJson.dbl(epNode, "azoteMin"), MiniJson.dbl(epNode, "azoteMax"));
            }
            zc.setCulture(new Culture(cNom, datePlant, dateRec, stade, ep));
        }
        return zc;
    }

    // ── Zone Élevage ──────────────────────────────────────────────────

    private static ZoneElevage loadZoneElevage(String nom, Status status,
                                               Ferme ferme, Map<String, Object> zNode) {
        String taStr = MiniJson.str(zNode, "typeAnimal");
        TypeAnimal ta = taStr != null ? TypeAnimal.valueOf(taStr) : TypeAnimal.RUMINANT;
        ZoneElevage ze = new ZoneElevage(nom, status, ta, ferme);

        Map<String, Object> paNode = MiniJson.obj(zNode, "progAlimentaire");
        if (!paNode.isEmpty()) {
            ze.setProgAlim(new ProgAlimentaire(
                    MiniJson.str(paNode, "typeAliment"),
                    MiniJson.dbl(paNode, "quantiteParRepas"),
                    MiniJson.integer(paNode, "repasParJour")));
        }

        for (Object aRaw : MiniJson.list(zNode, "animaux")) {
            Map<String, Object> aNode = (Map<String, Object>) aRaw;
            String especeNom  = MiniJson.str(aNode, "especeNom");
            String typeAStr   = MiniJson.str(aNode, "typeAnimal");
            int    age        = MiniJson.integer(aNode, "age");
            double poids      = MiniJson.dbl(aNode, "poids");
            String etatStr    = MiniJson.str(aNode, "etat");
            TypeAnimal typeA  = typeAStr != null ? TypeAnimal.valueOf(typeAStr) : ta;
            EtatSante etat    = etatStr  != null ? EtatSante.valueOf(etatStr)   : EtatSante.SAIN;
            ze.addAnimal(new Animal(new EspeceAnim(typeA, especeNom), age, poids, etat));
        }
        return ze;
    }

    // ── Zone Aqua ─────────────────────────────────────────────────────

    private static ZoneAqua loadZoneAqua(String nom, Status status,
                                         Ferme ferme, Map<String, Object> zNode) {
        String espece = MiniJson.str(zNode, "espece");
        if (espece == null) espece = "Inconnu";
        ZoneAqua za = new ZoneAqua(nom, status, espece, ferme);
        za.setNbAnimaux(MiniJson.integer(zNode, "nbAnimaux"));

        Map<String, Object> paNode = MiniJson.obj(zNode, "progAlimentaire");
        if (!paNode.isEmpty()) {
            za.setProgAlim(new ProgAlimentaire(
                    MiniJson.str(paNode, "typeAliment"),
                    MiniJson.dbl(paNode, "quantiteParRepas"),
                    MiniJson.integer(paNode, "repasParJour")));
        }
        return za;
    }

    // ── Capteurs ──────────────────────────────────────────────────────

    private static void loadCapteurs(Ferme ferme, Zone zone, List<Object> capteursRaw) {
        for (Object cRaw : capteursRaw) {
            Map<String, Object> cNode = (Map<String, Object>) cRaw;
            String type    = MiniJson.str(cNode, "type");
            String code    = MiniJson.str(cNode, "code");
            String statStr = MiniJson.str(cNode, "status");
            Status status  = parseStatus(statStr);
            Map<String, Object> seuilsNode = MiniJson.obj(cNode, "seuils");

            Capteurs cap = null;
            switch (type) {
                case "ENV" -> {
                    Cap_env c = new Cap_env(zone, status);
                    if (!seuilsNode.isEmpty())
                        c.configurer(
                                MiniJson.dbl(seuilsNode, "tempMin"), MiniJson.dbl(seuilsNode, "tempMax"),
                                MiniJson.dbl(seuilsNode, "humMin"),  MiniJson.dbl(seuilsNode, "humMax"),
                                MiniJson.dbl(seuilsNode, "pluviMin"),MiniJson.dbl(seuilsNode, "pluviMax"));
                    cap = c;
                }
                case "SOL" -> {
                    Cap_sol c = new Cap_sol(zone, status);
                    if (!seuilsNode.isEmpty()) {
                        c.configurerTemp( MiniJson.dbl(seuilsNode, "phMin"),    MiniJson.dbl(seuilsNode, "phMax"));
                        c.configurerHum(  MiniJson.dbl(seuilsNode, "humMin"),   MiniJson.dbl(seuilsNode, "humMax"));
                        c.configurerPh(   MiniJson.dbl(seuilsNode, "azoteMin"), MiniJson.dbl(seuilsNode, "azoteMax"));
                    }
                    cap = c;
                }
                case "BIOM" -> {
                    Cap_biometrique c = new Cap_biometrique(zone, status);
                    if (!seuilsNode.isEmpty())
                        c.configurer(
                                MiniJson.dbl(seuilsNode, "tempMin"), MiniJson.dbl(seuilsNode, "tempMax"),
                                MiniJson.dbl(seuilsNode, "actMin"),  MiniJson.dbl(seuilsNode, "actMax"));
                    cap = c;
                }
                case "AQUA" -> {
                    Cap_aqua c = new Cap_aqua(zone, status);
                    if (!seuilsNode.isEmpty())
                        c.configurer(
                                MiniJson.dbl(seuilsNode, "tempMin"), MiniJson.dbl(seuilsNode, "tempMax"),
                                MiniJson.dbl(seuilsNode, "oxyMin"),  MiniJson.dbl(seuilsNode, "oxyMax"),
                                MiniJson.dbl(seuilsNode, "phMin"),   MiniJson.dbl(seuilsNode, "phMax"));
                    cap = c;
                }
                case "GPS" -> {
                    // Trouver l'animal associé
                    Animal animal = null;
                    if (zone instanceof ZoneElevage ze && !ze.getAnimaux().isEmpty()) {
                        int idx = MiniJson.integer(cNode, "animalIndex");
                        idx = Math.min(idx, ze.getAnimaux().size() - 1);
                        animal = ze.getAnimaux().get(idx);
                    } else {
                        animal = new Animal(new EspeceAnim(TypeAnimal.VOLAILLE, "Inconnu"), 1, 1.0, EtatSante.SAIN);
                    }
                    Capteur_GPS c = new Capteur_GPS(zone, status, animal);
                    if (!seuilsNode.isEmpty())
                        c.configurer(
                                MiniJson.dbl(seuilsNode, "lonMin"), MiniJson.dbl(seuilsNode, "lonMax"),
                                MiniJson.dbl(seuilsNode, "latMin"), MiniJson.dbl(seuilsNode, "latMax"));
                    cap = c;
                }
                default -> System.err.println("Type capteur inconnu: " + type);
            }

            if (cap == null) continue;

            // Override code généré
            // if (code != null && !code.isBlank()) cap.setCode(code);

            // Relire l'historique sauvegardé (uniquement dans farm_save.json)
            for (Object rRaw : MiniJson.list(cNode, "historique_releves")) {
                Map<String, Object> rNode = (Map<String, Object>) rRaw;
                Map<String, Object> valeurs = MiniJson.obj(rNode, "valeurs");
                String niveauStr = MiniJson.str(rNode, "niveau");
                Niveau_gravite niveau = niveauStr != null ? Niveau_gravite.valueOf(niveauStr) : Niveau_gravite.INFO;
                cap.addReleve(new Releve(cap, valeurs, niveau));
            }

            zone.ajouterCapteur(cap);
            ferme.getTousLesCapteurs().add(cap);
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // SAUVEGARDE
    // ─────────────────────────────────────────────────────────────────

    public static void save(Ferme ferme, Path path) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"ferme\": { \"nom\": ").append(jsonStr(ferme.getNom())).append(" },\n");
        sb.append("  \"zones\": [\n");

        List<Zone> zones = ferme.getZones();
        for (int i = 0; i < zones.size(); i++) {
            Zone z = zones.get(i);
            sb.append(serializeZone(z, 4));
            if (i < zones.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n}\n");
        Files.writeString(path, sb.toString());
    }

    // ── Sérialisation Zone ────────────────────────────────────────────

    private static String serializeZone(Zone z, int indent) {
        String pad = " ".repeat(indent);
        StringBuilder sb = new StringBuilder();
        sb.append(pad).append("{\n");
        sb.append(pad).append("  \"code\": ").append(jsonStr(z.getCode())).append(",\n");
        sb.append(pad).append("  \"nom\": ").append(jsonStr(z.getName())).append(",\n");
        sb.append(pad).append("  \"status\": ").append(jsonStr(z.getStatus().name())).append(",\n");

        if (z instanceof ZoneCulture zc) {
            sb.append(pad).append("  \"type\": \"CULTURE\",\n");
            sb.append(pad).append("  \"culture\": ").append(serializeCulture(zc.getCultures(), indent + 2)).append(",\n");
        } else if (z instanceof ZoneElevage ze) {
            sb.append(pad).append("  \"type\": \"ELEVAGE\",\n");
            sb.append(pad).append("  \"typeAnimal\": ").append(jsonStr(ze.getTypeAnimal().name())).append(",\n");
            sb.append(pad).append("  \"progAlimentaire\": ").append(serializeProgAlim(ze.getProgAlim(), indent + 2)).append(",\n");
            sb.append(pad).append("  \"animaux\": ").append(serializeAnimaux(ze.getAnimaux(), indent + 2)).append(",\n");
        } else if (z instanceof ZoneAqua za) {
            sb.append(pad).append("  \"type\": \"AQUA\",\n");
            sb.append(pad).append("  \"espece\": ").append(jsonStr(za.getEspece())).append(",\n");
            sb.append(pad).append("  \"nbAnimaux\": ").append(za.getNbAnimaux()).append(",\n");
            sb.append(pad).append("  \"progAlimentaire\": ").append(serializeProgAlim(za.getProgAlim(), indent + 2)).append(",\n");
        }

        sb.append(pad).append("  \"productions\": ").append(serializeProductions(z.getProductions(), indent + 2)).append(",\n");
        sb.append(pad).append("  \"capteurs\": ").append(serializeCapteurs(z.getCapteurs(), indent + 2)).append("\n");
        sb.append(pad).append("}");
        return sb.toString();
    }

    // ── Sérialisation Culture ─────────────────────────────────────────

    private static String serializeCulture(Culture c, int indent) {
        if (c == null) return "{}";
        String pad = " ".repeat(indent);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(pad).append("  \"nom\": ").append(jsonStr(c.getNom())).append(",\n");
        sb.append(pad).append("  \"datePlantation\": ").append(jsonStr(c.getDatePlantation())).append(",\n");
        sb.append(pad).append("  \"dateRecolte\": ").append(jsonStr(c.getDateRecolte())).append(",\n");
        sb.append(pad).append("  \"stadeCroissance\": ").append(jsonStr(c.getStadeCroiss().name())).append(",\n");
        if (c.getExigPed() != null) {
            ExigPedologiques ep = c.getExigPed();
            sb.append(pad).append("  \"exigPedologiques\": {\n");
            sb.append(pad).append("    \"phMin\": ").append(ep.getPhMin()).append(", \"phMax\": ").append(ep.getPhMax()).append(",\n");
            sb.append(pad).append("    \"humiditeMin\": ").append(ep.getHumMin()).append(", \"humiditeMax\": ").append(ep.getHumMax()).append(",\n");
            sb.append(pad).append("    \"azoteMin\": ").append(ep.getAzoteMin()).append(", \"azoteMax\": ").append(ep.getAzoteMax()).append("\n");
            sb.append(pad).append("  }\n");
        } else {
            sb.append(pad).append("  \"exigPedologiques\": {}\n");
        }
        sb.append(pad).append("}");
        return sb.toString();
    }

    // ── Sérialisation ProgAlim ────────────────────────────────────────

    private static String serializeProgAlim(ProgAlimentaire pa, int indent) {
        if (pa == null) return "{}";
        String pad = " ".repeat(indent);
        return "{\n" +
                pad + "  \"typeAliment\": " + jsonStr(pa.getTypeAliment()) + ",\n" +
                pad + "  \"quantiteParRepas\": " + pa.getQuantiteParRepas() + ",\n" +
                pad + "  \"repasParJour\": " + pa.getRepasParJour() + "\n" +
                pad + "}";
    }

    // ── Sérialisation Animaux ─────────────────────────────────────────

    private static String serializeAnimaux(List<Animal> animaux, int indent) {
        if (animaux == null || animaux.isEmpty()) return "[]";
        String pad = " ".repeat(indent);
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < animaux.size(); i++) {
            Animal a = animaux.get(i);
            sb.append(pad).append("  {\n");
            sb.append(pad).append("    \"especeNom\": ").append(jsonStr(a.getEspece().getName())).append(",\n");
            sb.append(pad).append("    \"typeAnimal\": ").append(jsonStr(a.getEspece().getType().name())).append(",\n");
            sb.append(pad).append("    \"age\": ").append(a.age).append(",\n");
            sb.append(pad).append("    \"poids\": ").append(a.getPoids()).append(",\n");
            sb.append(pad).append("    \"etat\": ").append(jsonStr(a.getEtat().name())).append("\n");
            sb.append(pad).append("  }");
            if (i < animaux.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append(pad).append("]");
        return sb.toString();
    }

    // ── Sérialisation Productions ─────────────────────────────────────

    private static String serializeProductions(List<Prod> prods, int indent) {
        if (prods == null || prods.isEmpty()) return "[]";
        String pad = " ".repeat(indent);
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < prods.size(); i++) {
            Prod p = prods.get(i);
            sb.append(pad).append("  { \"valeur\": ").append(p.getVal())
                    .append(", \"typeProd\": ").append(jsonStr(p.getProd().name())).append(" }");
            if (i < prods.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append(pad).append("]");
        return sb.toString();
    }

    // ── Sérialisation Capteurs ────────────────────────────────────────

    private static String serializeCapteurs(List<Capteurs> capteurs, int indent) {
        if (capteurs == null || capteurs.isEmpty()) return "[]";
        String pad = " ".repeat(indent);
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < capteurs.size(); i++) {
            Capteurs c = capteurs.get(i);
            sb.append(pad).append("  {\n");
            sb.append(pad).append("    \"code\": ").append(jsonStr(c.getCode())).append(",\n");
            sb.append(pad).append("    \"status\": ").append(jsonStr(c.getStatus().name())).append(",\n");
            sb.append(pad).append("    \"type\": ").append(jsonStr(capteurTypeStr(c))).append(",\n");
            sb.append(serializeSeuilsCapteur(c, indent + 4));

            // Historique des relevés
            sb.append(pad).append("    \"historique_releves\": ");
            sb.append(serializeReleves(c.getHistorique(), indent + 4));
            sb.append("\n");
            sb.append(pad).append("  }");
            if (i < capteurs.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append(pad).append("]");
        return sb.toString();
    }

    private static String capteurTypeStr(Capteurs c) {
        if (c instanceof Cap_env)         return "ENV";
        if (c instanceof Cap_sol)         return "SOL";
        if (c instanceof Cap_biometrique) return "BIOM";
        if (c instanceof Cap_aqua)        return "AQUA";
        if (c instanceof Capteur_GPS)     return "GPS";
        return "UNKNOWN";
    }

    private static String serializeSeuilsCapteur(Capteurs c, int indent) {
        String pad = " ".repeat(indent);
        StringBuilder sb = new StringBuilder();
        sb.append(pad).append("\"seuils\": {");
        if (c instanceof Cap_env ce) {
            sb.append(" \"tempMin\":").append(ce.seuils.temp.getMin())
                    .append(", \"tempMax\":").append(ce.seuils.temp.getMax())
                    .append(", \"humMin\":").append(ce.seuils.humidity.getMin())
                    .append(", \"humMax\":").append(ce.seuils.humidity.getMax())
                    .append(", \"pluviMin\":").append(ce.seuils.pluvi.getMin())
                    .append(", \"pluviMax\":").append(ce.seuils.pluvi.getMax());
        } else if (c instanceof Cap_sol cs) {
            sb.append(" \"phMin\":").append(cs.seuils.ph.getMin())
                    .append(", \"phMax\":").append(cs.seuils.ph.getMax())
                    .append(", \"humMin\":").append(cs.seuils.humidite.getMin())
                    .append(", \"humMax\":").append(cs.seuils.humidite.getMax())
                    .append(", \"azoteMin\":").append(cs.seuils.azote.getMin())
                    .append(", \"azoteMax\":").append(cs.seuils.azote.getMax());
        } else if (c instanceof Cap_biometrique cb) {
            sb.append(" \"tempMin\":").append(cb.seuils.temp_corporelle.getMin())
                    .append(", \"tempMax\":").append(cb.seuils.temp_corporelle.getMax())
                    .append(", \"actMin\":").append(cb.seuils.activity_per_min.getMin())
                    .append(", \"actMax\":").append(cb.seuils.activity_per_min.getMax());
        } else if (c instanceof Cap_aqua ca) {
            sb.append(" \"tempMin\":").append(ca.seuils.temp.getMin())
                    .append(", \"tempMax\":").append(ca.seuils.temp.getMax())
                    .append(", \"oxyMin\":").append(ca.seuils.oxygen.getMin())
                    .append(", \"oxyMax\":").append(ca.seuils.oxygen.getMax())
                    .append(", \"phMin\":").append(ca.seuils.ph.getMin())
                    .append(", \"phMax\":").append(ca.seuils.ph.getMax());
        } else if (c instanceof Capteur_GPS cg) {
            sb.append(" \"lonMin\":").append(cg.longitude.getMin())
                    .append(", \"lonMax\":").append(cg.longitude.getMax())
                    .append(", \"latMin\":").append(cg.latitude.getMin())
                    .append(", \"latMax\":").append(cg.latitude.getMax());
        }
        sb.append(" },\n");
        return sb.toString();
    }

    private static String serializeReleves(List<Releve> releves, int indent) {
        if (releves == null || releves.isEmpty()) return "[]";
        String pad = " ".repeat(indent);
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < releves.size(); i++) {
            Releve r = releves.get(i);
            sb.append(pad).append("  { \"niveau\": ").append(jsonStr(r.getNiveauReleve().name()))
                    .append(", \"valeurs\": ").append(mapToJson(r.getValeurs())).append(" }");
            if (i < releves.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append(pad).append("]");
        return sb.toString();
    }

    // ─────────────────────────────────────────────────────────────────
    // UTILITAIRES JSON
    // ─────────────────────────────────────────────────────────────────

    /** Sérialise une Map<String,Object> en JSON en ligne (pour les valeurs d'un relevé). */
    private static String mapToJson(Map<String, Object> map) {
        if (map == null || map.isEmpty()) return "{}";
        StringBuilder sb = new StringBuilder("{ ");
        boolean first = true;
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(jsonStr(e.getKey())).append(": ");
            Object v = e.getValue();
            if (v instanceof Number) sb.append(((Number) v).doubleValue());
            else if (v instanceof Boolean) sb.append(v);
            else sb.append(jsonStr(v != null ? v.toString() : ""));
            first = false;
        }
        sb.append(" }");
        return sb.toString();
    }

    private static String jsonStr(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static Status parseStatus(String s) {
        if (s == null) return Status.ACTIF;
        try { return Status.valueOf(s); } catch (Exception e) { return Status.ACTIF; }
    }

    // ─────────────────────────────────────────────────────────────────
    // ENVOI PÉRIODIQUE DES VALEURS (données du fichier seed)
    // ─────────────────────────────────────────────────────────────────

    /**
     * Charge les valeurs historiques depuis le fichier seed et les regroupe
     * par TYPE de capteur ("ENV", "SOL", "AQUA", "BIOM", "GPS").
     *
     * Toutes les valeurs de tous les capteurs du meme type sont fusionnees dans
     * un seul pool. Ainsi, tout capteur nouvellement ajoute (quel que soit son code)
     * profite automatiquement du meme pool de valeurs que les capteurs du meme type
     * presents dans farm_data.json.
     *
     * Retourne : Map<typeStr, List<Map<cle, valeur>>>
     */
    public static Map<String, List<Map<String, Object>>> loadSeedSensorValues(Path seedPath) {
        // type -> pool de tous les jeux de valeurs de ce type
        Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();
        try {
            Map<String, Object> root = MiniJson.parseFile(seedPath);
            List<Object> zonesRaw = MiniJson.list(root, "zones");
            for (Object zRaw : zonesRaw) {
                Map<String, Object> zNode = (Map<String, Object>) zRaw;
                List<Object> capteursRaw = MiniJson.list(zNode, "capteurs");
                for (Object cRaw : capteursRaw) {
                    Map<String, Object> cNode = (Map<String, Object>) cRaw;
                    String type = MiniJson.str(cNode, "type");
                    if (type == null) continue;
                    List<Object> historiqueRaw = MiniJson.list(cNode, "historique_valeurs");
                    // Accumuler dans le pool du type
                    List<Map<String, Object>> pool =
                            result.computeIfAbsent(type, k -> new ArrayList<>());
                    for (Object hRaw : historiqueRaw) {
                        Map<String, Object> hNode = (Map<String, Object>) hRaw;
                        pool.add(normalizeValues(type, hNode));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement valeurs seed: " + e.getMessage());
        }
        return result;
    }

    /**
     * Convertit les clés JSON du fichier seed vers les clés attendues par les capteurs.
     */
    private static Map<String, Object> normalizeValues(String type, Map<String, Object> raw) {
        Map<String, Object> m = new LinkedHashMap<>();
        switch (type) {
            case "ENV" -> {
                if (raw.containsKey("temp"))     m.put("temperature",  raw.get("temp"));
                if (raw.containsKey("humidite")) m.put("humidite",     raw.get("humidite"));
                if (raw.containsKey("pluvi"))    m.put("pluviometrie", raw.get("pluvi"));
            }
            case "BIOM" -> {
                if (raw.containsKey("temp_corp")) m.put("temperature_corporelle", raw.get("temp_corp"));
                if (raw.containsKey("act"))        m.put("activite_par_minute",   raw.get("act"));
            }
            case "SOL" -> {
                if (raw.containsKey("azote"))    m.put("azote",   raw.get("azote"));
                if (raw.containsKey("humidite")) m.put("humidite",raw.get("humidite"));
                if (raw.containsKey("ph"))       m.put("ph",      raw.get("ph"));
            }
            case "AQUA" -> {
                if (raw.containsKey("temp"))    m.put("temperature", raw.get("temp"));
                if (raw.containsKey("oxygene")) m.put("oxygene",     raw.get("oxygene"));
                if (raw.containsKey("ph"))      m.put("ph",          raw.get("ph"));
            }
            case "GPS" -> {
                if (raw.containsKey("longitude")) m.put("longitude", raw.get("longitude"));
                if (raw.containsKey("latitude"))  m.put("latitude",  raw.get("latitude"));
            }
            default -> m.putAll(raw);
        }
        return m;
    }
}