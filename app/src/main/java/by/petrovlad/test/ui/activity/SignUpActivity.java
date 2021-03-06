package by.petrovlad.test.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import by.petrovlad.test.Constants;
import by.petrovlad.test.FontSettings;
import by.petrovlad.test.Kitten;
import by.petrovlad.test.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText edSignUpLogin, edSignUpPassword, edSignUpTail, edSignUpColor, edSignUpEmail, edSignUpHeight;
    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private TextView txtGoToSignIn;

    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
    }

    private void init() {
        typeface = ResourcesCompat.getFont(this, FontSettings.fontId);

        edSignUpLogin = findViewById(R.id.edSignUpLogin);
        edSignUpLogin.setTextSize(FontSettings.fontSize);
        edSignUpLogin.setTypeface(typeface);

        edSignUpPassword = findViewById(R.id.edSignUpPassword);
        edSignUpPassword.setTextSize(FontSettings.fontSize);
        edSignUpPassword.setTypeface(typeface);

        edSignUpTail = findViewById(R.id.edSignUpTail);
        edSignUpTail.setTextSize(FontSettings.fontSize);
        edSignUpTail.setTypeface(typeface);

        edSignUpColor = findViewById(R.id.edSignUpColor);
        edSignUpColor.setTextSize(FontSettings.fontSize);
        edSignUpColor.setTypeface(typeface);

        edSignUpEmail = findViewById(R.id.edSignUpEmail);
        edSignUpEmail.setTextSize(FontSettings.fontSize);
        edSignUpEmail.setTypeface(typeface);

        edSignUpHeight = findViewById(R.id.edSignUpHeight);
        edSignUpHeight.setTextSize(FontSettings.fontSize);
        edSignUpHeight.setTypeface(typeface);

        txtGoToSignIn = findViewById(R.id.txtGoToSignIn);
        txtGoToSignIn.setTextSize(FontSettings.fontSize);
        txtGoToSignIn.setTypeface(typeface);

        String email = getIntent().getStringExtra(Constants.EMAIL_EXTRA);
        String password = getIntent().getStringExtra(Constants.PASSWORD_EXTRA);
        if (email != null && !email.isEmpty()) {
            edSignUpEmail.setText(email);
        }
        if (password != null && !password.isEmpty()) {
            edSignUpPassword.setText(password);
        }

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            goToMainActivity(currentUser.getEmail());
        }
    }

    public void onClickSignUp(View view) {
        String email = edSignUpEmail.getText().toString().trim();
        String login = edSignUpLogin.getText().toString().trim();
        String color = edSignUpColor.getText().toString().trim();
        String height = edSignUpHeight.getText().toString().trim();
        String tailLength = edSignUpTail.getText().toString().trim();
        String password = edSignUpPassword.getText().toString().trim();

        if (email.isEmpty() || color.isEmpty() || height.isEmpty() || login.isEmpty() || password.isEmpty() || tailLength.isEmpty()) {
            Toast.makeText(this, R.string.toast_enter_values, Toast.LENGTH_LONG).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            if (user == null) {
                                Log.w("onClickSignUp:failed", "user was null");
                                return;
                            }

                            // add entity to Kittens db
                            database.getReference(Constants.KITTENS_REFERENCE)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(new Kitten(login, email, tailLength, height, color));

                            goToMainActivity(user.getEmail());
                        } else {
                            Log.w("onClickSignUp:failed", task.getException());
                            Toast.makeText(SignUpActivity.this, R.string.toast_creation_user_failed, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void onClickGoToSignIn(View view) {
        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
        intent.putExtra(Constants.EMAIL_EXTRA, edSignUpEmail.getText().toString());
        intent.putExtra(Constants.PASSWORD_EXTRA, edSignUpPassword.getText().toString());

        startActivity(intent);
    }

    public void goToMainActivity(String extraEmail) {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        intent.putExtra(Constants.EMAIL_EXTRA, extraEmail);
        startActivity(intent);
    }
}