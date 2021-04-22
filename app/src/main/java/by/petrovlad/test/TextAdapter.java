package by.petrovlad.test;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.squareup.picasso.Picasso;

import java.util.List;

public class TextAdapter extends BaseAdapter {

    private Context context;
    private List<Kitten> kittens;
    private LayoutInflater inflater;
    private Typeface typeface;


    public TextAdapter(Context c, List<Kitten> kittens) {
        this.context = c;
        this.kittens = kittens;
        inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return kittens.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        typeface = ResourcesCompat.getFont(context, FontSettings.fontId);

        view = inflater.inflate(R.layout.text_grid_item, parent, false);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(FontSettings.fontSize, FontSettings.fontSize, FontSettings.fontSize, FontSettings.fontSize);

        Kitten kitten = kittens.get(position);

        String text = context.getString(R.string.kitten_name) + ": " + kitten.name + "\n"
                + context.getString(R.string.kitten_eyes_color) + ": " + kitten.eyesColor + "\n"
                + context.getString(R.string.kitten_tail_length) + ": " + kitten.tailLength + "\n"
                + context.getString(R.string.kitten_height) + ": "  + kitten.height;

        TextView textView = view.findViewById(R.id.tvGridItem);
        textView.setTextSize(FontSettings.fontSize);
        textView.setTypeface(typeface);
        textView.setLayoutParams(layoutParams);
        textView.setText(text);

        return view;
    }
}
