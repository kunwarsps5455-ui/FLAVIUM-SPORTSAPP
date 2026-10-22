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
import com.flavium.sportsfest.models.PoolStanding;

import java.util.List;

// Syllabus Topic: Custom ArrayAdapter
public class StandingAdapter extends ArrayAdapter<PoolStanding> {

    private final Context context;
    private final List<PoolStanding> standings;

    public StandingAdapter(Context context, List<PoolStanding> standings) {
        super(context, R.layout.item_standing_row, standings);
        this.context = context;
        this.standings = standings;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_standing_row, parent, false);
        }

        PoolStanding s = standings.get(position);

        TextView tvRank = convertView.findViewById(R.id.tvRank);
        TextView tvStandingTeamName = convertView.findViewById(R.id.tvStandingTeamName);
        TextView tvPlayed = convertView.findViewById(R.id.tvPlayed);
        TextView tvWon = convertView.findViewById(R.id.tvWon);
        TextView tvLost = convertView.findViewById(R.id.tvLost);
        TextView tvDrawn = convertView.findViewById(R.id.tvDrawn);
        TextView tvPoints = convertView.findViewById(R.id.tvPoints);

        tvRank.setText(String.valueOf(position + 1));
        tvStandingTeamName.setText(s.getTeamName() + (s.getPoolName() != null && !s.getPoolName().isEmpty() ? " (" + s.getPoolName() + ")" : ""));
        tvPlayed.setText(String.valueOf(s.getMatchesPlayed()));
        tvWon.setText(String.valueOf(s.getMatchesWon()));
        tvLost.setText(String.valueOf(s.getMatchesLost()));
        tvDrawn.setText(String.valueOf(s.getMatchesDrawn()));
        tvPoints.setText(String.valueOf(s.getPoints()));

        if (position % 2 == 0) {
            convertView.setBackgroundColor(Color.parseColor("#F9F9F9"));
        } else {
            convertView.setBackgroundColor(Color.WHITE);
        }

        return convertView;
    }
}
