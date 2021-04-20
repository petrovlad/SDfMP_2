package by.petrovlad.test.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import by.petrovlad.test.Constants;
import by.petrovlad.test.FontSettings;
import by.petrovlad.test.KittenLocation;
import by.petrovlad.test.R;

public class SetLocationActivity extends AppCompatActivity implements OnMapReadyCallback{
    private Marker marker;
    private Button btnSetLocation;
    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);

        typeface = ResourcesCompat.getFont(this, FontSettings.fontId);

        btnSetLocation = findViewById(R.id.btnSetLocation);
        btnSetLocation.setEnabled(false);
        btnSetLocation.setTextSize(FontSettings.fontSize);
        btnSetLocation.setTypeface(typeface);

        SupportMapFragment mapSetLocation = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapSetLocation);
        mapSetLocation.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (marker != null) {
                    marker.remove();
                }
                marker = googleMap.addMarker(new MarkerOptions().position(latLng));
                btnSetLocation.setEnabled(true);

                btnSetLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.KITTEN_LOCATIONS_REFERENCE).child(uid);

                        KittenLocation kittenLocation = new KittenLocation(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));
                        ref.setValue(kittenLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SetLocationActivity.this, R.string.toast_changes_saved, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}