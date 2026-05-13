package com.example.tpoop;

import java.util.HashMap;
import java.util.Map;

public class Cap_biometrique extends Capteur_num {
    private double temp_corporelle;
    private double activity_per_min;

    public Cap_biometrique(String code, Zone location, Status status, double temp_corporelle, double activity_per_min) {
        super(code, location, status);
        this.temp_corporelle = temp_corporelle;
        this.activity_per_min = activity_per_min;
    }

    public void setTempCorporelle(double temp) { this.temp_corporelle = temp; }
    public void setActivityPerMin(double activity) { this.activity_per_min = activity; }

    @Override
    public Map<String, Object> send_values() {
        Map<String, Object> map = new HashMap<>();
        map.put("temperature_corporelle", temp_corporelle);
        map.put("activite_par_minute", activity_per_min);
        return map;
    }
}