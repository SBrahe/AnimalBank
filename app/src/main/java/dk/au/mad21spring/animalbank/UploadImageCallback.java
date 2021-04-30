package dk.au.mad21spring.animalbank;


import android.net.Uri;

interface UploadImageCallback {
    void onComplete(Uri result);
}