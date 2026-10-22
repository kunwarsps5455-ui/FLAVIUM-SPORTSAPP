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
import com.flavium.sportsfest.models.Team;

import java.util.List;

// Syllabus Topic: Custom ArrayAdapter
public class TeamAdapter extends ArrayAdapter<Team> {

    private final Context context;
    private final List<Team> teams;

    public TeamAdapter(Context context, List<Team> teams) {
        super(context, R.layout.item_team_row, teams);
        this.context = context;
        this.teams = teams;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_team_row, parent, false);
        }

        Team t = teams.get(position);

        TextView tvTeamName = convertView.findViewById(R.id.tvTeamName);
        TextView tvGenderTag = convertView.findViewById(R.id.tvGenderTag);
        TextView tvSportAndOrg = convertView.findViewById(R.id.tvSportAndOrg);
        TextView tvCandidateCount = convertView.findViewById(R.id.tvCandidateCount);

        tvTeamName.setText(t.getName());
        tvGenderTag.setText(t.getGender());
        if ("Girls".equalsIgnoreCase(t.getGender())) {
            tvGenderTag.setTextColor(Color.parseColor("#E91E63"));
        } else {
            tvGenderTag.setTextColor(Color.parseColor("#1976D2"));
        }

        String poolStr = (t.getPoolName() != null && !t.getPoolName().isEmpty()) ? (" • " + t.getPoolName()) : "";
        tvSportAndOrg.setText(t.getSportName() + poolStr + " (" + (t.getOrganization() != null ? t.getOrganization() : "General") + ")");
        tvCandidateCount.setText(t.getCandidateCount() + " Players");

        return convertView;
    }
}
