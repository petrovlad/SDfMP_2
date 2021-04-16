package by.petrovlad.test.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;

import by.petrovlad.test.Constants;
import by.petrovlad.test.R;
import by.petrovlad.test.Upload;

public class ShowKittenActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private Button btnShowGallery;
    private VideoView videoView;

    private MediaController mediaController;

    private DatabaseReference databaseImageReference;
    private DatabaseReference databaseVideoReference;


    private final TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

    private ImageView ivTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_kitten);

        init();
    }

    private void init() {
        btnShowGallery = findViewById(R.id.btnShowGallery);
        btnShowGallery.setOnClickListener(this::onClickShowGallery);

        tableLayout = findViewById(R.id.tlShowKitten);

        ivTemp = findViewById(R.id.ivTemp);

        // get values from previous activity
        Intent intent = getIntent();
        ArrayList<String> headers = intent.getStringArrayListExtra(Constants.KITTEN_HEADERS);

        for (String header : headers) {
            tableLayout.addView(createRow(header, intent.getExtras().getString(header)));
        }

        String uid = intent.getExtras().getString(Constants.UID_EXTRA);
        // reference = "images/<user_uid>"
        databaseImageReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_IMAGES_REFERENCE + "/" + uid);
        databaseVideoReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_VIDEOS_REFERENCE + "/" + uid);
        loadAvatar();

        // load video
        loadVideo();

    }

    private void loadVideo() {
        databaseVideoReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> snapshotIterator = dataSnapshot.getChildren().iterator();
                if (snapshotIterator.hasNext()) {
                    DataSnapshot avatarSnapshot = snapshotIterator.next();
                    Upload upload = avatarSnapshot.getValue(Upload.class);
                    if (upload == null) {
                        Log.w("ShwKittenActivity.init:", "'upload' was null");
                        return;
                    }
                    String videoUrl = upload.getImageUrl();

                    videoView = findViewById(R.id.videoView);
                    mediaController = new MediaController(ShowKittenActivity.this);
                    videoView.setMediaController(mediaController);
                    videoView.setVideoURI(Uri.parse(videoUrl));
                    videoView.start();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("ShwKittenActivity.init:", e.getMessage());
                Toast.makeText(ShowKittenActivity.this, R.string.toast_unable_load_data, Toast.LENGTH_LONG).show();
            }
        });
    }

    private TableRow createRow(String key, String value) {

        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(rowParams);

        TextView keyText = new TextView(this);
        keyText.setText(key);
        keyText.setTextSize(18);
        keyText.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.4f));
        tableRow.addView(keyText);

        TextView valueText = new TextView(this);
        valueText.setText(value);
        //valueText.setTextSize(16);
        valueText.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.6f));
        valueText.setGravity(Gravity.CENTER_VERTICAL);
        tableRow.addView(valueText);

        return tableRow;
    }

    private void loadAvatar() {
        // слушается только ветка с пользователем
        databaseImageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterator<DataSnapshot> snapshotIterator = snapshot.getChildren().iterator();
                if (snapshotIterator.hasNext()) {
                    DataSnapshot avatarSnapshot = snapshotIterator.next();
                    Upload upload = avatarSnapshot.getValue(Upload.class);
                    if (upload == null) {
                        Log.w("ShwKittenActivity.init:", "'upload' was null");
                        return;
                    }

                    Picasso.get().load(upload.getImageUrl())
                                .placeholder(R.drawable.default_avatar)
                                .resizeDimen(R.dimen.avatar_width, R.dimen.avatar_height)
                                //.fit().centerCrop()
                                .into(ivTemp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ShwKittenActivity.init:", error.getMessage());
                Toast.makeText(ShowKittenActivity.this, R.string.toast_unable_load_data, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onClickShowGallery(View view) {
        Intent intent = new Intent(ShowKittenActivity.this, ShowGalleryActivity.class);
        intent.putExtra(Constants.UID_EXTRA, getIntent().getExtras().getString(Constants.UID_EXTRA));
        startActivity(intent);
    }

}