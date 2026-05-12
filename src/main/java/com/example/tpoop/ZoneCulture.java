package com.example.tpoop;

public class ZoneCulture extends Zone {
    Object[] capteurs; //classe Capteur
    Culture[] cultures;
    private StadeCroissance stade_croiss = StadeCroissance.GERMINATION;
    public void  update_croissance(StadeCroissance s){
    stade_croiss = s;
    }
    public ZoneCulture (String code, String name, String status){
        super(code,name,status);
    }
    @Override
    public void display() {
        System.out.println("Zone Culture: "+name+" code: "+code+"Status: "+status);
    }

    @Override
    public void setStatus(String status){
        this.status = status;
    }
    public void displayCulture(){
        for (int i=0; i<cultures.length; i++){
            System.out.println(cultures[i].toString());
        }
    }
    public StadeCroissance displayCroissance(){
        return stade_croiss;
    }


}
