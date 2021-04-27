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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
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
import static dk.au.mad21spring.animalbank.Constants.IMAGE_EXTRA_NAME;

//Inspiration drawn from https://github.com/akhilbattula/android-camerax-java/blob/98593fbd93db214bb5551106f95e4fed348d42d5/app/src/main/java/com/akhil/cameraxjavademo/MainActivity.java#L149
public class CameraActivity extends AppCompatActivity implements AddAnimalFragment.AddAnimalFragmentListener, CaptureImageFragment.CaptureImageFragmentListener {

    private PreviewView viewFinder;
    private ImageView captureView;
    ImageCapture imageCapture;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};
    private Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        this.captureView = findViewById(R.id.captureView);
        this.viewFinder = findViewById(R.id.viewFinder);
        //Init camera
        if (hasPermissions()) {
            this.startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, CAMERA_PERMISSION_REQUEST_CODE);
        }
        this.goToCaptureImageMode();
    }

    private void goToCaptureImageMode() {
        this.discardCapturedImage();
        this.loadFragment(new CaptureImageFragment());
    }

    private void goToAddAnimalMode() {
        this.showCapturedImage();
        this.loadFragment(new AddAnimalFragment());
    }

    private void showCapturedImage() {
        Handler handler = new Handler(getApplicationContext().getMainLooper());
        handler.post(() -> {
            this.captureView.setImageBitmap(this.viewFinder.getBitmap());
            captureView.setVisibility(View.VISIBLE);
            viewFinder.setVisibility(View.GONE);
        });
    }

    private void discardCapturedImage() {
        this.captureView.setVisibility(View.GONE);
        this.viewFinder.setVisibility(View.VISIBLE);
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
        this.imageCapture = new ImageCapture.Builder()
                .setTargetRotation(this.getWindowManager()
                        .getDefaultDisplay().
                                getRotation()).build();
        imagePreview.setSurfaceProvider(this.viewFinder.createSurfaceProvider());
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imagePreview, imageCapture);
    }

    //Checks whether permissions has already been granted by user.
    boolean hasPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    //Will be called when user has been asked for permissions.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (hasPermissions()) {
                this.startCamera();
            } else {

            }
            Toast.makeText(this, "Permissions are necessary.", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, fragment).commit();
    }

    @Override
    public void onDiscardPressed() {
        this.goToCaptureImageMode();
    }

    @Override
    public void onCaptureImagePressed() {
        imageCapture.takePicture(this.executor, new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                //Should probably use the captured image. Currently just using the bitmap from the preview.
                goToAddAnimalMode();
                super.onCaptureSuccess(image);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                super.onError(exception);
            }
        });
    }
}