package dk.au.mad21spring.animalbank;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

// Note: Some code in this hand-in has been inspired by external sources.
// Sources include the MAD lessons as well as code forums on the web.
// Links to important external resources are provided directly in the code.

public class AnimalApp extends Application {

    private static Context context;

    public static Context getAppContext() {
        return AnimalApp.context;
    }

    public void onCreate() {
        super.onCreate();
        AnimalApp.context = getApplicationContext();
    }
}
