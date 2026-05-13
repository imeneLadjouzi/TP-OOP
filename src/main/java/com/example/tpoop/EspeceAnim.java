package com.example.tpoop;

public class EspeceAnim {
    private TypeAnimal type;
    private String name;

    public EspeceAnim(TypeAnimal type, String name) {
        this.type = type;
        this.name = name;
    }

    public TypeAnimal getType() { return type; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}