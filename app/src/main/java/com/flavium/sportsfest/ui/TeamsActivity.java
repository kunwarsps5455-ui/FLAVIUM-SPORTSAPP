package com.flavium.sportsfest.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.flavium.sportsfest.R;
import com.flavium.sportsfest.adapters.TeamAdapter;
import com.flavium.sportsfest.database.DatabaseHelper;
import com.flavium.sportsfest.models.Sport;
import com.flavium.sportsfest.models.Team;

import java.util.ArrayList;
import java.util.List;

public class TeamsActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private Spinner spinnerSports;
    private RadioGroup rgGenderFilter;
    private ListView lvTeams;
    private TextView tvEmptyTeams, tvTeamCountHeader;
    private Button btnAddTeam;

    private List<Sport> sportsList = new ArrayList<>();
    private int selectedSportId = 0; // 0 means all
    private String selectedGender = "All"; // "All", "Boys", "Girls"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Teams & Candidates");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = DatabaseHelper.getInstance(this);

        spinnerSports = findViewById(R.id.spinnerSports);
        rgGenderFilter = findViewById(R.id.rgGenderFilter);
        lvTeams = findViewById(R.id.lvTeams);
        tvEmptyTeams = findViewById(R.id.tvEmptyTeams);
        tvTeamCountHeader = findViewById(R.id.tvTeamCountHeader);
        btnAddTeam = findViewById(R.id.btnAddTeam);

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
                loadTeams();
            }
        });

        btnAddTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamsActivity.this, AddEditTeamActivity.class);
                if (selectedSportId > 0) intent.putExtra("sport_id", selectedSportId);
                if (!"All".equals(selectedGender)) intent.putExtra("gender", selectedGender);
                startActivity(intent);
            }
        });

        // Syllabus Topic: ListView item click
        lvTeams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Team team = (Team) parent.getItemAtPosition(position);
                Intent intent = new Intent(TeamsActivity.this, TeamDetailActivity.class);
                intent.putExtra("team_id", team.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTeams();
    }

    private void setupSpinners() {
        sportsList = db.getAllSports();
        List<String> sportNames = new ArrayList<>();
        sportNames.add("All Sports (" + sportsList.size() + ")");
        for (Sport s : sportsList) {
            sportNames.add(s.getName());
        }

        // Syllabus Topic: ArrayAdapter for Spinner
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
                loadTeams();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadTeams() {
        List<Team> teams = db.getTeams(selectedSportId, selectedGender);
        tvTeamCountHeader.setText("Showing " + teams.size() + " Teams (" + selectedGender + ")");

        if (teams.isEmpty()) {
            lvTeams.setVisibility(View.GONE);
            tvEmptyTeams.setVisibility(View.VISIBLE);
        } else {
            lvTeams.setVisibility(View.VISIBLE);
            tvEmptyTeams.setVisibility(View.GONE);

            // Syllabus Topic: Custom ArrayAdapter for ListView
            TeamAdapter adapter = new TeamAdapter(this, teams);
            lvTeams.setAdapter(adapter);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
