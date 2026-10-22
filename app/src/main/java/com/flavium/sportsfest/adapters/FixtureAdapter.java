package com.flavium.sportsfest.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flavium.sportsfest.R;
import com.flavium.sportsfest.models.Fixture;

import java.util.List;

// Syllabus Topic: Custom ArrayAdapter
public class FixtureAdapter extends ArrayAdapter<Fixture> {

    private final Context context;
    private final List<Fixture> fixtures;

    public FixtureAdapter(Context context, List<Fixture> fixtures) {
        super(context, R.layout.item_fixture_row, fixtures);
        this.context = context;
        this.fixtures = fixtures;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_fixture_row, parent, false);
        }

        Fixture f = fixtures.get(position);

        TextView tvRound = convertView.findViewById(R.id.tvRound);
        TextView tvStatus = convertView.findViewById(R.id.tvStatus);
        TextView tvMatchVersus = convertView.findViewById(R.id.tvMatchVersus);
        TextView tvScoreAndFormat = convertView.findViewById(R.id.tvScoreAndFormat);
        TextView tvWinnerResult = convertView.findViewById(R.id.tvWinnerResult);

        tvRound.setText(f.getRoundName());
        tvMatchVersus.setText(f.getTeam1Name() + " vs " + (f.getTeam2Name() != null && !f.getTeam2Name().isEmpty() ? f.getTeam2Name() : "TBD"));

        if (f.isCompleted()) {
            tvStatus.setText("COMPLETED");
            tvStatus.setTextColor(Color.parseColor("#2E7D32"));

            String score1 = f.getScore1() != null ? f.getScore1() : "-";
            String score2 = f.getScore2() != null ? f.getScore2() : "-";
            tvScoreAndFormat.setText("Scores: " + f.getTeam1Name() + " (" + score1 + ") vs " + f.getTeam2Name() + " (" + score2 + ")");

            tvWinnerResult.setVisibility(View.VISIBLE);
            if (f.getWinnerTeamId() > 0) {
                tvWinnerResult.setText("🏆 Winner: " + f.getWinnerTeamName());
                tvWinnerResult.setTextColor(Color.parseColor("#2E7D32"));
            } else if (f.getWinnerTeamId() == -1) {
                tvWinnerResult.setText("🤝 Match Drawn / Tied");
                tvWinnerResult.setTextColor(Color.parseColor("#1565C0"));
            } else {
                tvWinnerResult.setVisibility(View.GONE);
            }
        } else {
            tvStatus.setText("SCHEDULED");
            tvStatus.setTextColor(Color.parseColor("#E65100"));
            tvScoreAndFormat.setText("Rules: " + f.getMatchFormat() + " | Tap to record score");
            tvWinnerResult.setVisibility(View.GONE);
        }

        return convertView;
    }
}
