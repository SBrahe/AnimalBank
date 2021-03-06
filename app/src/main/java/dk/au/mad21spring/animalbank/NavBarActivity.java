package dk.au.mad21spring.animalbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.TimeUnit;

import dk.au.mad21spring.animalbank.CameraView.CameraParentFragment;
import dk.au.mad21spring.animalbank.ListView.ListFragment;
import dk.au.mad21spring.animalbank.MapView.MapsFragment;
import dk.au.mad21spring.animalbank.services.AnimalUpdateWorker;

import static dk.au.mad21spring.animalbank.Constants.STARTUP_INTENT_EXTRA;

public class NavBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottom_navbar = findViewById(R.id.bottom_navbar);
        bottom_navbar.setOnNavigationItemSelectedListener(navbarListener);
        //The following makes sure that the app does not revert to the camera view when device is rotated.
        if (getIntent().getBooleanExtra(STARTUP_INTENT_EXTRA,false) == true)
        {
            getIntent().removeExtra(STARTUP_INTENT_EXTRA);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new CameraParentFragment()).commit();
        }

        PeriodicWorkRequest.Builder dayWorkBuilder =
                new PeriodicWorkRequest.Builder(AnimalUpdateWorker.class, 15, TimeUnit.MINUTES, 5,
                        TimeUnit.MINUTES);
        WorkManager.getInstance(this).enqueue(dayWorkBuilder.build());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navbarListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()){
                        case R.id.nav_camera:
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            selectedFragment = new CameraParentFragment();
                            break;
                        case R.id.nav_location:
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                            selectedFragment = new MapsFragment();
                            break;
                        case R.id.nav_list:
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                            selectedFragment = new ListFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };
}