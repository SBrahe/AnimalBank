package dk.au.mad21spring.animalbank;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Camera;
import android.os.Bundle;

import static dk.au.mad21spring.animalbank.Constants.CAMERA_ID;

//Inspiration taken from https://developer.android.com/training/camera/cameradirect



public class CameraActivity extends AppCompatActivity {

    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
    }

    boolean tryOpenCamera(){
        boolean launchSuccess = false;
        try {
            this.camera = Camera.open(CAMERA_ID);
            launchSuccess = (this.camera != null);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return launchSuccess;
    }

    void releaseCamera(){

    }
}