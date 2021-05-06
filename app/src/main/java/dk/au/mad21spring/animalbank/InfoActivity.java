package dk.au.mad21spring.animalbank;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import dk.au.mad21spring.animalbank.Constants;

import static dk.au.mad21spring.animalbank.AnimalFireStoreModel.USER_NOTES_FIELD;
import static dk.au.mad21spring.animalbank.Constants.ANIMAL_REF_INTENT_EXTRA;
import static dk.au.mad21spring.animalbank.Constants.IMAGE_URL_INTENT_EXTRA;

public class InfoActivity extends AppCompatActivity {

    private static final String TAG = "InfoActivity";

    FirebaseFirestore db;
    DocumentReference animalRef;

    private Button btnBack;
    private Button btnDelete;
    private TextView txtAnimalName;
    private TextView txtSpottedDate;
    private TextView txtSpottedNear;
    private TextView txtUserNotes;
    private TextView txtWikiNotes;
    private ImageView userImageView;

    Repository repo;

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
        userImageView = findViewById(R.id.userImageView);


        repo = Repository.getAnimalRepository(getApplicationContext());
        btnBack.setOnClickListener(v -> onBackPressed());

        //get doc ref from intent extras.
        animalRef = db.document(getIntent().getStringExtra(Constants.ANIMAL_REF_INTENT_EXTRA));
        btnDelete.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.app_name).setTitle("Are you sure you want to delete this animal").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    repo.deleteAnimal(animalRef.getId(), () -> {
                    }, (err) -> {
                    });
                    finish();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).create();
            dialog.show();
        });

        //Attach listener that updates on changes.
        animalRef.addSnapshotListener(this, (snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                AnimalFireStoreModel animal = snapshot.toObject(AnimalFireStoreModel.class);
                txtAnimalName.setText(animal.getName());
                txtSpottedDate.setText(animal.getDate().toDate().toString());
                String near = Repository.getAnimalRepository(getApplicationContext()).getLocalityFromLatLong(animal.getLatitude(), animal.getLongitude());
                if (near != null) {
                    txtSpottedNear.setText(near);
                } else {
                    txtSpottedNear.setText(animal.getLatitude() + ", " + animal.getLongitude());
                }
                txtWikiNotes.setText(animal.getDescription());
                txtUserNotes.setText(animal.getUserNotes());
                Glide.with(userImageView.getContext()).load(animal.getImageURI()).into(userImageView);

                userImageView.setOnClickListener(v -> {
                    Intent intent = new Intent(this, FullScreenImgActivity.class);
                    intent.putExtra(IMAGE_URL_INTENT_EXTRA,animal.getImageURI()); //pass animal path to full screen activity
                    startActivity(intent);
                });
            } else {
                Log.d(TAG, "Current data: null");
            }
        });


    }

    //save animal when back button is pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        animalRef.update(USER_NOTES_FIELD, txtUserNotes.getText().toString());
        finish();
    }


}