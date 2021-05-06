package dk.au.mad21spring.animalbank;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;

public class FullScreenImgActivity extends AppCompatActivity {

    private ImageView fullScreenImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_img);
        fullScreenImageView = findViewById(R.id.fullScreenImageView);

        String Url = getIntent().getStringExtra(Constants.IMAGE_URL_INTENT_EXTRA);
        Glide.with(fullScreenImageView.getContext()).load(Url).into(fullScreenImageView);

    }
}