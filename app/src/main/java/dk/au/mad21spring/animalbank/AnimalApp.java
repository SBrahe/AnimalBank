package dk.au.mad21spring.animalbank;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

//note: ALL code in this hand-in has been inspired by external sources. Ideas and solutions have been inspired from many places including the MAD lessons or code forums in the web. I claim none of this as my own.

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
