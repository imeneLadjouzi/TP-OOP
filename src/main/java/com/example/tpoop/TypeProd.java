package com.example.tpoop;

public enum TypeProd {
        LAIT("L"),
        OEUFS("Egg"),
        POIDS_RECOLTE("Kg"),
        RENDEM_CULTURE("Kg");

        private String unite;

        private TypeProd(String var3) {
            this.unite = var3;
        }
}

