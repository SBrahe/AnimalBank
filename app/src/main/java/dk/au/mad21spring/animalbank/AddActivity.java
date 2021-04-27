package dk.au.mad21spring.animalbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import static dk.au.mad21spring.animalbank.Constants.IMAGE_EXTRA_NAME;

public class AddActivity extends AppCompatActivity {

    private EditText txtEditAnimalName;
    private ImageView captureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        this.captureView.findViewById(R.id.captureView);
        txtEditAnimalName = findViewById(R.id.editAnimalNameText);
        txtEditAnimalName.requestFocus();

        //open keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        //make enter open info view
        txtEditAnimalName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onEnterPressed();
            }
            return false;
        });


    }

    public void onEnterPressed() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

}