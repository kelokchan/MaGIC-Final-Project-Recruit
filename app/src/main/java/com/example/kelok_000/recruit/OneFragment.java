package com.example.kelok_000.recruit;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

;

public class OneFragment extends Fragment {

    private List<Candidate> candidates;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.rv)
    RecyclerView mRecyclerView;

    @Bind(R.id.filter_text)
    EditText mFilterText;

    private CandidateAdapter mAdapter;

    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initializeData() {
        candidates.add(new Candidate("Chere Ducksworth", "23", 0, R.mipmap.ic_launcher, "Promoter", 225));
        candidates.add(new Candidate("Lavery Maiss", "20", 1, R.mipmap.ic_launcher, "PR", 118));
        candidates.add(new Candidate("Gail Darbonne", "30", 0, R.mipmap.ic_launcher, "Supervisor", 102));
        candidates.add(new Candidate("Hai Bernat", "22", 1, R.mipmap.ic_launcher, "Typist", 100));
        candidates.add(new Candidate("Marlen Turbeville", "19", 1, R.mipmap.ic_launcher, "Promoter", 80));
        candidates.add(new Candidate("Willian Villani", "21", 0, R.mipmap.ic_launcher, "Ambassador", 60));
        candidates.add(new Candidate("Dorris Barrientes", "20", 1, R.mipmap.ic_launcher, "Distributor", 79));
        candidates.add(new Candidate("Barton Crank", "19", 0, R.mipmap.ic_launcher, "Telemarketer", 52));
        candidates.add(new Candidate("Marcell Jagger", "18", 0, R.mipmap.ic_launcher, "Manger", 32));
        candidates.add(new Candidate("Donnie Hooper", "48", 0, R.mipmap.ic_launcher, "Promoter", 12));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        ButterKnife.bind(this, view);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(llm);

        candidates = new ArrayList<>();
        initializeData();
        mAdapter = new CandidateAdapter(candidates);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        mFilterText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                mAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });


        return view;
    }

    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initializeData();
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 3000);
    }

}
