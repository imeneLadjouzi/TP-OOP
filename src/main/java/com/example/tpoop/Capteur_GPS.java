package com.example.tpoop;

import java.util.*;

public class Capteur_GPS extends Capteurs{
    private Object position_geo; //coordonnés
    public Capteur_GPS(String code, Object location, Status status, Object position_geo) {
        super(code, location,  status);
        this.position_geo = position_geo;
    }
    public Map<String, Object> send_values(){
        Map<String, Object> map = new HashMap<>();
        map.put("position_géo", position_geo);
        return map;
    }
}
