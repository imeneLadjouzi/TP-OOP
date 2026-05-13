package com.example.tpoop;

import java.util.ArrayList;
import java.util.List;

public class ProductionRecord {
    private TypeProd type;
    private List<Double> records;

    public ProductionRecord(TypeProd var1) {
        this.type = var1;
        this.records = new ArrayList();
    }

    public void record(double var1) {
        this.records.add(var1);
    }

    public double getLatest() {
        return this.records.isEmpty() ? (double)0.0F : (Double)this.records.get(this.records.size() - 1);
    }

    public TypeProd getType() {
        return this.type;
    }

    public List<Double> getHistory() {
        return this.records;
    }
}
