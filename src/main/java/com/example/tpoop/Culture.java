package com.example.tpoop;

public class Culture {
    private String nom;
    private String datePlantation;
    private String dateRecolte;
    private StadeCroissance stadeCroiss;
    private ExigPedologiques exigPed;

    public Culture(String nom, String datePlantation, String dateRecolte,
                   StadeCroissance stadeCroiss, ExigPedologiques exigPed) {
        this.nom = nom;
        this.datePlantation = datePlantation;
        this.dateRecolte = dateRecolte;
        this.stadeCroiss = stadeCroiss;
        this.exigPed = exigPed;
    }

    public String getNom() { return nom; }
    public String getDatePlantation() { return datePlantation; }
    public String getDateRecolte() { return dateRecolte; }
    public StadeCroissance getStadeCroiss() { return stadeCroiss; }
    public ExigPedologiques getExigPed() { return exigPed; }

    public void updateStadeCroiss(StadeCroissance s) {
        this.stadeCroiss = s;
    }

    public void displayStadeCroiss() {
        System.out.println("  Stade de croissance de [" + nom + "]: " + stadeCroiss);
    }

    public void display() {
        try{
            System.out.println("  Culture : " + nom);
            System.out.println("  Date de plantation : " + datePlantation);
            System.out.println("  Date de recolte    : " + dateRecolte);
            displayStadeCroiss();
            if (exigPed != null)
                System.out.println("  Exigences : " + exigPed);
        }catch (NullPointerException e){
            System.out.println("  Culture Null");
        }

    }
}