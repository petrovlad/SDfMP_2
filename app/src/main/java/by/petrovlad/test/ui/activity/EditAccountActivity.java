package by.petrovlad.test.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import by.petrovlad.test.Constants;
import by.petrovlad.test.FontSettings;
import by.petrovlad.test.Kitten;
import by.petrovlad.test.R;

public class EditAccountActivity extends AppCompatActivity {

    private TableLayout tlEditKitten;
    private TextView tvKittenName, tvKittenEyes, tvKittenHeight, tvKittenTail;
    private EditText etKittenName, etKittenEyes, etKittenHeight, etKittenTail;
    private Button btnSaveChanges;
    private Button btnEditLocation;
    private final TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);


    private String currentLogin;
    private String currentUid;

    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        init();
    }

    private void init() {
        typeface = ResourcesCompat.getFont(this, FontSettings.fontId);

        tlEditKitten = findViewById(R.id.tlEditKitten);

        ArrayList<String> headers = new ArrayList<>();

        tvKittenName = findViewById(R.id.tvKittenName);
        tvKittenName.setTextSize(FontSettings.fontSize);
        tvKittenName.setTypeface(typeface);

        tvKittenEyes = findViewById(R.id.tvKittenEyes);
        tvKittenEyes.setTextSize(FontSettings.fontSize);
        tvKittenEyes.setTypeface(typeface);

        tvKittenHeight = findViewById(R.id.tvKittenHeight);
        tvKittenHeight.setTextSize(FontSettings.fontSize);
        tvKittenHeight.setTypeface(typeface);

        tvKittenTail = findViewById(R.id.tvKittenTail);
        tvKittenTail.setTextSize(FontSettings.fontSize);
        tvKittenTail.setTypeface(typeface);

        etKittenName = findViewById(R.id.etKittenName);
        etKittenName.setTextSize(FontSettings.fontSize);
        etKittenName.setTypeface(typeface);

        etKittenEyes = findViewById(R.id.etKittenEyes);
        etKittenEyes.setTextSize(FontSettings.fontSize);
        etKittenEyes.setTypeface(typeface);

        etKittenHeight = findViewById(R.id.etKittenHeight);
        etKittenHeight.setTextSize(FontSettings.fontSize);
        etKittenHeight.setTypeface(typeface);

        etKittenTail = findViewById(R.id.etKittenTail);
        etKittenTail.setTextSize(FontSettings.fontSize);
        etKittenTail.setTypeface(typeface);

        String nameHeader = getString(R.string.kitten_name);
        String eyesHeader = getString(R.string.kitten_eyes_color);
        String tailHeader = getString(R.string.kitten_tail_length);
        String heightHeader = getString(R.string.kitten_height);

        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnSaveChanges.setTextSize(FontSettings.fontSize);
        btnSaveChanges.setTypeface(typeface);
        btnSaveChanges.setOnClickListener(EditAccountActivity.this::OnClickSaveChanges);

        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference(Constants.KITTENS_REFERENCE).child(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Kitten kitten = snapshot.getValue(Kitten.class);
                    if (kitten != null) {
                        currentLogin = kitten.name;
                        etKittenName.setText(kitten.name);
                        etKittenEyes.setText(kitten.eyesColor);
                        etKittenHeight.setText(kitten.height);
                        etKittenTail.setText(kitten.tailLength);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnEditLocation = findViewById(R.id.btnEditLocation);
        btnEditLocation.setTextSize(FontSettings.fontSize);
        btnEditLocation.setTypeface(typeface);
        btnEditLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditAccountActivity.this, SetLocationActivity.class);
                startActivity(intent);
            }
        });

    }

    public void OnClickSaveChanges(View view) {
        String login = etKittenName.getText().toString().trim();
        String tailLength = etKittenTail.getText().toString().trim();
        String height = etKittenHeight.getText().toString().trim();
        String color = etKittenEyes.getText().toString().trim();

        if (login.isEmpty() || height.isEmpty() || tailLength.isEmpty() || color.isEmpty()) {
            Toast.makeText(this, R.string.toast_enter_values, Toast.LENGTH_LONG).show();
            return;
        }

        Kitten newKitten = new Kitten(login, FirebaseAuth.getInstance().getCurrentUser().getEmail(), tailLength, height, color);
        FirebaseDatabase.getInstance().getReference(Constants.KITTENS_REFERENCE).child(currentUid).setValue(newKitten).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EditAccountActivity.this, R.string.toast_changes_saved, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(EditAccountActivity.this, R.string.toast_edit_failed, Toast.LENGTH_LONG).show();
                    Log.w("EditAcc.OnClickSave", task.getException().getMessage());
                }
            }
        });
    }
}