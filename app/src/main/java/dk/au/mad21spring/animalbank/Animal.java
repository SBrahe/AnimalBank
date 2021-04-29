package dk.au.mad21spring.animalbank;

import android.graphics.Bitmap;

import java.util.Date;

public class Animal {
    private String name;
    private String description;
    private Date date;
    private Bitmap image;

    public Animal(String name,String description,Date date,Bitmap image)
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
