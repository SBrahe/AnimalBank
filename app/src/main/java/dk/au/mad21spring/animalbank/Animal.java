package dk.au.mad21spring.animalbank;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Animal {
    public String name;
    public String userNotes;
    public String description;
    public Timestamp date;
    public Bitmap image;
    public double latitude;
    public double longitude;
}
