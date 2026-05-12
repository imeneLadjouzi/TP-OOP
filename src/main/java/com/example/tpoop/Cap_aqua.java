package com.example.tpoop;

import java.util.HashMap;
import java.util.Map;

public class Cap_aqua extends Capteur_num{
    private int temp;
    private int oxygen;
    private int ph;
    public Cap_aqua(String code, Object location, Status status, int temp, int oxygen, int ph) {
        super(code, location, status);
        this.temp = temp;
        this.oxygen = oxygen;
        this.ph = ph;
    }
    public Map<String, Object> send_values(){
        Map<String, Object> map = new HashMap<>();
        map.put("temp", temp);
        map.put("oxygen", oxygen);
        map.put("ph", ph);
        return map;
    }
}
