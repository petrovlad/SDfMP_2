package by.petrovlad.test.ui.map;

import android.Manifest;
import android.content.Intent;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.geometry.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import by.petrovlad.test.Constants;
import by.petrovlad.test.Kitten;
import by.petrovlad.test.KittenLocation;
import by.petrovlad.test.R;
import by.petrovlad.test.ui.activity.ShowKittenActivity;

public class MapFragment extends Fragment {

    private MapViewModel mapViewModel;
    private FirebaseDatabase firebaseDatabase;

    private HashMap<Marker, Kitten> markerKittens;
    private HashMap<Marker, String> markerUids;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                setDBListeners(googleMap);
            }
        });

        markerKittens = new HashMap<>();
        markerUids = new HashMap<>();

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
                    Kitten kitten = childSnapshot.getValue(Kitten.class);
                    String kittenName = kitten.name;
                    DatabaseReference kittenLocationRef = firebaseDatabase.getReference(Constants.KITTEN_LOCATIONS_REFERENCE + "/" + childSnapshot.getKey());

                    kittenLocationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Point kittenLocationPoint = null;
                            if (snapshot.exists()) {
                                KittenLocation kittenLocation = snapshot.getValue(KittenLocation.class);
                                kittenLocationPoint = new Point(Double.parseDouble(kittenLocation.xCoordinate), Double.parseDouble(kittenLocation.yCoordinate));
                                Marker mark = googleMap.addMarker(new MarkerOptions().position(new LatLng(kittenLocationPoint.x, kittenLocationPoint.y)).title(kittenName));
                                markerKittens.put(mark, kitten);
                                markerUids.put(mark, snapshot.getKey());

                                mark.setTag(0);
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

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                for (Map.Entry entry : markerUids.entrySet()) {
                    Marker temp = (Marker)entry.getKey();
                    Integer tag = (Integer) temp.getTag();
                    String name = markerKittens.get(entry.getKey()).name;
                    if (!marker.equals(temp)) {
                        temp.setTag(Constants.MARKER_UNCLICKED_TAG);
                    }
                }

                Integer clickedTag = (Integer) marker.getTag();
                if (clickedTag == null) {
                    return false;
                }

                if (clickedTag.equals(Constants.MARKER_UNCLICKED_TAG)) {
                    marker.showInfoWindow();
                    marker.setTag(Constants.MARKER_CLICKED_TAG);

                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 250, null);
                    return true;
                }

                Kitten kitten = markerKittens.get(marker);
                if (kitten != null) {
                    Intent intent = new Intent(getActivity(), ShowKittenActivity.class);
                    // put all headers to intent
                    ArrayList<String> headers = new ArrayList<>();

                    String nameHeader = getString(R.string.kitten_name);
                    String emailHeader = getString(R.string.kitten_email);
                    String eyesHeader = getString(R.string.kitten_eyes_color);
                    String tailHeader = getString(R.string.kitten_tail_length);
                    String heightHeader = getString(R.string.kitten_height);

                    headers.add(nameHeader);
                    headers.add(emailHeader);
                    headers.add(eyesHeader);
                    headers.add(tailHeader);
                    headers.add(heightHeader);

                    intent.putStringArrayListExtra(Constants.KITTEN_HEADERS, headers);

                    intent.putExtra(nameHeader, kitten.name);
                    intent.putExtra(emailHeader, kitten.email);
                    intent.putExtra(eyesHeader, kitten.eyesColor);
                    intent.putExtra(tailHeader, kitten.tailLength);
                    intent.putExtra(heightHeader, kitten.height);

                    intent.putExtra(Constants.UID_EXTRA, markerUids.get(marker));

                    startActivity(intent);
                }
                marker.setTag(Constants.MARKER_UNCLICKED_TAG);
                return true;

            }
        });

    }
}
