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
    private List<String> strings;
    private LayoutInflater inflater;
    private Typeface typeface;

    public TextAdapter(Context c, List<String> strings) {
        this.context = c;
        this.strings = strings;
        inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return strings.size();
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

        view = inflater.inflate(R.layout.text_grid_item, null);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(FontSettings.fontSize, FontSettings.fontSize, FontSettings.fontSize, FontSettings.fontSize);

        TextView textView = view.findViewById(R.id.tvGridItem);
        textView.setTextSize(FontSettings.fontSize);
        textView.setTypeface(typeface);
        textView.setLayoutParams(layoutParams);
        textView.setText(strings.get(position));
        return view;
    }
}
