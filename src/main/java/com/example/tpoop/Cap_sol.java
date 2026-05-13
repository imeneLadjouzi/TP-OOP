package com.example.tpoop;

import java.util.HashMap;
import java.util.Map;

public class Cap_sol extends Capteur_num {
    private double azote;
    private double humidity;
    private double ph;

    public Cap_sol(String code, Zone location, Status status, double azote, double humidity, double ph) {
        super(code, location, status);
        this.azote = azote;
        this.humidity = humidity;
        this.ph = ph;
    }

    public void setAzote(double azote) { this.azote = azote; }
    public void setHumidity(double humidity) { this.humidity = humidity; }
    public void setPh(double ph) { this.ph = ph; }

    @Override
    public Map<String, Object> send_values() {
        Map<String, Object> map = new HashMap<>();
        map.put("azote", azote);
        map.put("humidite", humidity);
        map.put("ph", ph);
        return map;
    }
}