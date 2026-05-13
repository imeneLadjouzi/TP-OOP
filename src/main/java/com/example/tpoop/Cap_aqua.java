package com.example.tpoop;

import java.util.HashMap;
import java.util.Map;

public class Cap_aqua extends Capteur_num {
    private double temp;
    private double oxygen;
    private double ph;

    public Cap_aqua(String code, Zone location, Status status, double temp, double oxygen, double ph) {
        super(code, location, status);
        this.temp = temp;
        this.oxygen = oxygen;
        this.ph = ph;
    }

    public void setTemp(double temp) { this.temp = temp; }
    public void setOxygen(double oxygen) { this.oxygen = oxygen; }
    public void setPh(double ph) { this.ph = ph; }

    @Override
    public Map<String, Object> send_values() {
        Map<String, Object> map = new HashMap<>();
        map.put("temperature", temp);
        map.put("oxygene", oxygen);
        map.put("ph", ph);
        return map;
    }
}