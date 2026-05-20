package com.example.tpoop;

public class ExigPedologiques {
     private double phMin;
     private double phMax;
     private double humiditeMin;
     private double humiditeMax;
     private double azoteMin;
     private double azoteMax;
     private double pluviMin;

     public ExigPedologiques(double phMin, double phMax, double humiditeMin, double humiditeMax,
                             double azoteMin, double azoteMax, double pluviMin) {
          this.phMin = phMin;
          this.phMax = phMax;
          this.humiditeMin = humiditeMin;
          this.humiditeMax = humiditeMax;
          this.azoteMin = azoteMin;
          this.azoteMax = azoteMax;
          this.pluviMin = pluviMin;
     }

     public boolean estCompatible(double ph, double humidite, double azote) {
          return ph >= phMin && ph <= phMax && humidite >= humiditeMin && humidite <= humiditeMax && azote >= azoteMin && azote <= azoteMax ;
     }


     @Override
     public String toString() {
          return "ExigPedologiques{pH:[" + phMin + "-" + phMax + "], humidite:[" + humiditeMin + "-" + humiditeMax
                  + "], azote:[" + azoteMin + "-" + azoteMax + "], pluviMin:" + pluviMin + "}";
     }
}