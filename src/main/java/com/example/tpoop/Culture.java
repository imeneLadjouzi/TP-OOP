package com.example.tpoop;

import java.util.Map;
public class Culture extends ZoneCulture{
    private String DatePlantation;
    private String DateRecolte;
    //private StadeCroissance stadeCroiss;

    private Map<int[], int[]> ExigencesPedolog;
    public Culture(String code, String name, String status, String DatePlantation, String DateRecolte, StadeCroissance stadeCroiss, Map<int[], int[]> ExigencesPedolog) {
        super(code, name, status);
        this.DatePlantation = DatePlantation;
        this.DateRecolte = DateRecolte;
        //this.stadeCroiss = stadeCroiss;
        this.ExigencesPedolog = ExigencesPedolog;
    }
}
