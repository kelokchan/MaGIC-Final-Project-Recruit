package com.example.kelok_000.recruit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by kelok_000 on 10/11/2015.
 */
public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.ViewHolder> implements Filterable {

    private List<Candidate> candidates;
    private List<Candidate> fullCandidates;

    public CandidateAdapter(List<Candidate> candidates) {
        this.candidates = candidates;
        fullCandidates = this.candidates;
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    candidates = (List<Candidate>) results.values;
                    notifyDataSetChanged();
                }else{
                    candidates = fullCandidates;
                    notifyDataSetChanged();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<Candidate> FilteredArrayNames = new ArrayList<Candidate>();

                if (constraint == null || constraint.length() == 0) {
                    results.values = fullCandidates;
                    results.count = fullCandidates.size();
                } else {
                    // perform your search here using the searchConstraint String.

                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < candidates.size(); i++) {
                        String dataNames = candidates.get(i).most_experienced;
                        if (dataNames.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrayNames.add(candidates.get(i));
                        }
                    }

                    results.count = FilteredArrayNames.size();
                    results.values = FilteredArrayNames;
                    Log.e("VALUES", results.values.toString());
                }

                return results;
            }
        };

        return filter;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.person_name)
        TextView personName;
        @Bind(R.id.person_age)
        TextView personAge;
        @Bind(R.id.person_photo)
        CircleImageView personPhoto;
        @Bind(R.id.most_experienced)
        TextView personExp;
        @Bind(R.id.recommend)
        TextView recommend;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public CandidateAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_candidate, parent, false);
        ViewHolder pvh = new ViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(CandidateAdapter.ViewHolder holder, int i) {
        Context context = holder.personPhoto.getContext();
        holder.personName.setText(candidates.get(i).name);
        holder.personAge.setText(candidates.get(i).age);
        int gender = candidates.get(i).gender;
        if (gender == 0) {
            Picasso.with(context).load(R.drawable.ic_male).into(holder.personPhoto);
        } else {
            Picasso.with(context).load(R.drawable.ic_female).into(holder.personPhoto);
        }
        holder.personExp.setText(candidates.get(i).most_experienced);
        holder.recommend.setText(String.valueOf(candidates.get(i).recommend));
    }

    @Override
    public int getItemCount() {
        return candidates.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
