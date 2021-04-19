package by.petrovlad.test.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;


import java.util.Locale;

import by.petrovlad.test.Constants;
import by.petrovlad.test.R;

public class ChangeLanguageActivity extends AppCompatActivity {

    private GridView gvLanguages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);

        init();
    }

    private void init() {
        gvLanguages = findViewById(R.id.gvLanguages);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constants.LANGUAGES);
        gvLanguages.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        gvLanguages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setAppLocale(ChangeLanguageActivity.this, Constants.LANGUAGES[position]);
                ChangeLanguageActivity.this.recreate();
                Toast.makeText(ChangeLanguageActivity.this, R.string.toast_changes_saved, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAppLocale(Activity activity, String localeCode){

        Locale locale = new Locale(localeCode);
        locale.setDefault(locale);

        Resources resources = activity.getResources();
        Configuration configuration = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = new Locale(localeCode.toLowerCase());
        }
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

/*        Resources resources = ChangeLanguageActivity.this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            config.setLocale(new Locale(localeCode.toLowerCase()));
        } else {
            config.locale = new Locale(localeCode.toLowerCase());
        }
        resources.updateConfiguration(config, dm);*/

/*        Locale locale = new Locale(localeCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        ChangeLanguageActivity.this.getApplicationContext().getResources().updateConfiguration(config, null);*/
    }
}