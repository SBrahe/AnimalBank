package dk.au.mad21spring.animalbank;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }

/*    //go to list when back button is pressed
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(ListActivity.this, CameraActivity.class));
        finish();
    }*/
}