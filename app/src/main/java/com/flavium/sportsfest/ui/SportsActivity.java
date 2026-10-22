package com.flavium.sportsfest.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.flavium.sportsfest.R;
import com.flavium.sportsfest.adapters.SportAdapter;
import com.flavium.sportsfest.database.DatabaseHelper;
import com.flavium.sportsfest.models.Sport;

import java.util.List;

public class SportsActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private ListView lvSports;
    private TextView tvSportsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Flavium Sports Catalog");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = DatabaseHelper.getInstance(this);
        lvSports = findViewById(R.id.lvSports);
        tvSportsCount = findViewById(R.id.tvSportsCount);

        List<Sport> sports = db.getAllSports();
        tvSportsCount.setText(sports.size() + " Sports Disciplines Available");

        // Syllabus Topic: Custom ArrayAdapter with ListView
        SportAdapter adapter = new SportAdapter(this, sports);
        lvSports.setAdapter(adapter);

        // Syllabus Topic: ListView item click
        lvSports.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Sport sport = (Sport) parent.getItemAtPosition(position);

                // Syllabus Topic: AlertDialog
                new AlertDialog.Builder(SportsActivity.this)
                        .setTitle(sport.getName())
                        .setMessage("Category: " + sport.getCategory() + "\n" +
                                "Rules: " + sport.getDefaultFormat() + "\n" +
                                "Players: " + sport.getMinPlayers() + (sport.getMinPlayers() != sport.getMaxPlayers() ? " to " + sport.getMaxPlayers() : ""))
                        .setPositiveButton("View Teams", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(SportsActivity.this, TeamsActivity.class);
                                intent.putExtra("sport_id", sport.getId());
                                startActivity(intent);
                            }
                        })
                        .setNeutralButton("Arrange Fixtures", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(SportsActivity.this, FixtureWizardActivity.class);
                                intent.putExtra("sport_id", sport.getId());
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Close", null)
                        .show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
