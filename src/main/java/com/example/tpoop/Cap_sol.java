package com.example.tpoop;

import java.util.HashMap;
import java.util.Map;

public class Cap_sol extends Capteur_num{
    private int azote;
    private int humidity;
    private int ph;
    public Cap_sol(String code, Object location, Status status, int azote, int humidity, int ph){
        super(code, location, status);
        this.azote = azote;
        this.humidity = humidity;
        this.ph = ph;
    }
    public Map<String, Object> send_values(){
        Map<String, Object> map = new HashMap<>();
        map.put("azote", azote);
        map.put("humidity", humidity);
        map.put("ph", ph);
        return map;
    }
}
