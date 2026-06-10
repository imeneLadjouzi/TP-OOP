package com.example.tpoop;

public class ExigPedologiques {
     PlageSeuils ph;
     PlageSeuils humidite;
     PlageSeuils azote;

     public ExigPedologiques(double phMin, double phMax, double humiditeMin, double humiditeMax,
                             double azoteMin, double azoteMax) {
          ph=new PlageSeuils(phMin,phMax);
          humidite=new PlageSeuils(humiditeMin, humiditeMax);
          azote=new PlageSeuils(azoteMin, azoteMax);
     }

     void configurerTemp(double phMin, double phMax) {
          ph=new PlageSeuils(phMin,phMax);
     }
     void configurerOxygen(double humiditeMin, double humiditeMax) {
          humidite=new PlageSeuils(humiditeMin,humiditeMax);
     }
     void configurerPh(double azoteMin, double azoteMax) {
          azote=new PlageSeuils(azoteMin,azoteMax);
     }

     public double getPhMax(){
          return ph.getMax();
     }
     public double getHumMax(){
          return humidite.getMax();
     }
     public double getAzoteMax(){
          return azote.getMax();
     }
     public double getPhMin(){
          return ph.getMin();
     }
     public double getHumMin(){
          return humidite.getMin();
     }
     public double getAzoteMin(){
          return azote.getMin();
     }

     public Niveau_gravite evaluerPh(double valPh){
          return ph.evaluer(valPh);
     }

     public Niveau_gravite evaluerhum(double valhum){
          return humidite.evaluer(valhum);
     }

     public Niveau_gravite evalueraz(double valaz){
          return azote.evaluer(valaz);
     }

     public Niveau_gravite evaluer(double valph, double valhumidite, double valazote){
          if (ph.evaluer(valph)== Niveau_gravite.CRITIQUE || humidite.evaluer(valhumidite)== Niveau_gravite.CRITIQUE || azote.evaluer(valazote)== Niveau_gravite.CRITIQUE){
               return Niveau_gravite.CRITIQUE;
          }
          else if (ph.evaluer(valph)== Niveau_gravite.AVERTISSEMENT || humidite.evaluer(valhumidite)== Niveau_gravite.AVERTISSEMENT || azote.evaluer(valazote)== Niveau_gravite.AVERTISSEMENT){
               return Niveau_gravite.AVERTISSEMENT;
          }
          else return Niveau_gravite.INFO;
     }

     @Override
     public String toString() {
          return "ExigPedologiques{pH:" +ph.toString() + ", humidite:" + humidite.toString() + ", azote:" + azote.toString();
     }
}