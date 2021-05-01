package dk.au.mad21spring.animalbank;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//this code was heavily influenced by this android developer tutorial: https://developer.android.com/codelabs/android-training-livedata-viewmodel

@RequiresApi(api = Build.VERSION_CODES.N)
public class Repository {

    private static final String TAG = "Repository";
    public static Repository instance = null;
    ExecutorService executorService;
    FirebaseStorage storage;
    //private final LiveData<List<Animal>> animals;
    private RequestQueue queue;

    private Repository(Context context) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context);
        }
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
        }
        storage = FirebaseStorage.getInstance();
    }

    public static Repository getAnimalRepository(Context context) {
        if (instance == null) {
            instance = new Repository(context);
        }
        return (instance);
    }

    public void tryInsertAnimal(Animal animal) {

    }

    public Animal getAnimal(String name) {
        return new Animal();
    }

    public void updateAnimal(Animal animal){

    }

    public void deleteAnimal() {
    }


    //uses the wiki api to search for a wiki page
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

    //gets the first few sentences from a wiki page
    public void getWikiNotes(String query, final VolleyCallBack callBack) {
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

    //uploads image to firebase storage and returns uri
    public void uploadImage(Bitmap image, final UploadImageCallback callback
    ) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    byte[] data = baos.toByteArray();

                    StorageReference storageRef = storage.getReference();
                    StorageReference imageRef = storageRef.child("public/images/" + Calendar.getInstance().getTime());

                    UploadTask uploadTask = imageRef.putBytes(data);
                    uploadTask.addOnFailureListener(exception -> {
                        Toast toast = Toast.makeText(AnimalBank.getAppContext(), "Couldn't upload image", Toast.LENGTH_SHORT);
                        toast.show();
                    }).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Toast toast = Toast.makeText(AnimalBank.getAppContext(), "Image uploaded", Toast.LENGTH_SHORT);
                        toast.show();
                        callback.onComplete(uri);
                    }));
                } catch (Exception e) {
                }
            }
        });
    }
}