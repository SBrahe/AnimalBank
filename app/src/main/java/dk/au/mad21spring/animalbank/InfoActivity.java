package dk.au.mad21spring.animalbank;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {

    private Button btnBack;
    private Button btnDelete;
    private TextView txtAnimalName;
    private TextView txtSpottedDateTitle;
    private TextView txtSpottedDate;
    private TextView txtSpottedNearTitle;
    private TextView txtSpottedNear;
    private TextView txtUserNotesTitle;
    private TextView txtUserNotes;
    private TextView txtWikiNotesTitle;
    private TextView txtWikiNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //initialize widgets
        btnBack = findViewById(R.id.backBtn);
        btnDelete = findViewById(R.id.deleteBtn);
        txtAnimalName = findViewById(R.id.animalNameText);
        txtSpottedDateTitle = findViewById(R.id.spottedDateTitleText);
        txtSpottedDate = findViewById(R.id.spottedDateText);
        txtSpottedNearTitle = findViewById(R.id.spottedLocationTitleText);
        txtSpottedNear = findViewById(R.id.spottedLocationText);
        txtUserNotesTitle = findViewById(R.id.userNotesTitleText);
        txtUserNotes = findViewById(R.id.userNotesText);
        txtWikiNotesTitle = findViewById(R.id.wikiNotesTitleText);
        txtWikiNotes = findViewById(R.id.wikiNotesText);

        //initialize back button
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    //go to list when back button is pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(InfoActivity.this, ListActivity.class));
        finish();
    }
}