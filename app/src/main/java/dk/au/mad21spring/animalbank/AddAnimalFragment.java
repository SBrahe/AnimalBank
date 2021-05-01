package dk.au.mad21spring.animalbank;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;

import static dk.au.mad21spring.animalbank.Constants.ANIMAL_REF_INTENT_EXTRA;

public class AddAnimalFragment extends Fragment {

    public static final String TAG = "AddAnimalFragment";

    FirebaseFirestore db;
    Repository repo;
    private AddAnimalFragmentListener listener;
    private EditText txtEditAnimalName;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        repo = Repository.getAnimalRepository(getActivity().getApplicationContext());

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
        CameraParentFragment cameraParentFragment = (CameraParentFragment)this.getParentFragment();
        Animal animal = new Animal();
        animal.name = txtEditAnimalName.getText().toString();
        animal.location = cameraParentFragment.getLocationAtCapture();
        animal.date = Calendar.getInstance().getTime();
        animal.image = cameraParentFragment.getCapturedImage();
        repo.insertAnimal(animal,(documentReference)->{
            Intent intent = new Intent(getActivity(), InfoActivity.class);
            intent.putExtra(ANIMAL_REF_INTENT_EXTRA, documentReference.getPath()); //pass image path to info activity
            startActivity(intent);
        },(error)->{});
    }

    public interface AddAnimalFragmentListener {
        void onDiscardPressed();
    }
}