package dk.au.mad21spring.animalbank;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class InfoActivity extends AppCompatActivity {

    private static final String TAG = "InfoActivity";

    FirebaseFirestore db;

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

        db = FirebaseFirestore.getInstance();

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

        //get image reference from firestore by passing path
        DocumentReference animalRef = db.document(getIntent().getStringExtra("animalRef"));

        //download info from firestore
        animalRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                String wikinotes = (String)snapshot.get("wikiNotes");
                Log.d(TAG, "Current data: "+ wikinotes);
                txtAnimalName.setText(snapshot.get("name").toString());
                txtSpottedDate.setText(snapshot.get("date").toString());
                txtSpottedNear.setText(snapshot.get("location").toString());
                txtWikiNotes.setText((String)snapshot.get("wikiNotes"));
            } else {
                Log.d(TAG, "Current data: null");
            }
        });
    }

    //go to list when back button is pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(InfoActivity.this, ListActivity.class));
        finish();
    }
}