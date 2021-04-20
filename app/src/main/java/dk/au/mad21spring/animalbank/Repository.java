package dk.au.mad21spring.animalbank;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

//this code was heavily influenced by this android developer tutorial: https://developer.android.com/codelabs/android-training-livedata-viewmodel

@RequiresApi(api = Build.VERSION_CODES.N)
public class Repository {

    private static final String TAG = "cityRepository";
    public static Repository instance = null;
    //private final LiveData<List<Animal>> animals;
    private RequestQueue queue;

    private Repository(Application app) {
        //db = CityDatabase.getDatabase(app.getApplicationContext());
        //cities = db.cityDAO().getAll();
        if (queue == null) {
            queue = Volley.newRequestQueue(AnimalApp.getAppContext());
        }
    }

    public static Repository getAnimalRepository() {
        return (instance);
    }

    public void searchForWikiPage(String query, final VolleyCallBack callBack) {
        String base = "https://en.wikipedia.org/w/api.php?origin=*&action=query&list=search&format=json&srlimit=1&srsearch=";
        String url = base + query;
        StringRequest wikiPageRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    Log.d(TAG, "api response: " + response);
                    callBack.onSuccess(response);
                },
                (error) -> callBack.onError());
        queue.add(wikiPageRequest);
    }

    public void requestWikiPage(String query, final VolleyCallBack callBack) {
        String base = "http://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exlimit=1&explaintext=1&exintro=1&redirects=&exsentences=5&titles=";
        String url = base + query;
        StringRequest wikiPageRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    Log.d(TAG, "api response: " + response);
                    callBack.onSuccess(response);
                },
                (error) -> callBack.onError());
        queue.add(wikiPageRequest);
    }
}

