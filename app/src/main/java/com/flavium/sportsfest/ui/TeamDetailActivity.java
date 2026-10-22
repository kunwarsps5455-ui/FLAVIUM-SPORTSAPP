package com.flavium.sportsfest.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.flavium.sportsfest.R;
import com.flavium.sportsfest.adapters.CandidateAdapter;
import com.flavium.sportsfest.database.DatabaseHelper;
import com.flavium.sportsfest.models.Candidate;
import com.flavium.sportsfest.models.Team;

import java.util.List;

public class TeamDetailActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private long teamId;
    private Team team;

    private TextView tvTeamName, tvSport, tvGenderBadge, tvOrg, tvCandidateHeader, tvEmptyCandidates;
    private ListView lvCandidates;
    private Button btnAddCandidate, btnDeleteTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail);

        db = DatabaseHelper.getInstance(this);
        teamId = getIntent().getLongExtra("team_id", 0);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Team Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvTeamName = findViewById(R.id.tvTeamName);
        tvSport = findViewById(R.id.tvSport);
        tvGenderBadge = findViewById(R.id.tvGenderBadge);
        tvOrg = findViewById(R.id.tvOrg);
        tvCandidateHeader = findViewById(R.id.tvCandidateHeader);
        tvEmptyCandidates = findViewById(R.id.tvEmptyCandidates);
        lvCandidates = findViewById(R.id.lvCandidates);
        btnAddCandidate = findViewById(R.id.btnAddCandidate);
        btnDeleteTeam = findViewById(R.id.btnDeleteTeam);

        btnAddCandidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamDetailActivity.this, AddEditCandidateActivity.class);
                intent.putExtra("team_id", teamId);
                if (team != null) {
                    intent.putExtra("gender", team.getGender());
                }
                startActivity(intent);
            }
        });

        btnDeleteTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeleteTeam();
            }
        });

        lvCandidates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Candidate c = (Candidate) parent.getItemAtPosition(position);
                Intent intent = new Intent(TeamDetailActivity.this, AddEditCandidateActivity.class);
                intent.putExtra("candidate_id", c.getId());
                intent.putExtra("team_id", teamId);
                intent.putExtra("gender", team != null ? team.getGender() : "Boys");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTeamData();
    }

    private void loadTeamData() {
        team = db.getTeamById(teamId);
        if (team == null) {
            Toast.makeText(this, "Team not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTeamName.setText(team.getName());
        tvSport.setText("Sport: " + team.getSportName() + (team.getPoolName() != null && !team.getPoolName().isEmpty() ? " (" + team.getPoolName() + ")" : ""));
        tvOrg.setText("College / Department: " + (team.getOrganization() != null && !team.getOrganization().isEmpty() ? team.getOrganization() : "General"));

        tvGenderBadge.setText("Division: " + team.getGender());
        if ("Girls".equalsIgnoreCase(team.getGender())) {
            tvGenderBadge.setTextColor(Color.parseColor("#E91E63"));
        } else {
            tvGenderBadge.setTextColor(Color.parseColor("#1976D2"));
        }

        List<Candidate> candidates = db.getCandidatesForTeam(teamId);
        tvCandidateHeader.setText("Players / Candidate Roster (" + candidates.size() + ")");

        if (candidates.isEmpty()) {
            lvCandidates.setVisibility(View.GONE);
            tvEmptyCandidates.setVisibility(View.VISIBLE);
        } else {
            lvCandidates.setVisibility(View.VISIBLE);
            tvEmptyCandidates.setVisibility(View.GONE);

            // Syllabus: Custom ArrayAdapter with ListView
            CandidateAdapter adapter = new CandidateAdapter(this, candidates, new CandidateAdapter.OnCandidateDeleteListener() {
                @Override
                public void onDelete(Candidate candidate) {
                    confirmDeleteCandidate(candidate);
                }
            });
            lvCandidates.setAdapter(adapter);
        }
    }

    private void confirmDeleteTeam() {
        // Syllabus Topic: AlertDialog
        new AlertDialog.Builder(this)
                .setTitle("Delete Team")
                .setMessage("Are you sure you want to delete " + team.getName() + " and all its candidates? Fixtures involving this team will also be deleted.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteTeam(teamId);
                        Toast.makeText(TeamDetailActivity.this, "Team deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDeleteCandidate(final Candidate c) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Candidate")
                .setMessage("Remove " + c.getName() + " from the team roster?")
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteCandidate(c.getId());
                        loadTeamData();
                        Toast.makeText(TeamDetailActivity.this, "Candidate removed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
