package dk.au.mad21spring.animalbank.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SingleAnimalViewModelFactory implements ViewModelProvider.Factory {
    private String animalFireStorePath;
    private Application application;

    public SingleAnimalViewModelFactory(Application application, String animalFireStorePath) {
        this.animalFireStorePath = animalFireStorePath;
        this.application = application;



    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SingleAnimalViewModel.class)){
            return (T) new SingleAnimalViewModel(this.application, this.animalFireStorePath);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
