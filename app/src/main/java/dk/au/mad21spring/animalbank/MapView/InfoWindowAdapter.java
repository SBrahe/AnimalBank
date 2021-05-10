package dk.au.mad21spring.animalbank.MapView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import dk.au.mad21spring.animalbank.R;

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    Context context;
    TextView animalNameText;
    TextView spottedDateText;

    public InfoWindowAdapter(Context context){
        this.context = context;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_info_window,null);
        animalNameText = view.findViewById(R.id.animalNameText);
        spottedDateText = view.findViewById(R.id.spottedDateText);
        animalNameText.setText(marker.getTitle());
        spottedDateText.setText(marker.getSnippet());
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
