package dk.au.mad21spring.animalbank.MapView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;

import dk.au.mad21spring.animalbank.DataAccess.AnimalFireStoreModel;
import dk.au.mad21spring.animalbank.DataAccess.Repository;
import dk.au.mad21spring.animalbank.InfoView.InfoActivity;
import dk.au.mad21spring.animalbank.R;

import static dk.au.mad21spring.animalbank.Constants.ANIMAL_REF_INTENT_EXTRA;
import static dk.au.mad21spring.animalbank.Constants.LOCATION_PERMISSION_REQUEST_CODE;

public class MapsFragment extends Fragment {

    private Repository repo;
    private FusedLocationProviderClient fusedLocationClient;
    // Inspired by https://stackoverflow.com/questions/38626685/google-maps-marker-how-saved-data
    private HashMap<Marker, DocumentReference> markerReferences;
    //Save ref in order to refresh manually.
    private GoogleMap map;
    private OnMapReadyCallback onMapReady = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            populateMap();
            moveToCurrentLocation();
        }
    };

    void moveToCurrentLocation(){
        getLocation(location -> {
            moveCamera(location);
        });
    }

    void moveCamera(Location location){
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        map.moveCamera(update);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        repo = Repository.getAnimalRepository(getActivity().getApplicationContext());
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
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
            populateMap();
        }
        super.onResume();
    }

    private void populateMap(){
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
        Marker marker =  googleMap.addMarker(new MarkerOptions().position(newAnimal).title(animal.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        //Add marker/docref lookup which can be used to navigate to infoview when clicking markers.
        markerReferences.put(marker, animal.documentReference);
    }




    boolean hasLocationPermission() {
        boolean FINE_LOCATION_PERMISSION = (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        boolean COARSE_LOCATION_PERMISSION = (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        if (FINE_LOCATION_PERMISSION && COARSE_LOCATION_PERMISSION) {
            return true;
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    private void getLocation(OnSuccessListener<Location> listener) {
        if (hasLocationPermission()) {
            this.fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), listener);
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }



    //Will be called when user has been asked for permissions.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && hasLocationPermission()) {
            getLocation(location -> {
                moveCamera(location);
            });
        }
    }

}