package dk.au.mad21spring.animalbank;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class CaptureImageFragment extends Fragment {

    public interface CaptureImageFragmentListener {
        public void onCaptureImagePressed();
    }
    private CaptureImageFragmentListener listener;
    Button captureBtn;

    @Override
    public void onAttach(@NonNull Context context) {
        //Activity listens for events.
        this.listener = (CaptureImageFragmentListener)context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_capture_image, container, false);
        captureBtn = view.findViewById(R.id.captureBtn);
        captureBtn.setOnClickListener(v -> listener.onCaptureImagePressed());
        return view;
    }
}