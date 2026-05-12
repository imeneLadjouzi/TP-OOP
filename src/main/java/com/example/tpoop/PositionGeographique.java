package com.example.tpoop;

public class PositionGeographique {
    private int latitude;
    private int longitude;
    public void PositionGeographique(int latitude, int longitude){;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getLatitude() {
        return latitude;
    }
    public int getLongitude(){
        return longitude;
    }
}
