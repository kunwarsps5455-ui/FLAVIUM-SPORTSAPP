package com.flavium.sportsfest.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.flavium.sportsfest.R;
import com.flavium.sportsfest.database.DatabaseHelper;
import com.flavium.sportsfest.engine.FixtureGenerator;
import com.flavium.sportsfest.models.Fixture;
import com.flavium.sportsfest.models.Sport;
import com.flavium.sportsfest.models.Team;

import java.util.ArrayList;
import java.util.List;

public class FixtureWizardActivity extends AppCompatActivity {

    private DatabaseHelper db;

    private Spinner spinnerSport;
    private RadioGroup rgGender;
    private RadioButton rbBoys, rbGirls;
    private TextView tvAvailableTeamsInfo, tvGenderWarning;

    private RadioGroup rgTournamentType;
    private RadioButton rbLeaguePools, rbKnockout;

    private LinearLayout layoutPoolOptions;
    private EditText etNumPools, etLeagueMatches, etMatchFormat;
    private RadioGroup rgRoundRobinChoice;
    private RadioButton rbFullRoundRobin, rbCustomLeagueMatches;

    private Button btnGenerate;

    private List<Sport> sportsList;
    private Sport selectedSport;
    private String selectedGender = "Boys";
    private List<Team> availableTeams = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixture_wizard);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Arrange Fixtures & Pools");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = DatabaseHelper.getInstance(this);

        spinnerSport = findViewById(R.id.spinnerSport);
        rgGender = findViewById(R.id.rgGender);
        rbBoys = findViewById(R.id.rbBoys);
        rbGirls = findViewById(R.id.rbGirls);
        tvAvailableTeamsInfo = findViewById(R.id.tvAvailableTeamsInfo);
        tvGenderWarning = findViewById(R.id.tvGenderWarning);

        rgTournamentType = findViewById(R.id.rgTournamentType);
        rbLeaguePools = findViewById(R.id.rbLeaguePools);
        rbKnockout = findViewById(R.id.rbKnockout);

        layoutPoolOptions = findViewById(R.id.layoutPoolOptions);
        etNumPools = findViewById(R.id.etNumPools);
        etLeagueMatches = findViewById(R.id.etLeagueMatches);
        etMatchFormat = findViewById(R.id.etMatchFormat);
        rgRoundRobinChoice = findViewById(R.id.rgRoundRobinChoice);
        rbFullRoundRobin = findViewById(R.id.rbFullRoundRobin);
        rbCustomLeagueMatches = findViewById(R.id.rbCustomLeagueMatches);

        btnGenerate = findViewById(R.id.btnGenerate);

        setupSportsSpinner();

        int passedSportId = getIntent().getIntExtra("sport_id", 0);
        if (passedSportId > 0) {
            for (int i = 0; i < sportsList.size(); i++) {
                if (sportsList.get(i).getId() == passedSportId) {
                    spinnerSport.setSelection(i);
                    break;
                }
            }
        }

        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbGirls) {
                    selectedGender = "Girls";
                } else {
                    selectedGender = "Boys";
                }
                updateAvailableTeams();
            }
        });

        rgTournamentType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbLeaguePools) {
                    layoutPoolOptions.setVisibility(View.VISIBLE);
                } else {
                    layoutPoolOptions.setVisibility(View.GONE);
                }
            }
        });

        rgRoundRobinChoice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbCustomLeagueMatches) {
                    etLeagueMatches.setVisibility(View.VISIBLE);
                } else {
                    etLeagueMatches.setVisibility(View.GONE);
                }
            }
        });

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateFixtures();
            }
        });
    }

    private void setupSportsSpinner() {
        sportsList = db.getAllSports();
        List<String> names = new ArrayList<>();
        for (Sport s : sportsList) {
            names.add(s.getName() + " (" + s.getCategory() + ")");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSport.setAdapter(adapter);

        spinnerSport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSport = sportsList.get(position);
                etMatchFormat.setText(selectedSport.getDefaultFormat());
                updateAvailableTeams();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateAvailableTeams() {
        if (selectedSport == null) return;

        availableTeams = db.getTeams(selectedSport.getId(), selectedGender);
        int count = availableTeams.size();

        tvAvailableTeamsInfo.setText("✅ " + count + " " + selectedGender + " Teams registered for " + selectedSport.getName());
        tvGenderWarning.setText("🔒 Strict Gender Isolation Active: Only " + selectedGender + " teams will be pooled together.");

        // Smart defaults based on team count
        if (count >= 8) {
            etNumPools.setText("2"); // 2 pools for 8 teams
        } else if (count >= 16) {
            etNumPools.setText("4");
        } else {
            etNumPools.setText("1");
        }
    }

    private void generateFixtures() {
        if (selectedSport == null) {
            Toast.makeText(this, "Please select a sport", Toast.LENGTH_SHORT).show();
            return;
        }

        if (availableTeams.size() < 2) {
            new AlertDialog.Builder(this)
                    .setTitle("Insufficient Teams")
                    .setMessage("There are only " + availableTeams.size() + " " + selectedGender + " teams registered for " + selectedSport.getName() + ".\n\n" +
                            "At least 2 teams of the same gender are required to generate pools or fixtures.")
                    .setPositiveButton("Add Teams Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(FixtureWizardActivity.this, AddEditTeamActivity.class);
                            intent.putExtra("sport_id", selectedSport.getId());
                            intent.putExtra("gender", selectedGender);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return;
        }

        String matchFormat = etMatchFormat.getText().toString().trim();
        if (matchFormat.isEmpty()) {
            matchFormat = selectedSport.getDefaultFormat();
        }

        final String finalFormat = matchFormat;

        if (selectedSport.isAthletics()) {
            // Athletics special generator
            List<Fixture> fixtures = FixtureGenerator.generateAthleticsFixtures(db, selectedSport, selectedGender, availableTeams, finalFormat);
            showSuccessAndNavigate(fixtures.size(), "Athletics Heats/Attempts");
            return;
        }

        if (rbKnockout.isChecked()) {
            // Knockout generator
            List<Fixture> fixtures = FixtureGenerator.generateKnockoutFixtures(db, selectedSport, selectedGender, availableTeams, finalFormat);
            showSuccessAndNavigate(fixtures.size(), "Single Elimination Knockout");
        } else {
            // Pool / League stage generator
            int pools = 1;
            try {
                pools = Integer.parseInt(etNumPools.getText().toString().trim());
                if (pools <= 0) pools = 1;
            } catch (Exception e) {
                pools = 1;
            }

            int matchesLimit = -1; // Full round robin
            if (rbCustomLeagueMatches.isChecked()) {
                try {
                    matchesLimit = Integer.parseInt(etLeagueMatches.getText().toString().trim());
                } catch (Exception e) {
                    matchesLimit = -1;
                }
            }

            List<Fixture> fixtures = FixtureGenerator.generatePoolFixtures(
                    db,
                    selectedSport,
                    selectedGender,
                    availableTeams,
                    pools,
                    finalFormat,
                    matchesLimit
            );

            showSuccessAndNavigate(fixtures.size(), pools + " Pools (Round Robin League)");
        }
    }

    private void showSuccessAndNavigate(int fixtureCount, String typeDescription) {
        new AlertDialog.Builder(this)
                .setTitle("Fixtures Arranged Successfully!")
                .setMessage("Created " + fixtureCount + " matches for " + selectedSport.getName() + " (" + selectedGender + ").\n" +
                        "Format: " + typeDescription)
                .setPositiveButton("View Fixtures", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(FixtureWizardActivity.this, FixturesActivity.class);
                        intent.putExtra("sport_id", selectedSport.getId());
                        intent.putExtra("gender", selectedGender);
                        startActivity(intent);
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
