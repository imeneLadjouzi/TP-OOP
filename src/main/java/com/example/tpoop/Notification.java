package com.example.tpoop;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A single notification entry in the notification centre.
 *
 * Three flavours (see {@link NotifType}):
 *  - WARNING  : a sensor reading is near its threshold (no releve / no alerte created)
 *  - CRITICAL : an active alert has been ignored for too long
 *  - ACTION   : audit record of a user action (deletion, acquittal, …)
 */
public class Notification {

    private static int counter = 0;
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final int id;
    private final NotifType type;
    private final String title;
    private final String detail;
    private final LocalDateTime createdAt;
    private boolean read;

    // Optional links to the domain object that triggered this notification
    private Capteurs sourceCapteur;   // for WARNING
    private Alerte   sourceAlerte;    // for CRITICAL
    // ACTION notifications carry no domain link — detail string is self-sufficient

    // ─── Constructors ────────────────────────────────────────────────

    /** Generic constructor (ACTION). */
    public Notification(NotifType type, String title, String detail) {
        this.id        = ++counter;
        this.type      = type;
        this.title     = title;
        this.detail    = detail;
        this.createdAt = LocalDateTime.now();
        this.read      = false;
    }

    /** Constructor for WARNING (sensor near limit). */
    public Notification(String title, String detail, Capteurs sourceCapteur) {
        this(NotifType.WARNING, title, detail);
        this.sourceCapteur = sourceCapteur;
    }

    /** Constructor for CRITICAL (ignored alert). */
    public Notification(String title, String detail, Alerte sourceAlerte) {
        this(NotifType.CRITICAL, title, detail);
        this.sourceAlerte = sourceAlerte;
    }

    // ─── API ─────────────────────────────────────────────────────────

    public void markRead()   { this.read = true; }
    public boolean isRead()  { return read; }
    public boolean isUnread(){ return !read; }

    // ─── Getters ─────────────────────────────────────────────────────

    public int           getId()            { return id; }
    public NotifType     getType()          { return type; }
    public String        getTitle()         { return title; }
    public String        getDetail()        { return detail; }
    public LocalDateTime getCreatedAt()     { return createdAt; }
    public Capteurs      getSourceCapteur() { return sourceCapteur; }
    public Alerte        getSourceAlerte()  { return sourceAlerte; }

    // ─── Display ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "[" + id + "] " + type + " | " + createdAt.format(FMT)
                + " | " + title + " : " + detail;
    }
}