package com.example.tpoop;

import java.util.HashMap;
import java.util.Map;

public class Cap_env extends Capteur_num {
    private double temp;
    private double humidity;
    private double pluvi;

    public Cap_env(String code, Zone location, Status status, double temp, double humidity, double pluvi) {
        super(code, location, status);
        this.temp = temp;
        this.humidity = humidity;
        this.pluvi = pluvi;
    }

    public void setTemp(double temp) { this.temp = temp; }
    public void setHumidity(double humidity) { this.humidity = humidity; }
    public void setPluvi(double pluvi) { this.pluvi = pluvi; }

    @Override
    public Map<String, Object> send_values() {
        Map<String, Object> map = new HashMap<>();
        map.put("temperature", temp);
        map.put("humidite", humidity);
        map.put("pluviometrie", pluvi);
        return map;
    }
}