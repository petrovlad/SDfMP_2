package by.petrovlad.test.ui.activity;

import android.content.Intent;
import android.graphics.Typeface;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;
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
import by.petrovlad.test.FontSettings;
import by.petrovlad.test.R;
import by.petrovlad.test.Upload;

public class ShowKittenActivity extends AppCompatActivity {

    private ExoPlayer exoPlayer;

    private TableLayout tableLayout;
    private Button btnShowGallery;
    private PlayerView playerView;

    private MediaController mediaController;

    private DatabaseReference databaseImageReference;
    private DatabaseReference databaseVideoReference;

    private Typeface typeface;

    private final TableRow.LayoutParams tableParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
    private TableRow.LayoutParams rowParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.4f);

    private ImageView ivTemp;

    private String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_kitten);

        init();
    }

    private void init() {
        typeface = ResourcesCompat.getFont(this, FontSettings.fontId);

        rowParams.setMargins(10, 10, 10, 10);

        playerView = ShowKittenActivity.this.findViewById(R.id.pvVideo);

        btnShowGallery = findViewById(R.id.btnShowGallery);
        btnShowGallery.setOnClickListener(this::onClickShowGallery);
        btnShowGallery.setTextSize(FontSettings.fontSize);
        btnShowGallery.setTypeface(typeface);

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
    }

    private void loadVideo() {
        databaseVideoReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> snapshotIterator = dataSnapshot.getChildren().iterator();
                if (snapshotIterator.hasNext()) {
                    playerView.setVisibility(View.VISIBLE);

                    DataSnapshot avatarSnapshot = snapshotIterator.next();
                    Upload upload = avatarSnapshot.getValue(Upload.class);
                    if (upload == null) {
                        Log.w("ShwKittenActivity.init:", "'upload' was null");
                        return;
                    }
                    videoUrl = upload.getImageUrl();
                    initializePlayer();
                } else {
                    playerView.setLayoutParams(new ConstraintLayout.LayoutParams(0, 0));
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

    private void initializePlayer() {
        exoPlayer = new SimpleExoPlayer.Builder(getApplicationContext()).build();
        playerView.setPlayer(exoPlayer);

        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.seekTo(0, 0);
        exoPlayer.prepare();
    }

    protected void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            loadVideo();
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
    }

    private TableRow createRow(String key, String value) {

        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(tableParams);


        TextView keyText = new TextView(this);
        keyText.setText(key);
        keyText.setTextSize(FontSettings.fontSize);
        keyText.setTypeface(typeface);
        rowParams.weight = 0.4f;
        keyText.setLayoutParams(rowParams);
        tableRow.addView(keyText);

        TextView valueText = new TextView(this);
        valueText.setText(value);
        valueText.setTextSize(FontSettings.fontSize - 2);
        valueText.setTypeface(typeface);
        rowParams.weight = 0.6f;
        valueText.setLayoutParams(rowParams);
        valueText.setGravity(Gravity.CENTER_VERTICAL);
        tableRow.addView(valueText);

        return tableRow;
    }

    private void loadAvatar() {
        // ?????????????????? ???????????? ?????????? ?? ??????????????????????????
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


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}