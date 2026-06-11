package com.example.tpoop;

public enum NotifType {
    /** Sensor value is close to a threshold (avertissement range) — no releve, no alerte generated. */
    WARNING,
    /** An active alert has been ignored for longer than the configured timeout. */
    CRITICAL,
    /** An action performed by the user (deletion, acquittal, etc.) — audit log entry. */
    ACTION
}