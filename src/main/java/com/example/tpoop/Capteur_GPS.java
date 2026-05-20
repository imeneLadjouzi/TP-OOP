package com.example.tpoop;

import java.util.HashMap;
import java.util.Map;

import static com.example.tpoop.Main.lireDouble;

public class Capteur_GPS extends Capteurs {
    private PositionGeographique position;

    public Capteur_GPS(String code, Zone location, Status status, PositionGeographique position) {
        super(code, location, status, TypeCapteur.GPS);
        this.position = position;
    }

    public void updatePosition(PositionGeographique nouvellePosition) {
        this.position = nouvellePosition;
    }

    @Override
    public Map<String, Object> send_values() {
        Map<String, Object> map = new HashMap<>();
        map.put("latitude", position.getLatitude());
        map.put("longitude", position.getLongitude());
        return map;
    }

    public PositionGeographique getPosition() {
        return position;
    }
}