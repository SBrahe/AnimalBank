package dk.au.mad21spring.animalbank;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;


public class AddAnimalFragment extends Fragment {

    private static final String TAG = "AddAnimalFragment";

    FirebaseFirestore db;

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
        Map<String, Object> animal = new HashMap<>();
        animal.put("name", "test");

        db.collection("animals")
                .add(animal)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Animal added with ID: " + documentReference.getId()))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding animal", e);
                    }
                });
    }
}