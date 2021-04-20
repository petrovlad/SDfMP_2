package by.petrovlad.test.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import by.petrovlad.test.Constants;
import by.petrovlad.test.Kitten;
import by.petrovlad.test.R;
import by.petrovlad.test.TextAdapter;
import by.petrovlad.test.ui.activity.ShowKittenActivity;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private DatabaseReference databaseReference;
    private GridView gvKittens;
    private ArrayAdapter<String> adapter;
    private List<String> names;
    private List<Kitten> kittens;
    private List<String> keys;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        init(root);

        return root;
    }

    private void init(View view) {
        // init list, set adapter on it and apply this adapter for gridview
        gvKittens = view.findViewById(R.id.gvKittens);
        names = new ArrayList<>();
        keys = new ArrayList<>();
        kittens = new ArrayList<>();

        //adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, names);
        TextAdapter textAdapter = new TextAdapter(getContext(), names);
        gvKittens.setAdapter(textAdapter);
        // retrieve from firebase
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.KITTENS_REFERENCE);
        // set listener for realtime update
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                names.clear();
                kittens.clear();
                keys.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Kitten kitten = ds.getValue(Kitten.class);
                    if (kitten == null) {
                        Log.w("DashboardFragment.init:", "'kitten' was null");
                        return;
                    }
                    names.add(kitten.name + "\n" +  kitten.eyesColor +"\n" + kitten.height);
                    kittens.add(kitten);
                    keys.add(ds.getKey());
                }
                textAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ShwKittenActivity.init:", error.getMessage());
                Toast.makeText(getActivity(), R.string.toast_unable_load_data, Toast.LENGTH_LONG).show();
            }
        });
        // set listener for clicking on item of gridview
        gvKittens.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Kitten kitten = kittens.get(position);
                Intent intent = new Intent(getActivity(), ShowKittenActivity.class);
                // put all headers to intent
                ArrayList<String> headers = new ArrayList<>();

                String nameHeader = getString(R.string.kitten_name);
                String emailHeader = getString(R.string.kitten_email);
                String eyesHeader = getString(R.string.kitten_eyes_color);
                String tailHeader = getString(R.string.kitten_tail_length);
                String heightHeader = getString(R.string.kitten_height);

                headers.add(nameHeader);
                headers.add(emailHeader);
                headers.add(eyesHeader);
                headers.add(tailHeader);
                headers.add(heightHeader);

                intent.putStringArrayListExtra(Constants.KITTEN_HEADERS, headers);

                intent.putExtra(nameHeader, kitten.name);
                intent.putExtra(emailHeader, kitten.email);
                intent.putExtra(eyesHeader, kitten.eyesColor);
                intent.putExtra(tailHeader, kitten.tailLength);
                intent.putExtra(heightHeader, kitten.height);

                intent.putExtra(Constants.UID_EXTRA, keys.get(position));

                startActivity(intent);
            }
        });
    }
}