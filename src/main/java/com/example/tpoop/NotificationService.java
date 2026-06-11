package com.example.tpoop;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Central notification service for the GreenField application.
 *
 * Responsibilities
 * ───────────────
 * 1. WARNING notifications — called by the periodic sensor timer BEFORE a
 *    releve is triggered.  It reads the sensor's current attribute values
 *    and evaluates each sub-threshold independently.  If a value is in
 *    AVERTISSEMENT range (and not CRITIQUE), a WARNING notification is
 *    created — no Releve, no Alerte.
 *
 * 2. CRITICAL notifications — also called by the periodic timer.  It walks
 *    the farm's alert list and fires a CRITICAL notification for every active
 *    alert that has not been acknowledged within {@link #ALERT_IGNORE_MINUTES}.
 *    A guard map prevents duplicate CRITICAL notifications per alert.
 *
 * 3. ACTION notifications — called by {@link Gestionnaire} (and the controller)
 *    whenever the user performs a significant action: zone deletion, alert
 *    acquittal, sensor deletion, animal deletion, etc.
 *
 * Observers
 * ─────────
 * Register a {@code Consumer<Notification>} listener via
 * {@link #addListener(Consumer)}.  The MainController registers one listener
 * that updates the bell badge and prepends the entry in the slide-out panel.
 *
 * Thread safety
 * ─────────────
 * All mutations are performed on the JavaFX Application Thread (the periodic
 * timer already uses {@code Platform.runLater}).  No synchronisation needed.
 */
public class NotificationService {

    /** Minutes after which an unacknowledged active alert triggers a CRITICAL notification. */
    public static final long ALERT_IGNORE_MINUTES = 5;

    /** Maximum notifications kept in memory (FIFO, oldest dropped). */
    private static final int MAX_SIZE = 200;

    private final Ferme ferme;
    private final List<Notification> notifications = new ArrayList<>();
    private final List<Consumer<Notification>> listeners = new ArrayList<>();

    /**
     * Tracks which alerts already have a pending CRITICAL notification so we
     * don't spam one every timer tick.
     * Key: Alerte.getId(), Value: timestamp when the CRITICAL was first fired.
     */
    private final Map<Integer, LocalDateTime> criticalFiredAt = new java.util.HashMap<>();

    public NotificationService(Ferme ferme) {
        this.ferme = ferme;
    }

    // ─────────────────────────────────────────────────────────────────
    // PUBLIC API
    // ─────────────────────────────────────────────────────────────────

    /**
     * Register a listener that is called synchronously each time a new
     * {@link Notification} is created.  The MainController uses this to
     * update the badge counter and panel.
     */
    public void addListener(Consumer<Notification> listener) {
        listeners.add(listener);
    }

    /**
     * Called by the periodic sensor timer in MainController.
     * Checks all active sensors for near-threshold values (WARNING) and
     * checks all active unacknowledged alerts for the ignore timeout (CRITICAL).
     */
    public void runPeriodicChecks() {
        checkSensorsForWarnings();
        checkIgnoredAlerts();
    }

    /**
     * Log a user action as an ACTION notification.
     *
     * @param title  Short verb phrase, e.g. "Zone supprimée"
     * @param detail Contextual detail, e.g. "Zone ZC001 — Champ Blé Nord"
     */
    public void logAction(String title, String detail) {
        push(new Notification(NotifType.ACTION, title, detail));
    }

    /** All notifications, newest first. */
    public List<Notification> getAll() {
        List<Notification> copy = new ArrayList<>(notifications);
        Collections.reverse(copy);
        return copy;
    }

    /** Unread count (badge number). */
    public long getUnreadCount() {
        return notifications.stream().filter(Notification::isUnread).count();
    }

    /** Mark all notifications as read (e.g. when the panel is opened). */
    public void markAllRead() {
        notifications.forEach(Notification::markRead);
    }

    /** Remove a single notification. */
    public void dismiss(Notification n) {
        notifications.remove(n);
    }

    /** Clear all notifications. */
    public void clearAll() {
        notifications.clear();
        criticalFiredAt.clear();
    }

    // ─────────────────────────────────────────────────────────────────
    // INTERNAL — WARNING (sensor near limit)
    // ─────────────────────────────────────────────────────────────────

    /**
     * Evaluates each active sensor's current attribute values against its
     * thresholds WITHOUT triggering a Releve or an Alerte.
     *
     * For each sensor sub-value that is in AVERTISSEMENT range (i.e. the
     * sensor's own evaluer() returns AVERTISSEMENT and the global level is
     * not already CRITIQUE), we emit one WARNING notification.
     *
     * Deduplication: we only emit one WARNING per sensor per timer cycle
     * (multiple sub-values in warning collapse into one notification with a
     * combined detail message).
     */
    private void checkSensorsForWarnings() {
        for (Capteurs c : ferme.getTousLesCapteurs()) {
            if (c.getStatus() != Status.ACTIF) continue;

            String warningDetail = buildWarningDetail(c);
            if (warningDetail != null) {
                String zone = c.getLocation() != null ? c.getLocation().getName() : "—";
                push(new Notification(
                        "Capteur proche des seuils — " + c.getCode(),
                        zone + " | " + warningDetail,
                        c
                ));
            }
        }
    }

    /**
     * Returns a human-readable description of which sub-values are in
     * AVERTISSEMENT range, or {@code null} if the sensor is fine (INFO)
     * or already CRITIQUE (handled by alerts).
     */
    private String buildWarningDetail(Capteurs c) {
        StringBuilder sb = new StringBuilder();

        if (c instanceof Cap_env ce) {
            if (ce.evaluertemp()  == Niveau_gravite.AVERTISSEMENT) sb.append("Température ");
            if (ce.evaluerhum()   == Niveau_gravite.AVERTISSEMENT) sb.append("Humidité ");
            if (ce.evaluerplu()   == Niveau_gravite.AVERTISSEMENT) sb.append("Pluviométrie ");
        } else if (c instanceof Cap_sol cs) {
            if (cs.seuils.evaluerPh(readPh(cs))       == Niveau_gravite.AVERTISSEMENT) sb.append("pH ");
            if (cs.seuils.evaluerhum(readHum(cs))     == Niveau_gravite.AVERTISSEMENT) sb.append("Humidité ");
            if (cs.seuils.evalueraz(readAzote(cs))    == Niveau_gravite.AVERTISSEMENT) sb.append("Azote ");
        } else if (c instanceof Cap_aqua ca) {
            if (ca.evaluertemp() == Niveau_gravite.AVERTISSEMENT) sb.append("Température ");
            if (ca.evalueroxy()  == Niveau_gravite.AVERTISSEMENT) sb.append("Oxygène ");
            if (ca.evaluerph()   == Niveau_gravite.AVERTISSEMENT) sb.append("pH ");
        } else if (c instanceof Cap_biometrique cb) {
            if (cb.evaluertemp() == Niveau_gravite.AVERTISSEMENT) sb.append("Température corporelle ");
            if (cb.evalueract()  == Niveau_gravite.AVERTISSEMENT) sb.append("Activité ");
        } else if (c instanceof Capteur_GPS cg) {
            if (cg.evaluerPosition() == Niveau_gravite.AVERTISSEMENT) sb.append("Position GPS ");
        }

        if (sb.length() == 0) return null;

        // Strip trailing space and suffix
        return sb.toString().trim() + " proche des seuils";
    }

    // Reflection-free field readers using send_values() map so we don't expose
    // private fields across packages.  The timer has already set the values.
    private double readPh(Cap_sol cs) {
        Object v = cs.send_values().get("ph");
        return v instanceof Number n ? n.doubleValue() : 0;
    }
    private double readHum(Cap_sol cs) {
        Object v = cs.send_values().get("humidite");
        return v instanceof Number n ? n.doubleValue() : 0;
    }
    private double readAzote(Cap_sol cs) {
        Object v = cs.send_values().get("azote");
        return v instanceof Number n ? n.doubleValue() : 0;
    }

    // ─────────────────────────────────────────────────────────────────
    // INTERNAL — CRITICAL (ignored alert)
    // ─────────────────────────────────────────────────────────────────

    /**
     * For every active (non-acknowledged) alert older than
     * {@link #ALERT_IGNORE_MINUTES} minutes, fire exactly one CRITICAL
     * notification.  Subsequent timer ticks do not re-fire for the same alert
     * until the alert is acknowledged and then becomes active again (which
     * resets its creation time).
     */
    private void checkIgnoredAlerts() {
        LocalDateTime now = LocalDateTime.now();
        for (Alerte a : ferme.getAlertes()) {
            if (!a.isActive()) {
                // Acknowledged — remove from guard map
                criticalFiredAt.remove(a.getId());
                continue;
            }
            long minutesOld = Duration.between(a.getDateCreation(), now).toMinutes();
            if (minutesOld < ALERT_IGNORE_MINUTES) continue;
            // Already fired a CRITICAL for this alert?
            if (criticalFiredAt.containsKey(a.getId())) continue;

            criticalFiredAt.put(a.getId(), now);
            String zone = a.getZone() != null ? a.getZone().getName() : "—";
            push(new Notification(
                    "⚠ Alerte ignorée depuis " + minutesOld + " min — " + a.getGravite(),
                    "Zone : " + zone + " | " + a.getMessage(),
                    a
            ));
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // INTERNAL — push & notify
    // ─────────────────────────────────────────────────────────────────

    private void push(Notification n) {
        // Enforce max size (drop oldest)
        if (notifications.size() >= MAX_SIZE) {
            notifications.remove(0);
        }
        notifications.add(n);
        listeners.forEach(l -> l.accept(n));
    }
}