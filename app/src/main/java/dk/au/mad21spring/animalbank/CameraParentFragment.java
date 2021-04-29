package dk.au.mad21spring.animalbank;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static dk.au.mad21spring.animalbank.Constants.CAMERA_PERMISSION_REQUEST_CODE;
import static dk.au.mad21spring.animalbank.Constants.LOCATION_PERMISSION_REQUEST_CODE;

//Inspiration drawn from https://github.com/akhilbattula/android-camerax-java/blob/98593fbd93db214bb5551106f95e4fed348d42d5/app/src/main/java/com/akhil/cameraxjavademo/MainActivity.java#L149
public class CameraParentFragment extends Fragment implements AddAnimalFragment.AddAnimalFragmentListener, CaptureImageFragment.CaptureImageFragmentListener {

    private PreviewView viewFinder;
    private ImageView captureView;
    private ImageCapture imageCapture;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};
    private final Executor executor = Executors.newSingleThreadExecutor();
    private FusedLocationProviderClient fusedLocationClient;
    private Location locationAtCapture;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera_parent, container, false);
        this.captureView = view.findViewById(R.id.captureView);
        this.viewFinder = view.findViewById(R.id.viewFinder);

        //Init camera
        if (hasPermissions()) {
            this.startCamera();
        } else {
            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, CAMERA_PERMISSION_REQUEST_CODE);
        }
        //For retrieving location data:
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //Make sure the UI starts out in the correct state:
        this.goToCaptureImageMode();
        //Init back stack handling.
        this.addReturnFromAddAnimalListener();
        return view;
    }


    /*----------------------------------------------------------------------------------------*/
    /*---------------------------------- STATE HANDLING --------------------------------------*/
    /*----------------------------------------------------------------------------------------*/

    private void goToCaptureImageMode() {
        this.discardCapturedImage();
        this.getChildFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new CaptureImageFragment()).commit();
    }

    private void goToAddAnimalMode() {
        this.showCapturedImage();
        this.getChildFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new AddAnimalFragment()).addToBackStack(AddAnimalFragment.tag).commit();
    }

    private void addReturnFromAddAnimalListener() {
        this.getChildFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                AddAnimalFragment a = (AddAnimalFragment) getChildFragmentManager().findFragmentByTag(AddAnimalFragment.tag);
                if (a == null) {
                    //Make sure camera is active if user returns back from the add animal fragment.
                    goToCaptureImageMode();
                }
            }
        });
    }


    @Override
    public void onDiscardPressed() {
        this.goToCaptureImageMode();
    }



    /*----------------------------------------------------------------------------------------*/
    /*----------------------------------- PERMISSIONS ----------------------------------------*/
    /*----------------------------------------------------------------------------------------*/

    //Checks whether permissions has already been granted by user.
    boolean hasPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
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
                Toast.makeText(getActivity(), "Permissions are necessary.", Toast.LENGTH_SHORT).show();
                //TODO: Needs to handle case when user did not give permissions.
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    /*----------------------------------------------------------------------------------------*/
    /*-------------------------------------- LOCATION ----------------------------------------*/
    /*----------------------------------------------------------------------------------------*/

    public Location getLocationAtCapture() {
        return this.locationAtCapture;
    }

    @SuppressLint("MissingPermission")
    private void getLocation(OnSuccessListener<Location> listener) {
        if (hasPermissions()) {
            this.fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), listener);
        } else {
            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    /*----------------------------------------------------------------------------------------*/
    /*--------------------------------------- CAMERA -----------------------------------------*/
    /*----------------------------------------------------------------------------------------*/
    public Bitmap getCapturedImage() {
        return ((BitmapDrawable) this.captureView.getDrawable()).getBitmap();
    }

    private void showCapturedImage() {
        Handler Handler = new Handler(Looper.getMainLooper());
        Handler.post(()->{Bitmap bitmap = viewFinder.getBitmap();
            captureView.setImageBitmap(bitmap);
            captureView.setVisibility(View.VISIBLE);
            viewFinder.setVisibility(View.GONE);});
    }

    private void discardCapturedImage() {
        this.captureView.setVisibility(View.GONE);
        this.viewFinder.setVisibility(View.VISIBLE);
    }

    void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getActivity());
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
        }, ContextCompat.getMainExecutor(getActivity()));
    }

    void bindImagePreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview imagePreview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().build();
        this.imageCapture = new ImageCapture.Builder()
                .setTargetRotation(getActivity().getWindowManager()
                        .getDefaultDisplay().
                                getRotation()).build();
        imagePreview.setSurfaceProvider(this.viewFinder.createSurfaceProvider());
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imagePreview, imageCapture);
    }

    @Override
    public void onCaptureImagePressed() {
        imageCapture.takePicture(this.executor, new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                //Should probably use the captured image. Currently just using the bitmap from the preview.
                getLocation(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        //TODO: Handle if a null location is received.
                        locationAtCapture = location;
                        Log.e("locationatcapture", location.toString());
                    }
                });
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