package by.petrovlad.test.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import by.petrovlad.test.Constants;
import by.petrovlad.test.FontSettings;
import by.petrovlad.test.R;

public class ShowPhotoFromGalleryActivity extends AppCompatActivity {

    private Typeface typeface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo_from_gallery);

        init();
    }

    private void init() {
        typeface = ResourcesCompat.getFont(this, FontSettings.fontId);

        ImageView imageView = findViewById(R.id.ivPhotoFromGallery);
        TextView textView = findViewById(R.id.tvPhotoFromGalleryName);
        textView.setTypeface(typeface);

        String photoName = getIntent().getExtras().getString(Constants.IMAGE_NAME_EXTRA);
        String photoUrl = getIntent().getExtras().getString(Constants.IMAGE_URL_EXTRA);
        Picasso.get().load(photoUrl).placeholder(R.drawable.default_avatar).into(imageView);
        textView.setText(photoName);
        textView.setTextSize(FontSettings.fontSize);
    }
}