package dk.au.mad21spring.animalbank.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.atomic.AtomicBoolean;

import dk.au.mad21spring.animalbank.DataAccess.AnimalFireStoreModel;
import dk.au.mad21spring.animalbank.DataAccess.Repository;
import dk.au.mad21spring.animalbank.R;

import static dk.au.mad21spring.animalbank.Constants.LOCATION_PERMISSION_REQUEST_CODE;


public class AnimalUpdateWorker extends Worker {
    private NotificationManager notificationManager;
    private NotificationChannel notificationChannel;
    private static final String TAG = "AnimalUpdateWorker";
    public static final int NOTIFICATION_ID = 101;
    public static final String ANIMAL_UPDATE_SERVICE_CHANNEL = "AnimalUpdateServiceChannel";
    public AnimalUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //Gets current location and notifies user with the closest observation, if it is in the same district.
        getLocation(this::fireNotification);
        return Result.success();
    }


    private double getDistFromCurrentLocation(AnimalFireStoreModel contestant, double currentLat, double currentLong) {
        double latDiff = currentLat - contestant.getLatitude();
        double longDiff = currentLong - contestant.getLongitude();
        return Math.sqrt(Math.pow(latDiff, 2) + Math.pow(longDiff, 2));
    }

    @SuppressLint("MissingPermission")
    private void getLocation(OnSuccessListener<Location> listener) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        fusedLocationClient.getLastLocation().addOnSuccessListener(listener);
    }

    private void fireNotification(Location currentLocation){
        Repository repo = Repository.getAnimalRepository(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.notificationChannel = new NotificationChannel(ANIMAL_UPDATE_SERVICE_CHANNEL, TAG, NotificationManager.IMPORTANCE_DEFAULT);
            this.notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            this.notificationManager.createNotificationChannel(this.notificationChannel);
        }

        final AnimalFireStoreModel[] closestObservation = {new AnimalFireStoreModel()};
        final double[] currentlyShortestDist = {Double.POSITIVE_INFINITY};
        repo.getAllAnimalsAsync((animalFireStoreModel) -> {
            double distFromCurrentLoc = getDistFromCurrentLocation(animalFireStoreModel, currentLocation.getLatitude(), currentLocation.getLongitude());
            if (distFromCurrentLoc < currentlyShortestDist[0]) {
                currentlyShortestDist[0] = distFromCurrentLoc;
                closestObservation[0] = animalFireStoreModel;
            }
        }, () -> {
            String currentLocality =  repo.getLocalityFromLatLong(currentLocation.getLatitude(),currentLocation.getLongitude());
            String closestLocality = repo.getLocalityFromLatLong(closestObservation[0].getLatitude(),closestObservation[0].getLongitude());

            if(currentLocality.equals(closestLocality)){
                Notification notification = new NotificationCompat.Builder(getApplicationContext(), ANIMAL_UPDATE_SERVICE_CHANNEL)
                        .setContentTitle("Previous observation")
                        .setSmallIcon(R.mipmap.transparent)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(closestObservation[0].getName() + " was observed close to your current location on "+closestObservation[0].getDateShortString() +"\n\n" + closestObservation[0].getDescription()))
                        .build();
                this.notificationManager.notify(NOTIFICATION_ID, notification);
            }
        });
    }
}
