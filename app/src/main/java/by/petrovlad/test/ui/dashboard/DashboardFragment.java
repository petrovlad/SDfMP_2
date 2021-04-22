package by.petrovlad.test.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.media.VolumeShaper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.zip.Inflater;

import by.petrovlad.test.Constants;
import by.petrovlad.test.Kitten;
import by.petrovlad.test.R;
import by.petrovlad.test.TextAdapter;
import by.petrovlad.test.ui.activity.ShowKittenActivity;
import kotlin.Function;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private DatabaseReference databaseReference;
    private GridView gvKittens;
    private List<Kitten> allKittens, visibleKittens;
    private Map<Kitten, String> kittensMap;
    private List<String> keys;

    private Button button;

    private List<TableRow> filters;

    private TextAdapter textAdapter;
    private SearchView svName, svColor, svTailLength;
    private Spinner spnTailLength;

    private Context context;

    private String[] SPINNER_OPTIONS;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        init(root);

        return root;
    }

    private void init(View view) {
        context = getContext();

        SPINNER_OPTIONS = new String[] { context.getString(R.string.spinner_greater_than),
                getContext().getString(R.string.spinner_lower_than) };
/*      maybe in the future

        filters = new ArrayList<>();

        TableLayout tl = view.findViewById(R.id.tableLayout);
        button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableRow.LayoutParams tableParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

                LayoutInflater inflater = LayoutInflater.from(getContext());
                View view = inflater.inflate(R.layout.search_item, null);

                TableRow tableRow = new TableRow(getContext());
                tableRow.setLayoutParams(tableParams);
                tableRow.addView(view);

                filters.add(tableRow);

                tl.addView(tableRow);
            }
        });*/

        allKittens = new ArrayList<>();
        visibleKittens = new ArrayList<>();

        svName = view.findViewById(R.id.svName);
        svName.setOnQueryTextListener(this.onQueryTextListener);

        svColor = view.findViewById(R.id.svColor);
        svColor.setOnQueryTextListener(this.onQueryTextListener);

        svTailLength = view.findViewById(R.id.svTailLength);
        svTailLength.setOnQueryTextListener(this.onQueryTextListener);

        spnTailLength = view.findViewById(R.id.spnTailLength);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, SPINNER_OPTIONS);
        spnTailLength.setAdapter(spinnerAdapter);
        spnTailLength.setOnItemSelectedListener(this.onItemSelectedListener);

        spinnerAdapter.notifyDataSetChanged();


        // init list, set adapter on it and apply this adapter for gridview
        gvKittens = view.findViewById(R.id.gvKittens);
        keys = new ArrayList<>();
        kittensMap = new HashMap<>();
        // set adapter for gridview
        // every time when 'visibleKittens' changes, call 'notifyDataSetChanged' and do not recreate adapter
        // but will it work???
        textAdapter = new TextAdapter(getContext(), visibleKittens);
        gvKittens.setAdapter(textAdapter);

        // retrieve from firebase
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.KITTENS_REFERENCE);
        // set listener for realtime update
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                keys.clear();
                kittensMap.clear();
                allKittens.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Kitten kitten = ds.getValue(Kitten.class);
                    if (kitten == null) {
                        Log.w("DashboardFragment.init:", "'kitten' was null");
                        return;
                    }
                    allKittens.add(kitten);
                    keys.add(ds.getKey());

                    kittensMap.put(kitten, ds.getKey());
                }
                updateGridViewContent();
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
                Kitten kitten = visibleKittens.get(position);
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

                intent.putExtra(Constants.UID_EXTRA, kittensMap.get(kitten));

                startActivity(intent);
            }
        });
    }

    private void updateGridViewContent() {
        visibleKittens.clear();
        String queryName = svName.getQuery().toString().trim();
        String queryColor = svColor.getQuery().toString().trim();
        String queryTailLength = svTailLength.getQuery().toString().trim();

        if (queryName.isEmpty() && queryColor.isEmpty() && queryTailLength.isEmpty()) {
            visibleKittens.addAll(allKittens);
        } else {
            int tailLength;
            try {
                tailLength = Integer.parseInt(queryTailLength);
            } catch (Exception e) {
                tailLength = 0;
            }
            Comparator<Integer> comparator;
            if (svTailLength.getQuery().toString().trim().isEmpty()) {
                comparator = (x, y) -> 1;
            } else {
                if (spnTailLength.getSelectedItem().equals(SPINNER_OPTIONS[0])) {
                    comparator = (x, y) -> Integer.compare(y, x);
                } else {
                    comparator = (x, y) -> Integer.compare(x, y);
                }
            }

            for (Kitten kitty : allKittens) {
                if (kitty.name.contains(queryName)
                        && kitty.eyesColor.contains(queryColor)
                        && (comparator.compare(tailLength, Integer.parseInt(kitty.tailLength)) > 0)) {
                    visibleKittens.add(kitty);
                }
            }
        }
        textAdapter.notifyDataSetChanged();
    }

    private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            updateGridViewContent();
            return true;
        }
    };

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            updateGridViewContent();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            spnTailLength.setSelection(0);
        }
    };
}