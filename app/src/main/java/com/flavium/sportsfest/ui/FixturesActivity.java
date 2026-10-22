package com.flavium.sportsfest.ui;

import android.content.Intent;
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
import com.flavium.sportsfest.adapters.FixtureAdapter;
import com.flavium.sportsfest.database.DatabaseHelper;
import com.flavium.sportsfest.models.Fixture;
import com.flavium.sportsfest.models.Sport;

import java.util.ArrayList;
import java.util.List;

public class FixturesActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private Spinner spinnerSports;
    private RadioGroup rgGenderFilter;
    private ListView lvFixtures;
    private TextView tvEmptyFixtures, tvFixtureCount;

    private List<Sport> sportsList = new ArrayList<>();
    private int selectedSportId = 0; // 0 means all
    private String selectedGender = "All"; // "All", "Boys", "Girls"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixtures);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Fixtures & Scores");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = DatabaseHelper.getInstance(this);

        spinnerSports = findViewById(R.id.spinnerSports);
        rgGenderFilter = findViewById(R.id.rgGenderFilter);
        lvFixtures = findViewById(R.id.lvFixtures);
        tvEmptyFixtures = findViewById(R.id.tvEmptyFixtures);
        tvFixtureCount = findViewById(R.id.tvFixtureCount);

        setupSpinners();

        int passedSportId = getIntent().getIntExtra("sport_id", 0);
        if (passedSportId > 0) {
            for (int i = 0; i < sportsList.size(); i++) {
                if (sportsList.get(i).getId() == passedSportId) {
                    spinnerSports.setSelection(i + 1);
                    break;
                }
            }
        }

        String passedGender = getIntent().getStringExtra("gender");
        if ("Girls".equalsIgnoreCase(passedGender)) {
            rgGenderFilter.check(R.id.rbGirls);
            selectedGender = "Girls";
        } else if ("Boys".equalsIgnoreCase(passedGender)) {
            rgGenderFilter.check(R.id.rbBoys);
            selectedGender = "Boys";
        }

        rgGenderFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbBoys) {
                    selectedGender = "Boys";
                } else if (checkedId == R.id.rbGirls) {
                    selectedGender = "Girls";
                } else {
                    selectedGender = "All";
                }
                loadFixtures();
            }
        });

        findViewById(R.id.btnOpenStandings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FixturesActivity.this, StandingsActivity.class);
                if (selectedSportId > 0) intent.putExtra("sport_id", selectedSportId);
                if (!"All".equals(selectedGender)) intent.putExtra("gender", selectedGender);
                startActivity(intent);
            }
        });

        // Syllabus Topic: ListView item click
        lvFixtures.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fixture fixture = (Fixture) parent.getItemAtPosition(position);
                Intent intent = new Intent(FixturesActivity.this, MatchDetailActivity.class);
                intent.putExtra("fixture_id", fixture.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFixtures();
    }

    private void setupSpinners() {
        sportsList = db.getAllSports();
        List<String> sportNames = new ArrayList<>();
        sportNames.add("All Sports");
        for (Sport s : sportsList) {
            sportNames.add(s.getName());
        }

        ArrayAdapter<String> sportAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sportNames);
        sportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSports.setAdapter(sportAdapter);

        spinnerSports.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedSportId = 0;
                } else {
                    selectedSportId = sportsList.get(position - 1).getId();
                }
                loadFixtures();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadFixtures() {
        List<Fixture> fixtures = db.getFixtures(selectedSportId, selectedGender, "All");
        tvFixtureCount.setText("Showing " + fixtures.size() + " Fixtures (" + selectedGender + ")");

        if (fixtures.isEmpty()) {
            lvFixtures.setVisibility(View.GONE);
            tvEmptyFixtures.setVisibility(View.VISIBLE);
        } else {
            lvFixtures.setVisibility(View.VISIBLE);
            tvEmptyFixtures.setVisibility(View.GONE);

            // Syllabus Topic: Custom ArrayAdapter with ListView
            FixtureAdapter adapter = new FixtureAdapter(this, fixtures);
            lvFixtures.setAdapter(adapter);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
