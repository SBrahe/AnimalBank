package dk.au.mad21spring.animalbank;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static dk.au.mad21spring.animalbank.AnimalFireStoreModel.DESCRIPTION_FIELD;
import static dk.au.mad21spring.animalbank.AnimalFireStoreModel.IMAGE_URI_FIELD;
import static dk.au.mad21spring.animalbank.Constants.ANIMAL_COLLECTION_NAME;
import static dk.au.mad21spring.animalbank.Constants.IMAGE_URL_INTENT_EXTRA;

//this code was heavily influenced by this android developer tutorial: https://developer.android.com/codelabs/android-training-livedata-viewmodel

@RequiresApi(api = Build.VERSION_CODES.N)
public class Repository {

    private static final String TAG = "Repository";
    public static Repository instance = null;
    ExecutorService executorService;
    FirebaseStorage storage;
    //private final LiveData<List<Animal>> animals;
    private RequestQueue queue;
    private Context context;

    private Repository(Context context) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context);
        }
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
        }
        storage = FirebaseStorage.getInstance();
        this.context = context;
    }

    public static Repository getAnimalRepository(Context context) {
        if (instance == null) {
            instance = new Repository(context);
        }
        return (instance);
    }

    //code inspired by https://firebase.google.com/docs/storage/android/upload-files
    public void insertAnimal(Animal animal, Consumer<DocumentReference> onSuccess, Consumer<Error> onError) {
        AnimalFireStoreModel toUpload = new AnimalFireStoreModel(animal);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference animalRef = db.collection(ANIMAL_COLLECTION_NAME).document(); //create new animal document in firestore
        animalRef.set(toUpload);
        this.uploadImage(animal.image, imageUri -> {
            animalRef.update(IMAGE_URI_FIELD, imageUri.toString());
            Log.d(TAG, "uploadImage: uploaded image and update db, imageuri: " + imageUri);
        });
        this.trySetWikiInfo(animal.name, animalRef, (e) -> {
        });
        //Refactor to Tasks.whenAllComplete()
    }

    public void getAllAnimals(Consumer<AnimalFireStoreModel> doForEach){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(ANIMAL_COLLECTION_NAME).get().onSuccessTask((snapshot)->{
            snapshot.iterator().forEachRemaining((item)->{
                AnimalFireStoreModel animal = item.toObject(AnimalFireStoreModel.class);
                animal.documentReference = item.getReference();
                doForEach.accept(animal);
            });
            return null;
        });
    }

    public LiveData<AnimalFireStoreModel> getAnimal(String animalFireStorePath) {
        MutableLiveData<AnimalFireStoreModel> animalLiveData = new MutableLiveData<AnimalFireStoreModel>();
        animalLiveData.setValue(new AnimalFireStoreModel());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference animalRef = db.document(animalFireStorePath);
        animalRef.addSnapshotListener(executorService, (snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                AnimalFireStoreModel animal = snapshot.toObject(AnimalFireStoreModel.class);
                animal.documentReference = snapshot.getReference();
                animalLiveData.setValue(animal);
            } else {
                Log.d(TAG, "Current data: null");
            }
        });
        return animalLiveData;
    }

    public void updateAnimal(AnimalFireStoreModel animal, Consumer<DocumentSnapshot> onSuccess, Consumer<Error> onError) {

    }

    public void deleteAnimal(AnimalFireStoreModel animal, Runnable onSuccess, Consumer<Error> onError) {

    }

    public void deleteAnimal(String animalDocumentId, Runnable onSuccess, Consumer<Exception> onError) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(ANIMAL_COLLECTION_NAME).document(animalDocumentId).delete().addOnSuccessListener((a)->{onSuccess.run();}).addOnFailureListener((e)->{onError.accept(e);});
    }


    private void trySetWikiInfo(String AnimalName, DocumentReference documentReference, Consumer<VolleyError> outerOnError) {
        this.searchForWikiPage(AnimalName, new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject ApiResponse) {
                try {
                    //get the title of the wiki page from the wiki search api
                    String animalname = null;
                    animalname = ApiResponse.getJSONObject("query").getJSONArray("search").getJSONObject(0).getString("title");
                    Log.d(TAG, "animalname:");
                    Log.d(TAG, animalname);

                    //get the first few sentences from wiki getpage api
                    getWikiNotes(animalname, new VolleyCallBack() {
                        @Override
                        public void onSuccess(JSONObject ApiResponse) {
                            //code inspired by https://stackoverflow.com/questions/7304002/how-to-parse-a-dynamic-json-key-in-a-nested-json-result
                            try {
                                JsonObject apiResponseAsJson = (JsonObject) new JsonParser().parse(ApiResponse.getJSONObject("query").getJSONObject("pages").toString());
                                Log.d(TAG, "onSuccess: " + apiResponseAsJson.toString());

                                JsonObject pages = apiResponseAsJson.getAsJsonObject();

                                //add wikinotes to animal in firestore
                                for (Map.Entry<String, JsonElement> entry : pages.entrySet()) {
                                    JsonObject entryAsJson = entry.getValue().getAsJsonObject();
                                    documentReference.update(DESCRIPTION_FIELD, entryAsJson.get("extract").getAsString());
                                    Log.d(TAG, "getWikiNotes: added wiki notes to animal in db!");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(VolleyError error) {
                            Log.d(TAG, "getWikiNotes: could not get wiki notes, even though wiki page was found");
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.d(TAG, "searchForWikiPage: could not find animal wiki page!");
                outerOnError.accept(error);
            }
        });

    }

    //uses the wiki api to search for a wiki page
    private void searchForWikiPage(String query, final VolleyCallBack callBack) {
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
                        error -> callBack.onError(error));
        queue.add(wikiPageRequest);
    }

    //gets the first few sentences from a wiki page
    private void getWikiNotes(String query, final VolleyCallBack callBack) {
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
                            callBack.onError(error);
                        });
        queue.add(wikiPageRequest);
    }

    //uploads image to firebase storage and returns uri
    private void uploadImage(Bitmap image, final UploadImageCallback callback
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
                        Toast toast = Toast.makeText(context, "Couldn't upload image", Toast.LENGTH_SHORT);
                        toast.show();
                    }).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Toast toast = Toast.makeText(context, "Image uploaded", Toast.LENGTH_SHORT);
                        toast.show();
                        callback.onComplete(uri);
                    }));
                } catch (Exception e) {
                }
            }
        });
    }

    public String getLocalityFromLatLong(double latitude, double longitude)  {
        // Uses code from https://stackoverflow.com/questions/2296377/how-to-get-city-name-from-latitude-and-longitude-coordinates-in-google-maps
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            return addresses.get(0).getLocality();
        }
        return "";
    }
}