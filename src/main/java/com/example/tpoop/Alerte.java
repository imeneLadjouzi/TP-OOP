package com.example.tpoop;

import java.io.ObjectInputFilter;

public class Alerte {//implements Suspendable
    private Object releve; //releve
    private ObjectInputFilter.Status status;
    private Niveau_gravité gravité;
    public Alerte(Object releve) {
        this.releve = releve;
    }
    public void change_status(){

    }


}
