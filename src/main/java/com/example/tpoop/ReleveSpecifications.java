package com.example.tpoop;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtre les relevés par plage de dates, sur le même modèle que AlerteSpecifications.
 * À placer dans le package com.example.tpoop.
 */
public class ReleveSpecifications {

    // Interface spécification (même patron que AlerteSpecification)
    public interface ReleveSpec {
        boolean isSatisfiedBy(Releve r);
        default ReleveSpec and(ReleveSpec other) {
            return releve -> this.isSatisfiedBy(releve) && other.isSatisfiedBy(releve);
        }
    }

    /** Filtre : date de relevé >= debut (null = pas de borne inférieure). */
    public static ReleveSpec apresOuEgal(LocalDate debut) {
        return debut == null
                ? r -> true
                : r -> !r.getDateHeure().toLocalDate().isBefore(debut);
    }

    /** Filtre : date de relevé <= fin (null = pas de borne supérieure). */
    public static ReleveSpec avantOuEgal(LocalDate fin) {
        return fin == null
                ? r -> true
                : r -> !r.getDateHeure().toLocalDate().isAfter(fin);
    }

    /** Filtre : niveau de gravité du relevé. */
    public static ReleveSpec niveauEquals(Niveau_gravite niveau) {
        return niveau == null ? r -> true : r -> r.getNiveauReleve() == niveau;
    }

    /**
     * Point d'entrée pratique : applique un filtre par plage de dates sur une liste de relevés.
     *
     * @param historique liste source (ex: capteur.getHistorique())
     * @param debut      date de début inclusive, null = pas de borne
     * @param fin        date de fin inclusive,   null = pas de borne
     * @return sous-liste filtrée
     */
    public static List<Releve> filtrer(List<Releve> historique, LocalDate debut, LocalDate fin) {
        ReleveSpec spec = apresOuEgal(debut).and(avantOuEgal(fin));
        return historique.stream()
                .filter(spec::isSatisfiedBy)
                .collect(Collectors.toList());
    }

    /**
     * Surcharge avec filtre niveau en plus.
     */
    public static List<Releve> filtrer(List<Releve> historique,
                                       LocalDate debut,
                                       LocalDate fin,
                                       Niveau_gravite niveau) {
        ReleveSpec spec = apresOuEgal(debut)
                .and(avantOuEgal(fin))
                .and(niveauEquals(niveau));
        return historique.stream()
                .filter(spec::isSatisfiedBy)
                .collect(Collectors.toList());
    }
}