package com.example.tpoop;

import java.time.LocalDate;
import java.time.LocalDateTime;

interface AlerteSpecification {
    boolean isSatisfiedBy(Alerte a);

    default AlerteSpecification and(AlerteSpecification other) {
        return capteur -> this.isSatisfiedBy(capteur) && other.isSatisfiedBy(capteur);
    }
}

class AlerteSpecifications {

    public static AlerteSpecification zoneEquals(Zone zone) {
        return zone == null ? c -> true : c -> c.getZone().equals(zone);
    }

    public static AlerteSpecification typeAlerteEquals(TypeCapteur type) {
        return type == null ? c -> true : c -> c.getReleve().getCapteur().getType() == type;
    }

    public static AlerteSpecification niveauEquals(Niveau_gravite niveau) {
        return niveau == null ? c -> true : c -> c.getGravite() == niveau;
    }

    public static AlerteSpecification dateEntre(LocalDate debut, LocalDate fin) {
        return a -> {
            LocalDate date = ( a.getDateCreation()).toLocalDate();
            if (debut != null && date.isBefore(debut)) return false;
            if (fin != null && date.isAfter(fin)) return false;
            return true;
        };
    }
}