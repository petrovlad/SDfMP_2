package by.petrovlad.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ShowKittenActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private Button btnShowGallery;
    private GridView gvPhotos;
    private DatabaseReference databaseReference;

    private ImageView ivTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_kitten);

        init();
    }

    private void init() {
        btnShowGallery = findViewById(R.id.btnShowGallery);
        gvPhotos = findViewById(R.id.gvKittens);
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
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_IMAGE_REFERENCE + "/" + uid);
        loadPhotos();
    }

    private TableRow createRow(String key, String value) {
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

        TextView keyText = new TextView(this);
        keyText.setTextSize(16);
        keyText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
        keyText.setText(key);
        tableRow.addView(keyText);

        TextView valueText = new TextView(this);
        valueText.setText(value);
        valueText.setTextSize(16);
        valueText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tableRow.addView(valueText);

        return tableRow;
    }

    private void loadPhotos() {
        List<Upload> uploads = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    if (upload == null) {
                        Log.w("ShwKittenActivity.init:", "'upload' was null");
                        return;
                    }
                    uploads.add(upload);

                    ImageView iv = new ImageView(ShowKittenActivity.this);
                    Picasso.get().load(upload.getImageUrl()).into(ivTemp);
                    //gvPhotos.addView(iv);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ShwKittenActivity.init:", error.getMessage());
                Toast.makeText(ShowKittenActivity.this, R.string.toast_unable_load_photos, Toast.LENGTH_LONG);
            }
        });
    }

}