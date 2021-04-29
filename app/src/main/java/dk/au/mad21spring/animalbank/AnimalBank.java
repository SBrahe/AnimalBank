package dk.au.mad21spring.animalbank;

import android.app.Application;
import android.content.Context;

// Note: Some code in this hand-in has been inspired by external sources.
// Sources include the MAD lessons as well as code forums on the web.
// Links to important external resources are provided directly in the code.

public class AnimalBank extends Application {

    private static Context context;
    private static AnimalBank instance;

    public static Context getAppContext() {
        return AnimalBank.context;
    }

    public static AnimalBank getInstance() {
        return AnimalBank.instance;
    }

    public void onCreate() {
        super.onCreate();
        AnimalBank.context = getApplicationContext();
        AnimalBank.instance = this;
    }
}