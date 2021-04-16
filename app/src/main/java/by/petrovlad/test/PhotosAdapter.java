package by.petrovlad.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import by.petrovlad.test.R;
import by.petrovlad.test.Upload;

public class PhotosAdapter extends BaseAdapter {
    private Context context;
    private List<Upload> uploads;
    private LayoutInflater inflater;

    public PhotosAdapter(Context c, List<Upload> uploads) {
        this.context = c;
        this.uploads = uploads;
        inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return uploads.size();
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
        view = inflater.inflate(R.layout.photo_grid_item, null);

        ImageView imageView = view.findViewById(R.id.ivPhoto);
        TextView textView = view.findViewById(R.id.tvPhotoTitle);

        Picasso.get().load(uploads.get(position).getImageUrl()).placeholder(R.drawable.default_avatar).into(imageView);
        textView.setText(uploads.get(position).getName());

        return view;
    }
}
