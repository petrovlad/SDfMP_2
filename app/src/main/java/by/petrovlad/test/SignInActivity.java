package by.petrovlad.test;

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

public class SignInActivity extends AppCompatActivity {

    private EditText edSignInLogin, edSignInPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        init();
    }

    private void init() {
        edSignInLogin = findViewById(R.id.edSignInLogin);
        edSignInPassword = findViewById(R.id.edSignInPassword);

        String login = getIntent().getStringExtra(Constants.LOGIN_EXTRA);
        String password = getIntent().getStringExtra(Constants.PASSWORD_EXTRA);
        if (!login.isEmpty()) {
            edSignInLogin.setText(login);
        }
        if (!password.isEmpty()) {
            edSignInPassword.setText(password);
        }

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void onClickGoToSignUp(View view) {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        intent.putExtra(Constants.LOGIN_EXTRA, edSignInLogin.getText().toString().trim());
        intent.putExtra(Constants.PASSWORD_EXTRA, edSignInPassword.getText().toString().trim());

        startActivity(intent);
    }

    public void onClickSignIn(View view) {
        String login = edSignInLogin.getText().toString().trim();
        String password = edSignInPassword.getText().toString().trim();

        if (login.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.toast_enter_values, Toast.LENGTH_LONG).show();
            return;
        }

        String finalLogin = login + Constants.UPN_SUFFIX;
        firebaseAuth.signInWithEmailAndPassword(finalLogin, password)
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

    public void goToMainActivity(String extraLogin) {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        intent.putExtra(Constants.LOGIN_EXTRA, extraLogin);
        startActivity(intent);
    }
}