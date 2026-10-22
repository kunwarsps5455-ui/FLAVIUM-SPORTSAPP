package com.flavium.sportsfest.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flavium.sportsfest.R;
import com.flavium.sportsfest.database.DatabaseHelper;
import com.flavium.sportsfest.models.Sport;
import com.flavium.sportsfest.models.Team;

import java.util.ArrayList;
import java.util.List;

public class AddEditTeamActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private Spinner spinnerSport;
    private EditText etTeamName, etOrganization;
    private RadioGroup rgGender;
    private RadioButton rbBoys, rbGirls;
    private Button btnSaveTeam;

    private List<Sport> sportsList;
    private long teamId = 0;
    private Team existingTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_team);

        db = DatabaseHelper.getInstance(this);

        spinnerSport = findViewById(R.id.spinnerSport);
        etTeamName = findViewById(R.id.etTeamName);
        etOrganization = findViewById(R.id.etOrganization);
        rgGender = findViewById(R.id.rgGender);
        rbBoys = findViewById(R.id.rbBoys);
        rbGirls = findViewById(R.id.rbGirls);
        btnSaveTeam = findViewById(R.id.btnSaveTeam);

        setupSportsSpinner();

        teamId = getIntent().getLongExtra("team_id", 0);
        if (teamId > 0) {
            existingTeam = db.getTeamById(teamId);
            if (existingTeam != null) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Edit Team");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
                populateExistingData();
            }
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Register New Team");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            int passedSportId = getIntent().getIntExtra("sport_id", 0);
            if (passedSportId > 0) {
                for (int i = 0; i < sportsList.size(); i++) {
                    if (sportsList.get(i).getId() == passedSportId) {
                        spinnerSport.setSelection(i);
                        break;
                    }
                }
            }
            String passedGender = getIntent().getStringExtra("gender");
            if ("Girls".equalsIgnoreCase(passedGender)) {
                rbGirls.setChecked(true);
            } else {
                rbBoys.setChecked(true);
            }
        }

        btnSaveTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTeam();
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
    }

    private void populateExistingData() {
        etTeamName.setText(existingTeam.getName());
        etOrganization.setText(existingTeam.getOrganization());
        if ("Girls".equalsIgnoreCase(existingTeam.getGender())) {
            rbGirls.setChecked(true);
        } else {
            rbBoys.setChecked(true);
        }

        for (int i = 0; i < sportsList.size(); i++) {
            if (sportsList.get(i).getId() == existingTeam.getSportId()) {
                spinnerSport.setSelection(i);
                break;
            }
        }
    }

    private void saveTeam() {
        String name = etTeamName.getText().toString().trim();
        String org = etOrganization.getText().toString().trim();
        String gender = rbGirls.isChecked() ? "Girls" : "Boys";

        if (name.isEmpty()) {
            etTeamName.setError("Team name is required");
            etTeamName.requestFocus();
            return;
        }

        int sportPos = spinnerSport.getSelectedItemPosition();
        if (sportPos < 0 || sportPos >= sportsList.size()) {
            Toast.makeText(this, "Please select a valid sport", Toast.LENGTH_SHORT).show();
            return;
        }

        Sport selectedSport = sportsList.get(sportPos);

        if (existingTeam == null) {
            Team newTeam = new Team(0, name, selectedSport.getId(), selectedSport.getName(), gender, org, "");
            long id = db.insertTeam(newTeam);
            if (id > 0) {
                Toast.makeText(this, "Team registered successfully!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to save team", Toast.LENGTH_SHORT).show();
            }
        } else {
            existingTeam.setName(name);
            existingTeam.setOrganization(org);
            existingTeam.setGender(gender);
            existingTeam.setSportId(selectedSport.getId());
            existingTeam.setSportName(selectedSport.getName());
            db.updateTeam(existingTeam);
            Toast.makeText(this, "Team updated successfully!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
