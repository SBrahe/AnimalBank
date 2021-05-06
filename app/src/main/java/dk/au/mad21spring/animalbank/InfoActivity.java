package dk.au.mad21spring.animalbank;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import dk.au.mad21spring.animalbank.Constants;
import dk.au.mad21spring.animalbank.viewmodels.SingleAnimalViewModel;
import dk.au.mad21spring.animalbank.viewmodels.SingleAnimalViewModelFactory;

import static dk.au.mad21spring.animalbank.Constants.ANIMAL_REF_INTENT_EXTRA;
import static dk.au.mad21spring.animalbank.Constants.IMAGE_URL_INTENT_EXTRA;

public class InfoActivity extends AppCompatActivity {
    private static final String TAG = "InfoActivity";
    private Button btnBack;
    private Button btnDelete;
    private TextView txtAnimalName;
    private TextView txtSpottedDate;
    private TextView txtSpottedNear;
    private TextView txtUserNotes;
    private TextView txtWikiNotes;
    private ImageView userImageView;
    private SingleAnimalViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        btnBack = findViewById(R.id.backBtn);
        btnDelete = findViewById(R.id.deleteBtn);
        txtAnimalName = findViewById(R.id.animalNameText);
        txtSpottedDate = findViewById(R.id.spottedDateText);
        txtSpottedNear = findViewById(R.id.spottedLocationText);
        txtUserNotes = findViewById(R.id.userNotesText);
        txtWikiNotes = findViewById(R.id.wikiNotesText);
        userImageView = findViewById(R.id.userImageView);

        SingleAnimalViewModelFactory vmFactory = new SingleAnimalViewModelFactory(getApplication(),getIntent().getStringExtra(Constants.ANIMAL_REF_INTENT_EXTRA) );
        viewModel = new ViewModelProvider(this, vmFactory).get(SingleAnimalViewModel.class);
        viewModel.getAnimal().observe(this, this::refreshUI);
        btnBack.setOnClickListener(v -> onBackPressed());
        btnDelete.setOnClickListener(this::onDeletePressed);
        userImageView.setOnClickListener(this::onImageClicked);
    }

    private void onImageClicked(View view){
        Intent intent = new Intent(this, FullScreenImgActivity.class);
        intent.putExtra(IMAGE_URL_INTENT_EXTRA,viewModel.getAnimal().getValue().getImageURI()); //pass animal path to full screen activity
        startActivity(intent);
    }

    private void onDeletePressed(View view){
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.app_name).setTitle("Are you sure you want to delete this animal").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewModel.deleteAnimal(()->{},(ex)->{});
                finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).create();
        dialog.show();
    }

    private void refreshUI(AnimalFireStoreModel animal){
        txtAnimalName.setText(animal.getName());
        txtSpottedDate.setText(animal.getDate().toDate().toString());
        String near = Repository.getAnimalRepository(getApplicationContext()).getLocalityFromLatLong(animal.getLatitude(), animal.getLongitude());
        if (near != null) {
            txtSpottedNear.setText(near);
        } else {
            txtSpottedNear.setText(animal.getLatitude() + ", " + animal.getLongitude());
        }
        txtWikiNotes.setText(animal.getDescription());
        Glide.with(userImageView.getContext()).load(animal.getImageURI()).into(userImageView);
    }

}