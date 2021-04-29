package dk.au.mad21spring.animalbank;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Future;

@RequiresApi(api = Build.VERSION_CODES.N)
public class AddAnimalFragment extends Fragment {

    private static final String TAG = "AddAnimalFragment";

    FirebaseFirestore db;
    Repository repo;

    public interface AddAnimalFragmentListener {
        public void onDiscardPressed();
    }

    private AddAnimalFragmentListener listener;
    private EditText txtEditAnimalName;

    @Override
    public void onAttach(@NonNull Context context) {
        listener = (AddAnimalFragmentListener)context;
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
        //TODO: Save image to db along with geodata and animal name
        //Image can be grabbed from context.

        addAnimalToDB();
        Intent intent = new Intent(getActivity(), InfoActivity.class);
        startActivity(intent);
    }

    public void addAnimalToDB() {

        DocumentReference animalRef = db.collection("animals").document();

        Map<String, Object> animalMap = new HashMap<>();
        animalMap.put("name", txtEditAnimalName.getText().toString());
        animalRef.set(animalMap);

        repo.searchForWikiPage(txtEditAnimalName.getText().toString(), new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject ApiResponse) {
                String animalname = null;
                try {
                    animalname = ApiResponse.getJSONObject("query").getJSONArray("search").getJSONObject(0).getString("title");
                } catch (JSONException e) {
                    animalname = "error";
                    e.printStackTrace();
                }
                Log.d(TAG, "animalname:");
                Log.d(TAG, animalname);
                repo.requestWikiPage(animalname, new VolleyCallBack() {
                    @Override
                    public void onSuccess(JSONObject ApiResponse) {
                        //code taken from https://stackoverflow.com/questions/7304002/how-to-parse-a-dynamic-json-key-in-a-nested-json-result
                        try {
                            JsonObject apiResponseAsJson = (JsonObject) new JsonParser().parse(ApiResponse.getJSONObject("query").getJSONObject("pages").toString());;
                            Log.d(TAG, "onSuccess: "+ apiResponseAsJson.toString());

                            JsonObject pages = apiResponseAsJson.getAsJsonObject();

                            for (Map.Entry<String, JsonElement> entry :  pages.entrySet()) {
                                JsonObject entryAsJson = entry.getValue().getAsJsonObject();
                                animalRef.update("wikiNotes", entryAsJson.get("extract").getAsString());
                                Log.d(TAG, "added wiki page to animal!");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError() {
                        Log.d(TAG, "requestWikiPage: could not find animal wiki page");
                    }
                });
            }
            @Override
            public void onError() {
                Log.d(TAG, "searchForWikiPage: could not find animal wiki page!");
            }
        });


    }
}