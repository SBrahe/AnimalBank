package dk.au.mad21spring.animalbank.ListView;

import dk.au.mad21spring.animalbank.DataAccess.AnimalFireStoreModel;

public interface IAnimalListActionListener {
    void onAnimalPressed(AnimalFireStoreModel animal);
}
