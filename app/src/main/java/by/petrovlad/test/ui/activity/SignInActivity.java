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

import by.petrovlad.test.Constants;
import by.petrovlad.test.FontSettings;
import by.petrovlad.test.R;

public class SignInActivity extends AppCompatActivity {

    private EditText edSignInEmail, edSignInPassword;
    private FirebaseAuth firebaseAuth;
    private TextView txtGoToSignUp;

    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        init();
    }

    private void init() {
        typeface = ResourcesCompat.getFont(this, FontSettings.fontId);

        edSignInEmail = findViewById(R.id.edSignInEmail);
        edSignInEmail.setTextSize(FontSettings.fontSize);
        edSignInEmail.setTypeface(typeface);

        edSignInPassword = findViewById(R.id.edSignInPassword);
        edSignInPassword.setTextSize(FontSettings.fontSize);
        edSignInPassword.setTypeface(typeface);

        txtGoToSignUp = findViewById(R.id.txtGoToSignUp);
        txtGoToSignUp.setTextSize(FontSettings.fontSize);
        txtGoToSignUp.setTypeface(typeface);

        String email = getIntent().getStringExtra(Constants.EMAIL_EXTRA);
        String password = getIntent().getStringExtra(Constants.PASSWORD_EXTRA);
        if (!email.isEmpty()) {
            edSignInEmail.setText(email);
        }
        if (!password.isEmpty()) {
            edSignInPassword.setText(password);
        }

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void onClickGoToSignUp(View view) {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        intent.putExtra(Constants.EMAIL_EXTRA, edSignInEmail.getText().toString().trim());
        intent.putExtra(Constants.PASSWORD_EXTRA, edSignInPassword.getText().toString().trim());

        startActivity(intent);
    }

    public void onClickSignIn(View view) {
        String email = edSignInEmail.getText().toString().trim();
        String password = edSignInPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.toast_enter_values, Toast.LENGTH_LONG).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user == null) {
                                Log.w("onClickSignIn:failed", "'user' was null");
                                return;
                            }
                            goToMainActivity(user.getEmail());
                        } else {
                            Log.w("onClickSignIn:failed", task.getException());
                            Toast.makeText(SignInActivity.this, R.string.toast_auth_failed, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void goToMainActivity(String extraEmail) {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        intent.putExtra(Constants.EMAIL_EXTRA, extraEmail);
        startActivity(intent);
    }
}