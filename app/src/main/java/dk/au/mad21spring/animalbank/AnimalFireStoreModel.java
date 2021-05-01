package dk.au.mad21spring.animalbank;

import android.graphics.Bitmap;
import android.location.Location;

import java.util.Date;

public class AnimalFireStoreModel {
    private String name;
    private String description;
    private Date date;
    private Location location;
    private String imageURI;

    public AnimalFireStoreModel(){
        //empty contructor needed with firebase
    }


    public AnimalFireStoreModel(Animal animal){
        this.name = animal.name;
        this.description = animal.description;
        this.date = animal.date;
        this.location = animal.location;
    }

    public AnimalFireStoreModel(String name,String description,Date date,Location location, String imageURI)
    {
        this.name = name;
        this.description = description;
        this.date = date;
        this.location = location;
        this.imageURI = imageURI;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImageURI(){return this.imageURI;}

    public void setImageURI(String imageURI){this.imageURI = imageURI;}

    public Location getLocation(){return this.location;}

    public void setLocation(Location location){this.location = location;}
}
