package com.flavium.sportsfest.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flavium.sportsfest.R;
import com.flavium.sportsfest.database.DatabaseHelper;
import com.flavium.sportsfest.models.Candidate;
import com.flavium.sportsfest.models.Team;

public class AddEditCandidateActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private long candidateId = 0;
    private long teamId = 0;
    private String gender = "Boys";
    private Candidate existingCandidate;
    private Team team;

    private TextView tvTeamHeader;
    private EditText etCandidateName, etJerseyNumber, etRole, etContact;
    private Button btnSaveCandidate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_candidate);

        db = DatabaseHelper.getInstance(this);

        candidateId = getIntent().getLongExtra("candidate_id", 0);
        teamId = getIntent().getLongExtra("team_id", 0);
        gender = getIntent().getStringExtra("gender");

        team = db.getTeamById(teamId);
        if (team != null) {
            gender = team.getGender();
        }

        tvTeamHeader = findViewById(R.id.tvTeamHeader);
        etCandidateName = findViewById(R.id.etCandidateName);
        etJerseyNumber = findViewById(R.id.etJerseyNumber);
        etRole = findViewById(R.id.etRole);
        etContact = findViewById(R.id.etContact);
        btnSaveCandidate = findViewById(R.id.btnSaveCandidate);

        if (team != null) {
            tvTeamHeader.setText("Team: " + team.getName() + " (" + team.getSportName() + " - " + team.getGender() + ")");
        }

        if (candidateId > 0) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Candidate");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            loadCandidateData();
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Add Candidate");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        btnSaveCandidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCandidate();
            }
        });
    }

    private void loadCandidateData() {
        // Load candidate from candidates table
        for (Candidate c : db.getCandidatesForTeam(teamId)) {
            if (c.getId() == candidateId) {
                existingCandidate = c;
                etCandidateName.setText(c.getName());
                etJerseyNumber.setText(c.getJerseyNumber());
                etRole.setText(c.getRole());
                etContact.setText(c.getContact());
                break;
            }
        }
    }

    private void saveCandidate() {
        String name = etCandidateName.getText().toString().trim();
        String jersey = etJerseyNumber.getText().toString().trim();
        String role = etRole.getText().toString().trim();
        String contact = etContact.getText().toString().trim();

        // Syllabus Regex & Input Validation: [a-zA-Z ]+, setError(), requestFocus()
        if (name.isEmpty()) {
            etCandidateName.setError("Candidate name is required");
            etCandidateName.requestFocus();
            return;
        }

        if (!name.matches("[a-zA-Z ]+")) {
            etCandidateName.setError("Candidate name must contain alphabets only");
            etCandidateName.requestFocus();
            return;
        }

        // Syllabus Regex: Digit validation [0-9]+
        if (!jersey.isEmpty() && !jersey.matches("[0-9]+")) {
            etJerseyNumber.setError("Jersey number must be digits only");
            etJerseyNumber.requestFocus();
            return;
        }

        // Syllabus Regex: 10-Digit Phone Number Validation [0-9]{10}
        if (!contact.isEmpty() && !contact.matches("[0-9]{10}")) {
            etContact.setError("Contact number must be exactly 10 digits");
            etContact.requestFocus();
            return;
        }

        if (role.isEmpty()) {
            role = "Player";
        }

        if (existingCandidate == null) {
            Candidate newCandidate = new Candidate(0, teamId, team != null ? team.getName() : "", name, jersey, role, gender != null ? gender : "Boys", contact);
            long id = db.insertCandidate(newCandidate);
            if (id > 0) {
                Toast.makeText(this, "Candidate added successfully!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to add candidate", Toast.LENGTH_SHORT).show();
            }
        } else {
            existingCandidate.setName(name);
            existingCandidate.setJerseyNumber(jersey);
            existingCandidate.setRole(role);
            existingCandidate.setContact(contact);
            db.updateCandidate(existingCandidate);
            Toast.makeText(this, "Candidate updated!", Toast.LENGTH_SHORT).show();
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
