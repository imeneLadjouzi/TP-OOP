package com.example.tpoop;

public class ExigPedologiques {
     private double phMin;
     private double phMax;
     private double humiditeMin;
     private double humiditeMax;
     private double azoteMin;
     private double azoteMax;
     private double pluviMin;




     boolean estCompatible(double ph, double humidite) {
          return ph >= phMin && ph <= phMax && humidite >= humiditeMin && humidite <= humiditeMax;
     }
}
