package dk.au.mad21spring.animalbank.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import dk.au.mad21spring.animalbank.DataAccess.AnimalFireStoreModel;
import dk.au.mad21spring.animalbank.DataAccess.Repository;

public class AnimalCollectionViewModel extends ViewModel {
    private Repository repo;
    private LiveData<ArrayList<AnimalFireStoreModel>> animals;

    public AnimalCollectionViewModel(Application application) {
        this.repo = Repository.getAnimalRepository(application);
    }

    public LiveData<ArrayList<AnimalFireStoreModel>> getAnimals(){
        if (this.animals == null){
            this.animals = repo.getAllAnimals();
        }
        return this.animals;
    }
}