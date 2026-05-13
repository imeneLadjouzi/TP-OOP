package com.example.tpoop;

import java.util.Map;
public class Culture {
    private String DatePlantation;
    private String DateRecolte;
    private StadeCroissance StadeCroiss;
    private ExigPedologiques ExigPed;


    public Culture(String datePlantation, String dateRecolte, StadeCroissance stadeCroiss, ExigPedologiques exigPed) {
        this.DatePlantation = datePlantation;
        this.DateRecolte = dateRecolte;
        this.StadeCroiss = stadeCroiss;
        this.ExigPed = exigPed;
    }
    public StadeCroissance getStadeCroiss(){
        return StadeCroiss;
    }
    public void updateStadeCroiss(StadeCroissance s){
        StadeCroiss = s;
    }
    public void displayStadeCroiss(){
        System.out.println("Stade de croissance: "+ StadeCroiss);
    }

    public void display(){
        System.out.println("Date de plantation: "+DatePlantation);
        System.out.println("Date de recolte: "+DateRecolte);
        displayStadeCroiss();
    }
}
