package com.example.tpoop;
import java.util.ArrayList;

public class ZoneElevage extends Zone {


    ArrayList<Animal> animaux;
    private ProgAlimentaire progAlim;
    private String name;

    public  ZoneElevage( String code,  String name, Status status){
        super(code,name,status);
    }

    @Override
    public void display() {
        super.display();
        System.out.println("Nombre d'animaux: "+ animaux.size());
    }
    @Override
    public void setStatus(Status status){
        this.status = status;
    }

    public void addAnimal(Animal a){
        animaux.add(a);
        }
    }


