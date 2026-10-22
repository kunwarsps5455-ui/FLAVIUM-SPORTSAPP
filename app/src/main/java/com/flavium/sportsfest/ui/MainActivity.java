package com.flavium.sportsfest.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.flavium.sportsfest.R;
import com.flavium.sportsfest.database.DatabaseHelper;
import com.flavium.sportsfest.database.SampleDataSeeder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private TextView tvTotalTeams, tvTotalCandidates, tvTotalFixtures, tvCompletedMatches;

    // Syllabus Topic: BroadcastReceiver for monitoring system events (Airplane Mode Changes)
    private BroadcastReceiver airplaneModeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())) {
                boolean isAirplaneModeOn = intent.getBooleanExtra("state", false);
                String msg = isAirplaneModeOn ? "Airplane Mode is ON" : "Airplane Mode is OFF";
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Flavium Sports Fest");
        }

        db = DatabaseHelper.getInstance(this);

        tvTotalTeams = findViewById(R.id.tvTotalTeams);
        tvTotalCandidates = findViewById(R.id.tvTotalCandidates);
        tvTotalFixtures = findViewById(R.id.tvTotalFixtures);
        tvCompletedMatches = findViewById(R.id.tvCompletedMatches);

        Button btnArrangeFixtures = findViewById(R.id.btnArrangeFixtures);
        Button btnTeams = findViewById(R.id.btnTeams);
        Button btnFixtures = findViewById(R.id.btnFixtures);
        Button btnStandings = findViewById(R.id.btnStandings);
        Button btnSports = findViewById(R.id.btnSports);
        Button btnRules = findViewById(R.id.btnRules);
        Button btnVenueMap = findViewById(R.id.btnVenueMap);
        Button btnLoadDemo = findViewById(R.id.btnLoadDemo);
        Button btnExport = findViewById(R.id.btnExport);
        Button btnClearData = findViewById(R.id.btnClearData);

        // Syllabus Topic: Explicit Intents
        btnArrangeFixtures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FixtureWizardActivity.class);
                startActivity(intent);
            }
        });

        btnTeams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TeamsActivity.class);
                startActivity(intent);
            }
        });

        btnFixtures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FixturesActivity.class);
                startActivity(intent);
            }
        });

        btnStandings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StandingsActivity.class);
                startActivity(intent);
            }
        });

        btnSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SportsActivity.class);
                startActivity(intent);
            }
        });

        btnRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RulesWebViewActivity.class);
                startActivity(intent);
            }
        });

        // Syllabus Topic: Implicit Intent - Google Maps using geo:lat,lng?q=query
        btnVenueMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:28.5355,77.3910?q=Flavium+Main+Sports+Complex");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Map coordinates: 28.5355° N, 77.3910° E", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLoadDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmLoadDemoData();
            }
        });

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndReadFileSummary();
            }
        });

        btnClearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmClearData();
            }
        });
    }

    // Syllabus Topic: Activity Lifecycle (onStart, onResume, onStop)
    @Override
    protected void onStart() {
        super.onStart();
        // Syllabus Topic: Dynamic Registration Using registerReceiver()
        IntentFilter filter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(airplaneModeReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStats();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Syllabus Topic: Dynamic Broadcast Receiver Unregistration
        try {
            unregisterReceiver(airplaneModeReceiver);
        } catch (Exception ignored) {}
    }

    private void refreshStats() {
        Map<String, Integer> stats = db.getFestStats();
        tvTotalTeams.setText(String.valueOf(stats.get("total_teams")));
        tvTotalCandidates.setText(String.valueOf(stats.get("total_candidates")));
        tvTotalFixtures.setText(String.valueOf(stats.get("total_fixtures")));
        tvCompletedMatches.setText(String.valueOf(stats.get("completed_matches")));
    }

    // Syllabus Topic: Internal File Handling (openFileOutput, openFileInput, MODE_PRIVATE, byte streams)
    private void saveAndReadFileSummary() {
        try {
            String report = "=== FLAVIUM SPORTS FESTIVAL INTERNAL REPORT ===\n"
                    + "Total Teams Registered: " + tvTotalTeams.getText().toString() + "\n"
                    + "Total Players/Candidates: " + tvTotalCandidates.getText().toString() + "\n"
                    + "Total Fixtures Arranged: " + tvTotalFixtures.getText().toString() + "\n"
                    + "Matches Completed: " + tvCompletedMatches.getText().toString() + "\n";

            // Writing File Using openFileOutput and MODE_PRIVATE
            FileOutputStream fos = openFileOutput("flavium_summary.txt", MODE_PRIVATE);
            fos.write(report.getBytes());
            fos.close();

            // Reading File Using openFileInput and Reading Byte Streams
            FileInputStream fis = openFileInput("flavium_summary.txt");
            byte[] buffer = new byte[fis.available()];
            int length = fis.read(buffer);
            fis.close();

            String readContent = new String(buffer, 0, length);

            // Syllabus Topic: AlertDialog
            new AlertDialog.Builder(this)
                    .setTitle("File Handling Result")
                    .setMessage("Data written to and read from internal storage (MODE_PRIVATE):\n\n" + readContent)
                    .setPositiveButton("OK", null)
                    .show();

        } catch (IOException e) {
            Toast.makeText(this, "File error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmLoadDemoData() {
        new AlertDialog.Builder(this)
                .setTitle("Load Preloaded Demo Fest Data?")
                .setMessage("This will seed:\n" +
                        "• 8 Cricket Teams (Boys) with 8-over Pool Fixtures\n" +
                        "• 4 Cricket Teams (Girls) in Knockout format\n" +
                        "• Football, Badminton, and Athletics 100m heats\n" +
                        "• Roster of candidates for all teams")
                .setPositiveButton("Load Data", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SampleDataSeeder.seedDemoData(db);
                        refreshStats();
                        Toast.makeText(MainActivity.this, "Demo fest data loaded!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmClearData() {
        new AlertDialog.Builder(this)
                .setTitle("Clear All Fest Data")
                .setMessage("Are you sure you want to delete all registered teams, candidates, and fixtures?")
                .setPositiveButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.clearAllData();
                        refreshStats();
                        Toast.makeText(MainActivity.this, "Fest data cleared", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
