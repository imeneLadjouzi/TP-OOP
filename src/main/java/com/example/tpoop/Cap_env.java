package com.example.tpoop;

import java.util.HashMap;
import java.util.Map;

public class Cap_env extends Capteur_num{
    private int temp;
    private int humidity;
    private int pluvi;
    public Cap_env(String code, Object location, Status status,  int temp, int humidity, int pluvi) {
        super(code, location, status);
        this.temp = temp;
        this.humidity = humidity;
        this.pluvi = pluvi;
    }
    public Map<String, Object> send_values(){
        Map<String, Object> map = new HashMap<>();
        map.put("temp", temp);
        map.put("humidity", humidity);
        map.put("pluvi", pluvi);
        return map;
    }

}
