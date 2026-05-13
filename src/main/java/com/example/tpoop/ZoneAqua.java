package com.example.tpoop;

import java.util.ArrayList;
import java.util.List;

public class ZoneAqua extends Zone{
    private int Nbanimaux;
    private String Espece;
    private ProgAlimentaire progAlim;
    //private List<AquaticHealthEvent> healthEvents;
    private ProductionRecord productionRecord;
    private ArrayList<Capteurs> capteurs;
    public ZoneAqua (String code, String name, Status status){
        super(code,name,status);
    }

    public ZoneAqua(String var1, String var2,Status var3, String espece) {
        super(var1, var2,var3);
        this.Espece = espece;
        this.capteurs = new ArrayList();
        this.productionRecord = new ProductionRecord(TypeProd.POIDS_RECOLTE);
        //this.healthEvents = new ArrayList();
    }

    public void setFeedingProgram(ProgAlimentaire var1) {
        this.progAlim = var1;
    }

    public ProgAlimentaire getProgAlim() {
        return this.progAlim;
    }

    public void recordProduction(double var1) {
        this.productionRecord.record(var1);
    }

    public void suspend() {
        super.suspendre();
       // this.suspendAllSensors();
    }




    //add sensor

    @Override
    public void display() {
        super.display();
        System.out.println("Nombre d'especes aquacoles: "+Nbanimaux);
    }

}
