package com.example.tpoop;

public class ZoneAqua extends Zone{
    private int Nbanimaux;
    public ZoneAqua (String code, String name, String status){
        super(code,name,status);
    }
    @Override
    public void display() {
        System.out.println("Zone Aqua: "+name+" code: "+code+"status: "+status);
    }
    @Override
    public void setStatus(String status){
        this.status = status;
    }

}
