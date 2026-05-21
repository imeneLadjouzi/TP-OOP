package com.example.tpoop;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class Prod {
    private double val;
    private TypeProd prod;
    private String date;
    public Prod(double val, TypeProd prod) {
        this.val = val;
        this.prod = prod;
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public double getVal() { return val; }
    public TypeProd getProd() { return prod; }
    public String getDate(){ return date; }


    @Override
    public String toString() {
        return val + " " + prod.getUnite() + " (" + prod + ")";
    }
    public void displayProduction(){
        System.out.println("  Production : " + val + " " + prod.getUnite() + " (" + prod + ") enregistre le: " + date);
    }
}