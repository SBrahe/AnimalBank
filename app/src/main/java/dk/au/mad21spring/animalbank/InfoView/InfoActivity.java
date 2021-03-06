package dk.au.mad21spring.animalbank.InfoView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import dk.au.mad21spring.animalbank.Constants;
import dk.au.mad21spring.animalbank.DataAccess.AnimalFireStoreModel;
import dk.au.mad21spring.animalbank.DataAccess.Repository;
import dk.au.mad21spring.animalbank.R;
import dk.au.mad21spring.animalbank.viewmodels.SingleAnimalViewModel;
import dk.au.mad21spring.animalbank.viewmodels.SingleAnimalViewModelFactory;

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

        this.btnBack        = findViewById(R.id.backBtn);
        this.btnDelete      = findViewById(R.id.deleteBtn);
        this.txtAnimalName  = findViewById(R.id.animalNameText);
        this.txtSpottedDate = findViewById(R.id.spottedDateText);
        this.txtSpottedNear = findViewById(R.id.spottedLocationText);
        this.txtUserNotes   = findViewById(R.id.userNotesText);
        this.txtWikiNotes   = findViewById(R.id.wikiNotesText);
        this.userImageView  = findViewById(R.id.userImageView);

        SingleAnimalViewModelFactory vmFactory = new SingleAnimalViewModelFactory(getApplication(),getIntent().getStringExtra(Constants.ANIMAL_REF_INTENT_EXTRA) );
        this.viewModel = new ViewModelProvider(this, vmFactory).get(SingleAnimalViewModel.class);
        this.viewModel.getAnimal().observe(this, this::refreshUI);
        this.btnBack.setOnClickListener(v -> onBackPressed());
        this.btnDelete.setOnClickListener(this::onDeletePressed);
        this.userImageView.setOnClickListener(this::onImageClicked);
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
        txtSpottedDate.setText(animal.getDateShortString());
        String near = Repository.getAnimalRepository(getApplicationContext()).getLocalityFromLatLong(animal.getLatitude(), animal.getLongitude());
        if (near != null) {
            txtSpottedNear.setText(near);
        } else {
            txtSpottedNear.setText(animal.getLatitude() + ", " + animal.getLongitude());
        }
        txtWikiNotes.setText(animal.getDescription());
        txtUserNotes.setText(animal.getUserNotes());
        Glide.with(userImageView.getContext()).load(animal.getImageURI()).into(userImageView);
    }
    //save animal when back button is pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimalFireStoreModel animal = viewModel.getAnimal().getValue();
        animal.setUserNotes(txtUserNotes.getText().toString());
        viewModel.setAnimal(animal);
        finish();
    }


}