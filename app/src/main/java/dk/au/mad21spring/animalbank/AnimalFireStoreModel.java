package dk.au.mad21spring.animalbank;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;


public class AnimalFireStoreModel {
    @Exclude
    public DocumentReference documentReference;
    private String name;
    private String description;
    private Timestamp date;
    private String imageURI;
    private double latitude;
    private double longitude;

    public AnimalFireStoreModel() {
        //empty constructor needed with firebase
    }


    public AnimalFireStoreModel(Animal animal) {
        this.name = animal.name;
        this.description = animal.description;
        this.date = animal.date;
        this.latitude = animal.latitude;
        this.longitude = animal.longitude;
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
