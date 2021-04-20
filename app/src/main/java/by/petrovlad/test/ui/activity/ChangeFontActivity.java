package by.petrovlad.test.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import by.petrovlad.test.FontSettings;
import by.petrovlad.test.R;

public class ChangeFontActivity extends AppCompatActivity {

    private TextView tvFontName, tvFontSize;
    private Spinner spnFontNames, spnFontSizes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_font);

        init();
    }

    private void init() {
        spnFontNames = findViewById(R.id.spnFontNames);

        tvFontName = findViewById(R.id.tvFontName);

        spnFontSizes = findViewById(R.id.spnFontSizes);

        tvFontSize = findViewById(R.id.tvFontSize);

        Resources res = getResources();
        //ArrayList<String> fontNames = getFontNames(res);
        ArrayList<String> fontNames = new ArrayList<>();
        ArrayList<Integer> fonts = new ArrayList<>();
        for (Field field : R.font.class.getFields()) {
            try {
                fonts.add(field.getInt(null));
            } catch (Exception e) {
                Log.w("ChangeFont:init", e.getMessage());
            }
        }
        for (int item : fonts) {
            String fontName = res.getResourceName(item);
            fontNames.add(fontName.substring(fontName.lastIndexOf("/") + 1));
        }
        ArrayAdapter<String> stringAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fontNames);
        spnFontNames.setAdapter(stringAdapter);
        stringAdapter.notifyDataSetChanged();

        List<Integer> fontSizes = Arrays.asList(FontSettings.FONT_SIZES);
        ArrayAdapter<Integer> intAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, fontSizes);
        spnFontSizes.setAdapter(intAdapter);
        intAdapter.notifyDataSetChanged();
    }

    private ArrayList<String> getFontNames(Resources res) {
        ArrayList<String> result = new ArrayList<>();
        return result;
    }
}