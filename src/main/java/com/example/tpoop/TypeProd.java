package com.example.tpoop;

public enum TypeProd {
        LAIT("L"),
        OEUFS("Egg"),
        POIDS_RECOLTE("Kg"),
        RENDEM_CULTURE("Kg/ha");

        private final String unite;

        TypeProd(String unite) {
                this.unite = unite;
        }

        public String getUnite() {
                return unite;
        }
}