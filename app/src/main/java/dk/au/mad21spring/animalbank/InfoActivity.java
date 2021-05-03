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
    private TextView txtSpottedDate;
    private TextView txtSpottedNear;
    private TextView txtUserNotes;
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
        txtSpottedDate = findViewById(R.id.spottedDateText);
        txtSpottedNear = findViewById(R.id.spottedLocationText);
        txtUserNotes = findViewById(R.id.userNotesText);
        txtWikiNotes = findViewById(R.id.wikiNotesText);

        //initialize back button
        btnBack.setOnClickListener(v -> onBackPressed());

        //get doc ref from intent extras.
        DocumentReference animalRef = db.document(getIntent().getStringExtra("animalRef"));
        //Attach listener that updates on changes.
        animalRef.addSnapshotListener(this,(snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                AnimalFireStoreModel animal = snapshot.toObject(AnimalFireStoreModel.class);
                txtAnimalName.setText(animal.getName());
                txtSpottedDate.setText(animal.getDate().toString());
                txtSpottedNear.setText(animal.getLatitude()+ ", " + animal.getLongitude());
                txtWikiNotes.setText(animal.getDescription());
            } else {
                Log.d(TAG, "Current data: null");
            }
        });
    }

    // onBackPressed should not be overridden since the info view can be accessed both from the map and the list view.
    // The default back activity can handle this.

    //go to list when back button is pressed
    //@Override
    //public void onBackPressed() {
    //    super.onBackPressed();
    //    startActivity(new Intent(InfoActivity.this, ListFragment.class));
    //    finish();
    //}
}