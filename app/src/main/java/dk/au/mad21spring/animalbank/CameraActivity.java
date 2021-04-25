package dk.au.mad21spring.animalbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static dk.au.mad21spring.animalbank.Constants.CAMERA_PERMISSION_REQUEST_CODE;

//Inspiration drawn from https://github.com/akhilbattula/android-camerax-java/blob/98593fbd93db214bb5551106f95e4fed348d42d5/app/src/main/java/com/akhil/cameraxjavademo/MainActivity.java#L149
public class CameraActivity extends AppCompatActivity {


    PreviewView previewView;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};
    private Button addBtn;
    private Button captureBtn;
    private Executor executor = Executors.newSingleThreadExecutor();
    private ImageView captureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        this.addBtn = findViewById(R.id.addBtn);
        this.addBtn.setOnClickListener(v -> onAddPressed());
        this.captureBtn = findViewById(R.id.captureBtn);
        this.captureView = findViewById(R.id.captureView);




        this.previewView = findViewById(R.id.previewView);
        //Init camera
        if (hasPermissions()) {
            this.startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private void onCaptureBtnPressed() {
        this.showCapturedImage();
    }

    private void showCapturedImage() {
        this.captureView.setImageBitmap(this.previewView.getBitmap());
    }

    private void swapCaptureAndPreviewVisibility(){

    }


    void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            ProcessCameraProvider cameraProvider = null;
            try {
                cameraProvider = cameraProviderFuture.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.bindImagePreview(cameraProvider);
        }, ContextCompat.getMainExecutor(this));
    }

    void bindImagePreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview imagePreview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().build();
        ImageCapture imageCapture = new ImageCapture.Builder()
                .setTargetRotation(this.getWindowManager()
                        .getDefaultDisplay().
                                getRotation()).build();
        imagePreview.setSurfaceProvider(this.previewView.createSurfaceProvider());
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this,cameraSelector,imagePreview,imageCapture);
        //Camera variable not used?
        this.captureBtn.setOnClickListener(v->{
            imageCapture.takePicture(this.executor, new ImageCapture.OnImageCapturedCallback() {
                @Override
                public void onCaptureSuccess(@NonNull ImageProxy image) {
                    showCapturedImage();
                    super.onCaptureSuccess(image);
                }
                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    super.onError(exception);
                }
            });
        });
    }

    //Checks whether permissions has already been granted by user.
    boolean hasPermissions() {
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    //Will be called when user has been asked for permissions.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==CAMERA_PERMISSION_REQUEST_CODE){
            if(hasPermissions()){
                this.startCamera();
        }
            else{

            }
            Toast.makeText(this, "Permissions are necessary.", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onAddPressed() {
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}