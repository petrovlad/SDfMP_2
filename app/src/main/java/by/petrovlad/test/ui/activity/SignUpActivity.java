package by.petrovlad.test.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import by.petrovlad.test.Constants;
import by.petrovlad.test.Kitten;
import by.petrovlad.test.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText edSignUpLogin, edSignUpPassword, edTailLength;
    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
    }

    private void init() {
        edSignUpLogin = findViewById(R.id.edSignUpLogin);
        edSignUpPassword = findViewById(R.id.edSignUpPassword);
        edTailLength = findViewById(R.id.edTailLength);

        String login = getIntent().getStringExtra(Constants.LOGIN_EXTRA);
        String password = getIntent().getStringExtra(Constants.PASSWORD_EXTRA);
        if (login != null && !login.isEmpty()) {
            edSignUpLogin.setText(login);
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
        String login = edSignUpLogin.getText().toString().trim();
        String password = edSignUpPassword.getText().toString().trim();
        String tailLength = edTailLength.getText().toString().trim();

        if (login.isEmpty() || password.isEmpty() || tailLength.isEmpty()) {
            Toast.makeText(this, R.string.toast_enter_values, Toast.LENGTH_LONG).show();
            return;
        }

        String finalLogin = login + Constants.UPN_SUFFIX;
        firebaseAuth.createUserWithEmailAndPassword(finalLogin, password)
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
                            database.getReference(Constants.KITTEN_ENTITY)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(new Kitten(finalLogin, tailLength, "100", tailLength));

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
        intent.putExtra(Constants.LOGIN_EXTRA, edSignUpLogin.getText().toString().trim());
        intent.putExtra(Constants.PASSWORD_EXTRA, edSignUpPassword.getText().toString().trim());

        startActivity(intent);
    }

    public void goToMainActivity(String extraLogin) {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        intent.putExtra(Constants.LOGIN_EXTRA, extraLogin);
        startActivity(intent);
    }
}