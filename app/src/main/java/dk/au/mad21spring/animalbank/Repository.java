package dk.au.mad21spring.animalbank;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.concurrent.Future;

//this code was heavily influenced by this android developer tutorial: https://developer.android.com/codelabs/android-training-livedata-viewmodel

@RequiresApi(api = Build.VERSION_CODES.N)
public class Repository {

    private static final String TAG = "Repository";
    public static Repository instance = null;
    //private final LiveData<List<Animal>> animals;
    private RequestQueue queue;

    private Repository() {
        if (queue == null) {
            queue = Volley.newRequestQueue(AnimalBank.getAppContext());
        }
    }

    public static Repository getAnimalRepository() {
        if (instance == null) {
            instance = new Repository();
        }
        return (instance);
    }

    public void searchForWikiPage(String query, final VolleyCallBack callBack) {
        String base = "https://en.wikipedia.org/w/api.php?origin=*&action=query&list=search&format=json&srlimit=1&srsearch=";
        String url = base + query;

        JsonObjectRequest wikiPageRequest = new JsonObjectRequest
                (Request.Method.GET,
                        url,
                        null,
                        response -> {
                            Log.d(TAG, "searchForWikiPage: " + response);
                            callBack.onSuccess(response);
                        },
                        error -> callBack.onError());
        queue.add(wikiPageRequest);
    }

    public void requestWikiPage(String query, final VolleyCallBack callBack) {
        String base = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exlimit=1&explaintext=1&exintro=1&redirects=&exsentences=5&titles=";
        String url = base + query;
        JsonObjectRequest wikiPageRequest = new JsonObjectRequest
                (Request.Method.GET,
                        url,
                        null,
                        response -> {
                            Log.d(TAG, "requestWikiPage: " + response);
                            callBack.onSuccess(response);
                        },
                        error ->
                        {
                            Log.d(TAG, "requestWikiPage: error");
                            Log.d(TAG, error.toString());
                            callBack.onError();
                        });
        queue.add(wikiPageRequest);
    }
}

