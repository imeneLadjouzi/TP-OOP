package com.example.tpoop;

public class Prod {
    private double val;
    private TypeProd prod;

    public Prod(double val, TypeProd prod) {
        this.val = val;
        this.prod = prod;
    }

    public double getVal() { return val; }
    public TypeProd getProd() { return prod; }

    @Override
    public String toString() {
        return val + " " + prod.getUnite() + " (" + prod + ")";
    }
}