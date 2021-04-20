package by.petrovlad.test.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import by.petrovlad.test.Constants;
import by.petrovlad.test.FontSettings;
import by.petrovlad.test.R;

public class ChangeFontActivity extends AppCompatActivity {

    // TODO: значения в спиннерах - текущие

    private TextView tvFontName, tvFontSize;
    private Spinner spnFontNames, spnFontSizes;
    private Button btnApplyChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_font);

        init();
    }

    private void init() {
        Typeface typeface = ResourcesCompat.getFont(this, FontSettings.fontId);

        spnFontNames = findViewById(R.id.spnFontNames);

        tvFontName = findViewById(R.id.tvFontName);
        tvFontName.setTypeface(typeface);

        spnFontSizes = findViewById(R.id.spnFontSizes);

        tvFontSize = findViewById(R.id.tvFontSize);
        tvFontSize.setTypeface(typeface);

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
        //spnFontSizes.setOnItemClickListener(this::onSpnItemClick);

        List<Integer> fontSizes = Arrays.asList(FontSettings.FONT_SIZES);
        ArrayAdapter<Integer> intAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, fontSizes);
        spnFontSizes.setAdapter(intAdapter);
        intAdapter.notifyDataSetChanged();
        //spnFontSizes.setOnItemClickListener(this::onSpnItemClick);

        btnApplyChanges = findViewById(R.id.btnApplyChanges);
        btnApplyChanges.setTextSize(FontSettings.fontSize);
        btnApplyChanges.setTypeface(typeface);
        btnApplyChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedFont = fontNames.get(spnFontNames.getSelectedItemPosition());
                int selectedFontId = fonts.get(spnFontNames.getSelectedItemPosition());
                FontSettings.fontId = selectedFontId;

                int size = fontSizes.get(spnFontSizes.getSelectedItemPosition());
                FontSettings.fontSize = size;

                Intent intent = new Intent(ChangeFontActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                ChangeFontActivity.this.recreate();
                Toast.makeText(ChangeFontActivity.this, R.string.toast_changes_saved, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ArrayList<String> getFontNames(Resources res) {
        ArrayList<String> result = new ArrayList<>();
        return result;
    }

    private void onSpnItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (btnApplyChanges.getVisibility() == View.VISIBLE) {
            btnApplyChanges.setVisibility(View.INVISIBLE);
        }
    }
}