package com.flavium.sportsfest.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flavium.sportsfest.R;
import com.flavium.sportsfest.models.Candidate;

import java.util.List;

// Syllabus Topic: Custom ArrayAdapter
public class CandidateAdapter extends ArrayAdapter<Candidate> {

    public interface OnCandidateDeleteListener {
        void onDelete(Candidate candidate);
    }

    private final Context context;
    private final List<Candidate> candidates;
    private final OnCandidateDeleteListener deleteListener;

    public CandidateAdapter(Context context, List<Candidate> candidates, OnCandidateDeleteListener deleteListener) {
        super(context, R.layout.item_candidate_row, candidates);
        this.context = context;
        this.candidates = candidates;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_candidate_row, parent, false);
        }

        final Candidate c = candidates.get(position);

        TextView tvCandidateName = convertView.findViewById(R.id.tvCandidateName);
        TextView tvJerseyNumber = convertView.findViewById(R.id.tvJerseyNumber);
        TextView tvCandidateRole = convertView.findViewById(R.id.tvCandidateRole);
        TextView tvCandidateContact = convertView.findViewById(R.id.tvCandidateContact);
        ImageButton btnDeleteCandidate = convertView.findViewById(R.id.btnDeleteCandidate);

        tvCandidateName.setText(c.getName());
        tvJerseyNumber.setText((c.getJerseyNumber() != null && !c.getJerseyNumber().isEmpty()) ? ("#" + c.getJerseyNumber()) : "");
        tvCandidateRole.setText(c.getRole() != null && !c.getRole().isEmpty() ? c.getRole() : "Player");

        if (c.getContact() != null && !c.getContact().isEmpty()) {
            tvCandidateContact.setVisibility(View.VISIBLE);
            tvCandidateContact.setText("📞 " + c.getContact() + " (Tap to Call)");

            // Syllabus Topic: Implicit Intent - Call Dialer Using tel:number, Uri.parse()
            tvCandidateContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                    dialIntent.setData(Uri.parse("tel:" + c.getContact()));
                    context.startActivity(dialIntent);
                }
            });
        } else {
            tvCandidateContact.setVisibility(View.GONE);
        }

        btnDeleteCandidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteListener != null) {
                    deleteListener.onDelete(c);
                }
            }
        });

        return convertView;
    }
}
