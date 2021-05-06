package dk.au.mad21spring.animalbank;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;

import static dk.au.mad21spring.animalbank.Constants.ANIMAL_REF_INTENT_EXTRA;

public class MapsFragment extends Fragment {

    private Repository repo;

    // Inspired by https://stackoverflow.com/questions/38626685/google-maps-marker-how-saved-data
    private HashMap<Marker, DocumentReference> markerReferences;

    //Save ref in order to refresh manually.
    private GoogleMap map;

    private OnMapReadyCallback onMapReady = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            populateMap(map);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        repo = Repository.getAnimalRepository(getActivity().getApplicationContext());
        markerReferences = new HashMap<Marker,DocumentReference>();

        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(onMapReady);
        }
    }

    @Override
    public void onResume() {
        if(map!=null){
            map.clear();
            populateMap(map);
        }
        super.onResume();
    }

    private void populateMap(GoogleMap googleMap){
        repo.getAllAnimalsAsync((animalFireStoreModel) -> {
            addAnimalToMap(animalFireStoreModel, map);
        });
        map.setOnInfoWindowClickListener((marker) -> {
            DocumentReference docRef = markerReferences.get(marker);
            Intent intent = new Intent(getActivity(), InfoActivity.class);
            intent.putExtra(ANIMAL_REF_INTENT_EXTRA, docRef.getPath()); //pass animal path to info activity
            startActivity(intent);

        });
    }

    private void addAnimalToMap(AnimalFireStoreModel animal, GoogleMap googleMap) {
        LatLng newAnimal = new LatLng(animal.getLatitude(), animal.getLongitude());
        Marker marker =  googleMap.addMarker(new MarkerOptions().position(newAnimal).title(animal.getName()));
        //Add marker/docref lookup which can be used to navigate to infoview when clicking markers.
        markerReferences.put(marker, animal.documentReference);
    }
}