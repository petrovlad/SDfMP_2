package by.petrovlad.test.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.geometry.Point;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import by.petrovlad.test.Constants;
import by.petrovlad.test.Kitten;
import by.petrovlad.test.KittenLocation;
import by.petrovlad.test.R;

public class MapFragment extends Fragment {

    private MapViewModel mapViewModel;
    private FirebaseDatabase firebaseDatabase;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);
/*        final TextView textView = root.findViewById(R.id.text_map);
        mapViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                setDBListeners(googleMap);
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        return root;
    }

    private void setDBListeners(GoogleMap googleMap) {
        DatabaseReference kittensReference = firebaseDatabase.getReference(Constants.KITTENS_REFERENCE);
        kittensReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                googleMap.clear();

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String kittenName = childSnapshot.getValue(Kitten.class).name;
                    DatabaseReference kittenLocationRef = firebaseDatabase.getReference(Constants.KITTEN_LOCATIONS_REFERENCE + "/" + childSnapshot.getKey());

                    kittenLocationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Point kittenLocationPoint = null;
                            if (snapshot.exists()) {
                                KittenLocation kittenLocation = snapshot.getValue(KittenLocation.class);
                                kittenLocationPoint = new Point(Double.parseDouble(kittenLocation.xCoordinate), Double.parseDouble(kittenLocation.yCoordinate));
                                googleMap.addMarker(new MarkerOptions().position(new LatLng(kittenLocationPoint.x, kittenLocationPoint.y)).title(kittenName));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.w("MapFragment.getLoctns:", error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("MapFragment.getKittens:", error.getMessage());
            }
        });

    }
}
