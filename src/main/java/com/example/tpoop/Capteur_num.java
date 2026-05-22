package com.example.tpoop;

import java.util.Map;

public abstract class Capteur_num extends Capteurs {
    public Capteur_num(String code, Zone location, Status status, TypeCapteur type) {
        super(code, location, status, type);
    }

    public abstract Map<String, Object> send_values();
    //abstract public void configurer(double minlong, double maxlong, double minlat, double maxlat);
}