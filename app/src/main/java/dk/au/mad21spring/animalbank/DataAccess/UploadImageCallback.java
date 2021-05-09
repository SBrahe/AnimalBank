package dk.au.mad21spring.animalbank.DataAccess;


import android.net.Uri;

interface UploadImageCallback {
    void onComplete(Uri result);
}