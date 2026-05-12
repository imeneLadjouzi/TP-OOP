package com.example.tpoop;

import java.util.Map;

public class Cap_biometrique extends Capteur_num {
    private int temp_corporelle;
    private int activity_per_min;
    public Cap_biometrique(String code, Object location, Status status, int temp_corporelle, int activity_per_min) {
        super(code, location, status);
        this.temp_corporelle = temp_corporelle;
        this.activity_per_min = activity_per_min;
    }
    @Override
    public Map<String, Object> send_values() {
        return Map.of(
                "temperature corporelle",temp_corporelle,
                "activity_per_min",activity_per_min
        );
    }
}
