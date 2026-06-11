package com.example.tpoop;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * LangChain4j tool implementations backed by live {@link Ferme} data.
 */
public class FarmAiTools {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Ferme ferme;

    public FarmAiTools(Ferme ferme) {
        this.ferme = ferme;
    }

    @Tool(name = "getZoneStatus", value = """
            Returns status and details for farm zones (code, name, status, type, sensors, culture or livestock info).
            Pass a zone code (e.g. ZC001) or zone name to filter, or leave empty for all zones.""")
    public String getZoneStatus(
            @P("Optional zone code or name. Empty string returns all zones.") String zoneCodeOrName) {
        List<Zone> zones = resolveZones(zoneCodeOrName);
        if (zones.isEmpty()) {
            return zoneFilterEmpty(zoneCodeOrName)
                    ? "Aucune zone enregistrée sur la ferme."
                    : "Aucune zone trouvée pour : " + zoneCodeOrName;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Ferme : ").append(ferme.getNom()).append(" — ").append(zones.size()).append(" zone(s)\n\n");
        for (Zone z : zones) {
            appendZoneDetails(sb, z);
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    @Tool(name = "getAnimalCount", value = """
            Returns animal counts across the farm: livestock in ZoneElevage and aquaculture individuals in ZoneAqua.
            Optionally filter by zone code or name.""")
    public String getAnimalCount(
            @P("Optional zone code or name. Empty string counts all zones.") String zoneCodeOrName) {
        List<Zone> zones = resolveZones(zoneCodeOrName);
        if (zones.isEmpty()) {
            return zoneFilterEmpty(zoneCodeOrName)
                    ? "Aucune zone enregistrée."
                    : "Aucune zone trouvée pour : " + zoneCodeOrName;
        }

        int totalElevage = 0;
        int totalMalades = 0;
        int totalAqua = 0;
        StringBuilder sb = new StringBuilder("Comptage des animaux — Ferme : ").append(ferme.getNom()).append("\n\n");

        for (Zone z : zones) {
            if (z instanceof ZoneElevage ze) {
                int count = ze.getAnimaux().size();
                int malades = ze.getNbAnimauxMalades();
                totalElevage += count;
                totalMalades += malades;
                sb.append("• ").append(z.getName()).append(" (").append(z.getCode()).append(") — Élevage ")
                        .append(ze.getTypeAnimal()).append(" : ").append(count).append(" animal(aux)");
                if (malades > 0) sb.append(", dont ").append(malades).append(" malade(s)/quarantaine");
                sb.append("\n");
                for (Animal a : ze.getAnimaux()) {
                    sb.append("    - #").append(a.getID()).append(" ").append(a.getEspece().getName())
                            .append(", état: ").append(a.getEtat()).append(", poids: ").append(a.getPoids()).append(" kg\n");
                }
            } else if (z instanceof ZoneAqua za) {
                int count = za.getNbAnimaux();
                totalAqua += count;
                sb.append("• ").append(z.getName()).append(" (").append(z.getCode()).append(") — Aquaculture ")
                        .append(za.getEspece()).append(" : ").append(count).append(" individu(s)\n");
            }
        }

        if (totalElevage == 0 && totalAqua == 0) {
            sb.append("Aucun animal dans les zones sélectionnées.");
        } else {
            sb.append("\nTotaux : ").append(totalElevage).append(" élevage, ")
                    .append(totalAqua).append(" aquaculture, ")
                    .append(totalMalades).append(" malade(s)/quarantaine.");
        }
        return sb.toString().trim();
    }

    @Tool(name = "getActiveAlerts", value = """
            Returns all active alerts on the farm with id, severity, message, zone, and creation date.""")
    public String getActiveAlerts() {
        List<Alerte> actives = ferme.getAlertes().stream().filter(Alerte::isActive).toList();
        if (actives.isEmpty()) {
            return "Aucune alerte active sur la ferme " + ferme.getNom() + ".";
        }

        StringBuilder sb = new StringBuilder("Alertes actives (").append(actives.size()).append(") :\n\n");
        for (Alerte a : actives) {
            sb.append("• [").append(a.getId()).append("] ").append(a.getGravite())
                    .append(" — ").append(a.getMessage());
            if (a.getZone() != null) {
                sb.append(" (zone: ").append(a.getZone().getName())
                        .append(" / ").append(a.getZone().getCode()).append(")");
            }
            sb.append(" — ").append(a.getDateCreation().format(DT_FMT)).append("\n");
        }
        return sb.toString().trim();
    }

    @Tool(name = "getProductionTotal", value = """
            Returns production totals per zone and farm-wide.
            Optionally filter by zone code/name and/or production type (LAIT, OEUFS, POIDS_RECOLTE, RENDEM_CULTURE).""")
    public String getProductionTotal(
            @P("Optional zone code or name. Empty string includes all zones.") String zoneCodeOrName,
            @P("Optional production type: LAIT, OEUFS, POIDS_RECOLTE, RENDEM_CULTURE. Empty for all types.") String productionType) {
        List<Zone> zones = resolveZones(zoneCodeOrName);
        if (zones.isEmpty()) {
            return zoneFilterEmpty(zoneCodeOrName)
                    ? "Aucune zone enregistrée."
                    : "Aucune zone trouvée pour : " + zoneCodeOrName;
        }

        TypeProd filterType = null;
        if (productionType != null && !productionType.isBlank()) {
            try {
                filterType = TypeProd.valueOf(productionType.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return "Type de production invalide : " + productionType
                        + ". Valeurs acceptées : LAIT, OEUFS, POIDS_RECOLTE, RENDEM_CULTURE.";
            }
        }

        StringBuilder sb = new StringBuilder("Production — Ferme : ").append(ferme.getNom()).append("\n\n");
        double grandTotal = 0;
        int recordCount = 0;

        for (Zone z : zones) {
            List<Prod> prods = z.getProductions();
            if (filterType != null) {
                final TypeProd ft = filterType;
                prods = prods.stream().filter(p -> p.getProd() == ft).collect(Collectors.toList());
            }
            if (prods.isEmpty()) continue;

            double zoneTotal = prods.stream().mapToDouble(Prod::getVal).sum();
            grandTotal += zoneTotal;
            recordCount += prods.size();

            sb.append("• ").append(z.getName()).append(" (").append(z.getCode()).append(") : ")
                    .append(zoneTotal).append(" (").append(prods.size()).append(" relevé(s))\n");
            Prod latest = z.getLatest();
            if (latest != null && (filterType == null || latest.getProd() == filterType)) {
                sb.append("    Dernier : ").append(latest.getVal()).append(latest.getProd().getUnite())
                        .append(" (").append(latest.getProd()).append(") le ").append(latest.getDate()).append("\n");
            }
        }

        if (recordCount == 0) {
            sb.append("Aucun enregistrement de production trouvé");
            if (filterType != null) sb.append(" pour le type ").append(filterType);
            sb.append(".");
        } else {
            sb.append("\nTotal cumulé : ").append(grandTotal).append(" (").append(recordCount).append(" relevé(s)).");
        }
        return sb.toString().trim();
    }

    private void appendZoneDetails(StringBuilder sb, Zone z) {
        sb.append("Zone : ").append(z.getName()).append(" [").append(z.getCode()).append("]\n");
        sb.append("  Statut : ").append(z.getStatus()).append("\n");
        sb.append("  Type : ").append(zoneTypeLabel(z)).append("\n");
        sb.append("  Capteurs : ").append(z.getCapteurs().size());
        long actifs = z.getCapteurs().stream().filter(c -> c.getStatus() == Status.ACTIF).count();
        sb.append(" (").append(actifs).append(" actif(s))\n");

        if (z instanceof ZoneCulture zc) {
            Culture c = zc.getCultures();
            if (c != null) {
                sb.append("  Culture : ").append(c.getNom())
                        .append(" — stade : ").append(c.getStadeCroiss())
                        .append(" — plantation : ").append(c.getDatePlantation())
                        .append(" — récolte prévue : ").append(c.getDateRecolte()).append("\n");
            } else {
                sb.append("  Culture : non affectée\n");
            }
        } else if (z instanceof ZoneElevage ze) {
            sb.append("  Élevage : ").append(ze.getTypeAnimal())
                    .append(" — ").append(ze.getAnimaux().size()).append(" animal(aux)")
                    .append(" — malades/quarantaine : ").append(ze.getNbAnimauxMalades()).append("\n");
        } else if (z instanceof ZoneAqua za) {
            sb.append("  Aquaculture : ").append(za.getEspece())
                    .append(" — ").append(za.getNbAnimaux()).append(" individu(s)\n");
        }

        double prodTotal = z.getTotal();
        if (!z.getProductions().isEmpty()) {
            sb.append("  Production cumulée : ").append(prodTotal)
                    .append(" (").append(z.getProductions().size()).append(" relevé(s))\n");
        }
    }

    private String zoneTypeLabel(Zone z) {
        if (z instanceof ZoneCulture) return "CULTURE";
        if (z instanceof ZoneElevage) return "ELEVAGE";
        if (z instanceof ZoneAqua) return "AQUACULTURE";
        return "INCONNU";
    }

    private List<Zone> resolveZones(String zoneCodeOrName) {
        if (zoneFilterEmpty(zoneCodeOrName)) {
            return ferme.getZones();
        }
        String q = zoneCodeOrName.trim().toLowerCase();
        Optional<Zone> exact = ferme.getZones().stream()
                .filter(z -> z.getCode().equalsIgnoreCase(q) || z.getName().equalsIgnoreCase(q))
                .findFirst();
        if (exact.isPresent()) return List.of(exact.get());
        return ferme.getZones().stream()
                .filter(z -> z.getCode().toLowerCase().contains(q) || z.getName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    private boolean zoneFilterEmpty(String zoneCodeOrName) {
        return zoneCodeOrName == null || zoneCodeOrName.isBlank();
    }
}
