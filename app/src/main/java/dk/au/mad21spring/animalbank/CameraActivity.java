package dk.au.mad21spring.animalbank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;

import android.hardware.Camera;
import android.os.Bundle;

import static dk.au.mad21spring.animalbank.Constants.CAMERA_ID;

//Inspiration taken from https://developer.android.com/training/camera/cameradirect



public class CameraActivity extends AppCompatActivity {

    PreviewView previewView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
    }

}