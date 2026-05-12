package com.example.tpoop;

import java.util.Map;

abstract public class Capteur_num extends Capteurs{
    public Capteur_num(String code, Object location, Status status) {
        super(code, location, status);
    }

    abstract public Map<String, Object> send_values();
}
