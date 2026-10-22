package com.flavium.sportsfest.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flavium.sportsfest.R;
import com.flavium.sportsfest.database.DatabaseHelper;
import com.flavium.sportsfest.models.Fixture;

import java.util.Calendar;

public class MatchDetailActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private long fixtureId;
    private Fixture fixture;

    private TextView tvMatchTitle, tvMatchSub, tvTeam1Label, tvTeam2Label, tvScheduledDateTime;
    private EditText etScore1, etScore2, etNotes;
    private RadioGroup rgWinner;
    private RadioButton rbTeam1Wins, rbTeam2Wins, rbDraw;
    private Button btnSaveResult, btnPickDate, btnPickTime;

    private String selectedDate = "";
    private String selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);

        db = DatabaseHelper.getInstance(this);
        fixtureId = getIntent().getLongExtra("fixture_id", 0);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Record Match Score");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvMatchTitle = findViewById(R.id.tvMatchTitle);
        tvMatchSub = findViewById(R.id.tvMatchSub);
        tvTeam1Label = findViewById(R.id.tvTeam1Label);
        tvTeam2Label = findViewById(R.id.tvTeam2Label);
        tvScheduledDateTime = findViewById(R.id.tvScheduledDateTime);
        etScore1 = findViewById(R.id.etScore1);
        etScore2 = findViewById(R.id.etScore2);
        etNotes = findViewById(R.id.etNotes);
        rgWinner = findViewById(R.id.rgWinner);
        rbTeam1Wins = findViewById(R.id.rbTeam1Wins);
        rbTeam2Wins = findViewById(R.id.rbTeam2Wins);
        rbDraw = findViewById(R.id.rbDraw);
        btnSaveResult = findViewById(R.id.btnSaveResult);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnPickTime = findViewById(R.id.btnPickTime);

        loadFixtureData();

        // Syllabus Topic: DatePickerDialog using java.util.Calendar and OnDateSetListener
        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MatchDetailActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int y, int m, int d) {
                                selectedDate = d + "/" + (m + 1) + "/" + y;
                                updateScheduleDisplay();
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        // Syllabus Topic: TimePickerDialog using java.util.Calendar and OnTimeSetListener
        btnPickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(MatchDetailActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int h, int m) {
                                String minStr = (m < 10) ? ("0" + m) : String.valueOf(m);
                                selectedTime = h + ":" + minStr;
                                updateScheduleDisplay();
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        btnSaveResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveResult();
            }
        });
    }

    private void updateScheduleDisplay() {
        tvScheduledDateTime.setText("Scheduled: " + selectedDate + " " + selectedTime);
    }

    private void loadFixtureData() {
        fixture = db.getFixtureById(fixtureId);
        if (fixture == null) {
            Toast.makeText(this, "Match not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvMatchTitle.setText(fixture.getTeam1Name() + " vs " + fixture.getTeam2Name());
        tvMatchSub.setText(fixture.getSportName() + " (" + fixture.getGender() + ") • " + fixture.getRoundName() + " • " + fixture.getMatchFormat());

        tvTeam1Label.setText(fixture.getTeam1Name() + " Score:");
        tvTeam2Label.setText(fixture.getTeam2Name() + " Score:");

        rbTeam1Wins.setText("Winner: " + fixture.getTeam1Name());
        rbTeam2Wins.setText("Winner: " + fixture.getTeam2Name());
        rbDraw.setText("Draw / Tie / Shared");

        if (fixture.getScore1() != null) etScore1.setText(fixture.getScore1());
        if (fixture.getScore2() != null) etScore2.setText(fixture.getScore2());
        if (fixture.getNotes() != null) etNotes.setText(fixture.getNotes());

        if (fixture.getWinnerTeamId() == fixture.getTeam1Id()) {
            rbTeam1Wins.setChecked(true);
        } else if (fixture.getWinnerTeamId() == fixture.getTeam2Id() && fixture.getTeam2Id() > 0) {
            rbTeam2Wins.setChecked(true);
        } else if (fixture.getWinnerTeamId() == -1) {
            rbDraw.setChecked(true);
        }
    }

    private void saveResult() {
        String score1 = etScore1.getText().toString().trim();
        String score2 = etScore2.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        if (!selectedDate.isEmpty() || !selectedTime.isEmpty()) {
            notes = (notes.isEmpty() ? "" : notes + " | ") + "Time: " + selectedDate + " " + selectedTime;
        }

        long winnerId = 0;
        int checkedId = rgWinner.getCheckedRadioButtonId();
        if (checkedId == R.id.rbTeam1Wins) {
            winnerId = fixture.getTeam1Id();
        } else if (checkedId == R.id.rbTeam2Wins) {
            winnerId = fixture.getTeam2Id();
        } else if (checkedId == R.id.rbDraw) {
            winnerId = -1; // Draw
        }

        fixture.setScore1(score1);
        fixture.setScore2(score2);
        fixture.setWinnerTeamId(winnerId);
        fixture.setStatus("COMPLETED");
        fixture.setNotes(notes);

        db.updateFixture(fixture);
        Toast.makeText(this, "Match result recorded!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

