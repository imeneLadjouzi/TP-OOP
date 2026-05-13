package com.example.tpoop;

import java.util.ArrayList;
import java.util.List;

public class ProductionRecord {
    private TypeProd type;
    private List<Double> records;

    public ProductionRecord(TypeProd type) {
        this.type = type;
        this.records = new ArrayList<>();
    }

    public void record(double val) {
        records.add(val);
    }

    public double getLatest() {
        return records.isEmpty() ? 0.0 : records.get(records.size() - 1);
    }

    public double getTotal() {
        return records.stream().mapToDouble(Double::doubleValue).sum();
    }

    public TypeProd getType() { return type; }
    public List<Double> getHistory() { return records; }

    public void display() {
        System.out.println("  Production (" + type + ") : dernier=" + getLatest()
                + " " + type.getUnite() + " | total=" + getTotal() + " " + type.getUnite()
                + " | nb releves=" + records.size());
    }
}