package com.example.tpoop;

import java.util.Map;

public abstract class Capteur_num extends Capteurs {
    public Capteur_num(String code, Zone location, Status status) {
        super(code, location, status);
    }

    public abstract Map<String, Object> send_values();
}