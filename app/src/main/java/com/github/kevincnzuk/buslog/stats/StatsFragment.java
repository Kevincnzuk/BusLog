package com.github.kevincnzuk.buslog.stats;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.kevincnzuk.buslog.R;
import com.github.kevincnzuk.buslog.adapter.StatsAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends Fragment {

    private static final String TAG = "StatsFragment";
    private static final String ARG_PARAM1 = "position";

    private int position;

    public StatsFragment() {
        // Required empty public constructor
    }

    public static StatsFragment newInstance(int position) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats,
                container, false);
        RecyclerView recyclerView = view.findViewById(R.id.fragment_stats_recycler_view);

        String columnName;
        switch (position) {
            case 2:
                columnName = "bus_route_number";
                break;
            case 1:
                columnName = "bus_model";
                break;
            case 0:
            default:
                columnName = "bus_number";
                break;
        }
        IStatsList statsList = new StatsList(getContext(), columnName);

        StatsAdapter adapter = new StatsAdapter(getContext(), statsList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }
}