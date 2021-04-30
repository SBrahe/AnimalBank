package dk.au.mad21spring.animalbank;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.N)
public class AddAnimalFragment extends Fragment {

    private static final String TAG = "AddAnimalFragment";

    FirebaseFirestore db;
    Repository repo;
    private AddAnimalFragmentListener listener;
    private EditText txtEditAnimalName;

    @Override
    public void onAttach(@NonNull Context context) {
        listener = (AddAnimalFragmentListener) context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        repo = Repository.getAnimalRepository();

        View view = inflater.inflate(R.layout.fragment_add_animal, container, false);
        txtEditAnimalName = view.findViewById(R.id.editAnimalNameText);
        txtEditAnimalName.requestFocus();
        //open keyboard
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        //make enter open info view
        txtEditAnimalName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onEnterPressed();
            }
            return false;
        });
        return view;
    }

    public void onEnterPressed() {

        addAnimalToDB();
        Intent intent = new Intent(getActivity(), InfoActivity.class);
        intent.putExtra("image", "test");
        startActivity(intent);
    }

    //code inspired by https://firebase.google.com/docs/storage/android/upload-files
    public void addAnimalToDB() {
        CameraActivity activity = (CameraActivity) getActivity();

        DocumentReference animalRef = db.collection("animals").document();

        Map<String, Object> animalMap = new HashMap<>();
        animalMap.put("name", txtEditAnimalName.getText().toString());
        animalMap.put("location", activity.getLocationAtCapture());
        animalRef.set(animalMap);

        Bitmap image = activity.getCapturedImage();
        repo.uploadImage(image, imageUri -> {
            animalRef.update("imageUri", imageUri.toString());
            Log.d(TAG, "uploadImage: uploaded image and update db, imageuri: " + imageUri);
        });

        repo.searchForWikiPage(txtEditAnimalName.getText().toString(), new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject ApiResponse) {
                String animalname = null;
                try {
                    animalname = ApiResponse.getJSONObject("query").getJSONArray("search").getJSONObject(0).getString("title");
                    Log.d(TAG, "animalname:");
                    Log.d(TAG, animalname);
                    repo.getWikiNotes(animalname, new VolleyCallBack() {
                        @Override
                        public void onSuccess(JSONObject ApiResponse) {
                            //code inspired by https://stackoverflow.com/questions/7304002/how-to-parse-a-dynamic-json-key-in-a-nested-json-result
                            try {
                                JsonObject apiResponseAsJson = (JsonObject) new JsonParser().parse(ApiResponse.getJSONObject("query").getJSONObject("pages").toString());
                                Log.d(TAG, "onSuccess: " + apiResponseAsJson.toString());

                                JsonObject pages = apiResponseAsJson.getAsJsonObject();

                                for (Map.Entry<String, JsonElement> entry : pages.entrySet()) {
                                    JsonObject entryAsJson = entry.getValue().getAsJsonObject();
                                    animalRef.update("wikiNotes", entryAsJson.get("extract").getAsString());
                                    Log.d(TAG, "getWikiNotes: added wiki notes to animal in db!");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError() {
                            Log.d(TAG, "getWikiNotes: could not get wiki notes, even though wiki page was found");
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                Log.d(TAG, "searchForWikiPage: could not find animal wiki page!");
            }
        });
    }

    public interface AddAnimalFragmentListener {
        void onDiscardPressed();
    }
}