package by.petrovlad.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import by.petrovlad.test.ui.dashboard.DashboardFragment;
import by.petrovlad.test.ui.map.MapFragment;
import by.petrovlad.test.ui.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private EditText edFirstName, edSecondName, edTailLength;
    private Button btnSubmit;
    private BottomNavigationView bottomNavigationView;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


/*        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_dashboard, R.id.navigation_map, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);*/

        init();
    }

    private void init() {
        String login = getIntent().getStringExtra(Constants.LOGIN_EXTRA);
        if (login != null) {
            Toast.makeText(this, getString(R.string.toast_hello) + ", " + login, Toast.LENGTH_LONG).show();
        }

        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.KITTEN_ENTITY);

        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.navigation_dashboard:
                        selectedFragment = new DashboardFragment();
                        break;
                    case R.id.navigation_map:
                        selectedFragment = new MapFragment();
                        break;
                    case R.id.navigation_settings:
                        selectedFragment = new SettingsFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, selectedFragment)
                        .commit();

                return true;
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new DashboardFragment()).commit();
    }


    public void onClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, SignUpActivity.class));
    }
}