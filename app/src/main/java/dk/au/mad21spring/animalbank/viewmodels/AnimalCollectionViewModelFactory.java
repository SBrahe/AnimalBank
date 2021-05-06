package dk.au.mad21spring.animalbank.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AnimalCollectionViewModelFactory implements ViewModelProvider.Factory {
    private Application application;

    public AnimalCollectionViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AnimalCollectionViewModel.class)){
            return (T) new AnimalCollectionViewModel(this.application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
