package by.petrovlad.test.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import by.petrovlad.test.Constants;
import by.petrovlad.test.PhotosAdapter;
import by.petrovlad.test.R;
import by.petrovlad.test.Upload;

public class ShowGalleryActivity extends AppCompatActivity {

    private GridView gvPhotos;
    private List<Upload> uploads;
    private PhotosAdapter adapter;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_gallery);

        init();
    }

    private void init() {
        String uid = getIntent().getExtras().getString(Constants.UID_EXTRA);
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_IMAGES_REFERENCE + "/" + uid);

        uploads = new ArrayList<>();
        loadPhotos();

        gvPhotos = findViewById(R.id.gvPhotos);
        adapter = new PhotosAdapter(getApplicationContext(), uploads);
        gvPhotos.setAdapter(adapter);

        gvPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ShowGalleryActivity.this, ShowPhotoFromGalleryActivity.class);
                intent.putExtra(Constants.IMAGE_NAME_EXTRA, uploads.get(position).getName());
                intent.putExtra(Constants.IMAGE_URL_EXTRA, uploads.get(position).getImageUrl());

                startActivity(intent);
            }
        });
    }

    private void loadPhotos() {
        // слушается только ветка с пользователем
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uploads.clear();

                for (DataSnapshot subSnapshot : snapshot.getChildren()) {
                    Upload upload = subSnapshot.getValue(Upload.class);
                    if (upload == null) {
                        Log.w("ShwKittenActivity.init:", "'upload' was null");
                        continue;
                    }

                    uploads.add(upload);
                }
                if (uploads.isEmpty()) {
                    TextView tvUserHaveNoPhotos = ShowGalleryActivity.this.findViewById(R.id.tvUserHaveNoPhotos);
                    tvUserHaveNoPhotos.setTextSize(18);
                    tvUserHaveNoPhotos.setText(R.string.user_have_no_photos_hint);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ShwKittenActivity.init:", error.getMessage());
                Toast.makeText(ShowGalleryActivity.this, R.string.toast_unable_load_data, Toast.LENGTH_LONG).show();
            }
        });
    }


}