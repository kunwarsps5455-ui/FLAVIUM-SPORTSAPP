package com.flavium.sportsfest.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.flavium.sportsfest.R;
import com.flavium.sportsfest.adapters.StandingAdapter;
import com.flavium.sportsfest.database.DatabaseHelper;
import com.flavium.sportsfest.models.PoolStanding;
import com.flavium.sportsfest.models.Sport;

import java.util.ArrayList;
import java.util.List;

public class StandingsActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private Spinner spinnerSports, spinnerPools;
    private RadioGroup rgGenderFilter;
    private ListView lvStandings;
    private TextView tvEmptyStandings;

    private List<Sport> sportsList = new ArrayList<>();
    private int selectedSportId = 1;
    private String selectedGender = "Boys";
    private String selectedPool = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Standings & Leaderboard");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = DatabaseHelper.getInstance(this);

        spinnerSports = findViewById(R.id.spinnerSports);
        spinnerPools = findViewById(R.id.spinnerPools);
        rgGenderFilter = findViewById(R.id.rgGenderFilter);
        lvStandings = findViewById(R.id.lvStandings);
        tvEmptyStandings = findViewById(R.id.tvEmptyStandings);

        setupSpinners();

        int passedSportId = getIntent().getIntExtra("sport_id", 0);
        if (passedSportId > 0) {
            for (int i = 0; i < sportsList.size(); i++) {
                if (sportsList.get(i).getId() == passedSportId) {
                    spinnerSports.setSelection(i);
                    selectedSportId = passedSportId;
                    break;
                }
            }
        }

        String passedGender = getIntent().getStringExtra("gender");
        if ("Girls".equalsIgnoreCase(passedGender)) {
            rgGenderFilter.check(R.id.rbGirls);
            selectedGender = "Girls";
        } else {
            rgGenderFilter.check(R.id.rbBoys);
            selectedGender = "Boys";
        }

        rgGenderFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbGirls) {
                    selectedGender = "Girls";
                } else {
                    selectedGender = "Boys";
                }
                updatePoolsSpinner();
                loadStandings();
            }
        });
    }

    private void setupSpinners() {
        sportsList = db.getAllSports();
        List<String> sportNames = new ArrayList<>();
        for (Sport s : sportsList) {
            sportNames.add(s.getName());
        }

        ArrayAdapter<String> sportAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sportNames);
        sportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSports.setAdapter(sportAdapter);

        spinnerSports.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSportId = sportsList.get(position).getId();
                updatePoolsSpinner();
                loadStandings();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        updatePoolsSpinner();
    }

    private void updatePoolsSpinner() {
        List<String> pools = db.getDistinctPools(selectedSportId, selectedGender);
        List<String> poolOptions = new ArrayList<>();
        poolOptions.add("All Pools");
        poolOptions.addAll(pools);

        ArrayAdapter<String> poolAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, poolOptions);
        poolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPools.setAdapter(poolAdapter);

        spinnerPools.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedPool = "All";
                } else {
                    selectedPool = poolOptions.get(position);
                }
                loadStandings();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadStandings() {
        List<PoolStanding> standings = db.calculatePoolStandings(selectedSportId, selectedGender, selectedPool);

        if (standings.isEmpty()) {
            lvStandings.setVisibility(View.GONE);
            tvEmptyStandings.setVisibility(View.VISIBLE);
        } else {
            lvStandings.setVisibility(View.VISIBLE);
            tvEmptyStandings.setVisibility(View.GONE);

            // Syllabus Topic: Custom ArrayAdapter with ListView
            StandingAdapter adapter = new StandingAdapter(this, standings);
            lvStandings.setAdapter(adapter);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
