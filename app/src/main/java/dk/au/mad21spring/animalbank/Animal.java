package dk.au.mad21spring.animalbank;

import android.graphics.Bitmap;

import com.google.firebase.Timestamp;
import com.google.type.Date;

public class Animal {
    private String name;
    private String description;
    private Timestamp date;
    private Bitmap image;

    public Animal(){
        //empty contructor needed with firebase
    }
    public Animal(String name,String description,Timestamp date,Bitmap image)
    {
        this.name = name;
        this.description = description;
        this.date = date;
        this.image = image;
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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
