package dk.au.mad21spring.animalbank;

import android.graphics.Bitmap;
import android.location.Location;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;
import java.util.Map;

public class AnimalFireStoreModel {
    private String name;
    private String description;
    private Date date;
    private String imageURI;
    private double latitude;
    private double longitude;

    public AnimalFireStoreModel() {
        //empty contructor needed with firebase
    }


    public AnimalFireStoreModel(Animal animal) {
        this.name = animal.name;
        this.description = animal.description;
        this.date = animal.date;
        this.latitude = animal.latitude;
        this.longitude = animal.longitude;
    }

    public AnimalFireStoreModel(Map animalMap) {
        this.name = animalMap.get("name") != null ? (String) animalMap.get("name") : null;
        this.description = animalMap.get("description") != null ? (String) animalMap.get("description") : null;
        //this.date = animalMap.get("date") != null ? (Date) animalMap.get("date") : null;
        this.latitude = (double) animalMap.get("latitude");
        this.longitude = (double) animalMap.get("longitude");
        //this.imageURI = animalMap.get("imageURI") != null ? (String) animalMap.get("imageURI") : null;
    }

    public AnimalFireStoreModel(String name, String description, Date date, Location location, String imageURI) {
        this.name = name;
        this.description = description;
        this.date = date;
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

    public String getImageURI() {
        return this.imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
