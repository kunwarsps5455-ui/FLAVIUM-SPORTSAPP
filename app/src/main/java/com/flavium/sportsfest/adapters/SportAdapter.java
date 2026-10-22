package com.flavium.sportsfest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flavium.sportsfest.R;
import com.flavium.sportsfest.models.Sport;

import java.util.List;

// Syllabus Topic: Custom ArrayAdapter
public class SportAdapter extends ArrayAdapter<Sport> {

    private final Context context;
    private final List<Sport> sports;

    public SportAdapter(Context context, List<Sport> sports) {
        super(context, R.layout.item_sport_row, sports);
        this.context = context;
        this.sports = sports;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_sport_row, parent, false);
        }

        Sport s = sports.get(position);

        TextView tvName = convertView.findViewById(R.id.tvSportName);
        TextView tvCategory = convertView.findViewById(R.id.tvSportCategory);
        TextView tvFormat = convertView.findViewById(R.id.tvSportFormat);
        TextView tvPlayers = convertView.findViewById(R.id.tvSportPlayers);

        tvName.setText(s.getName());
        tvCategory.setText(s.getCategory());
        tvFormat.setText("Rules: " + s.getDefaultFormat());
        tvPlayers.setText("Team: " + s.getMinPlayers() + (s.getMinPlayers() != s.getMaxPlayers() ? "-" + s.getMaxPlayers() : "") + " players");

        return convertView;
    }
}
