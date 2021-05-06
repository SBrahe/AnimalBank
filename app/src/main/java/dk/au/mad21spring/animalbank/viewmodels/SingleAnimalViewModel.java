package dk.au.mad21spring.animalbank.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.function.Consumer;

import dk.au.mad21spring.animalbank.AnimalFireStoreModel;
import dk.au.mad21spring.animalbank.Repository;

public class SingleAnimalViewModel extends ViewModel {
    private Repository repo;
    private String animalFireStorePath;
    private LiveData<AnimalFireStoreModel> animal;

    public SingleAnimalViewModel(Application application, String animalFireStorePath) {
        this.repo = repo;

    }

    public LiveData<AnimalFireStoreModel> getAnimal(){
        if (this.animal == null){
            this.animal = repo.getAnimal(this.animalFireStorePath);
        }
        return this.animal;
    }

    public void setAnimal(AnimalFireStoreModel animalFireStoreModel){
        repo.updateAnimal(animalFireStoreModel,(docRef)->{},(error -> {}));
    }

    public void deleteAnimal(Runnable onSuccess, Consumer<Exception> onError){
        repo.deleteAnimal(animalFireStorePath, onSuccess, onError);
    }
}
