package dk.au.mad21spring.animalbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import dk.au.mad21spring.animalbank.CameraView.CameraParentFragment;
import dk.au.mad21spring.animalbank.ListView.ListFragment;
import dk.au.mad21spring.animalbank.MapView.MapsFragment;

public class NavBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottom_navbar = findViewById(R.id.bottom_navbar);
        bottom_navbar.setOnNavigationItemSelectedListener(navbarListenser);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new CameraParentFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navbarListenser =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()){
                        case R.id.nav_camera:
                            selectedFragment = new CameraParentFragment();
                            break;
                        case R.id.nav_location:
                            selectedFragment = new MapsFragment();
                            break;
                        case R.id.nav_list:
                            selectedFragment = new ListFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };
}