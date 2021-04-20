package by.petrovlad.test.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Locale;

import by.petrovlad.test.Constants;
import by.petrovlad.test.R;
import by.petrovlad.test.ui.dashboard.DashboardFragment;
import by.petrovlad.test.ui.map.MapFragment;
import by.petrovlad.test.ui.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private String currentLanguage;

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

        String email = getIntent().getStringExtra(Constants.EMAIL_EXTRA);
        if (email != null) {
            Toast.makeText(this, getString(R.string.toast_hello) + ", " + email, Toast.LENGTH_LONG).show();
        }

        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.KITTENS_REFERENCE);

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

        currentLanguage = Locale.getDefault().getLanguage();
    }
}