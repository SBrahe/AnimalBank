package dk.au.mad21spring.animalbank;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


public class AddAnimalFragment extends Fragment {

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
        Intent intent = new Intent(getActivity(), InfoActivity.class);
        startActivity(intent);
    }
}