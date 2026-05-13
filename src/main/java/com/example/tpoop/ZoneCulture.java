
package com.example.tpoop;
import java.util.ArrayList;

public class ZoneCulture extends Zone {

    ArrayList<Culture> cultures;

    public ZoneCulture (String code, String name, Status status){
            super(code,name,status);
        }

        @Override
        public void display() {
            super.display();
            System.out.println("Nombre de cultures: "+ cultures.size());
        }
        @Override
        public void setStatus(Status status){
            this.status = status;
        }
        public void displayCulture(){
            for (Culture c: cultures){
                c.display();
            }
        }
    public void addCulture(Culture c){
        cultures.add(c);
    }
    public void updateStadeCroiss(StadeCroissance s){
        for(Culture c : cultures){
            c.updateStadeCroiss(s);
        }
    }
    public void displayStadeCroiss(){
        for (Culture c : cultures){
            System.out.println("Stade de croissance: "+c.getStadeCroiss());
        }

    }

}




