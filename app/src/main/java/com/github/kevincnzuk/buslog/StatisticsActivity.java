/**
 * Statistics Activity, shows the counts of different transportations user has travelled on.
 * Copyright (C) 2026  Leyuan Chang
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.kevincnzuk.buslog;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.kevincnzuk.buslog.adapter.StatisticsAdapter;
import com.github.kevincnzuk.buslog.stats.BusModelStatsList;
import com.github.kevincnzuk.buslog.stats.BusNumberStatsList;
import com.github.kevincnzuk.buslog.stats.BusRouteStatsList;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

public class StatisticsActivity extends AppCompatActivity {

    private static final String TAG = "StatisticsActivity";

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private StatisticsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_statistics);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initComponents();
        initComponentActions();
    }

    private void initComponents() {
        toolbar = findViewById(R.id.stats_toolbar);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.stats_tab_layout);
        recyclerView = findViewById(R.id.stats_recycler_view);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new StatisticsAdapter(this, new BusNumberStatsList());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    private void initComponentActions() {
        toolbar.setNavigationOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    adapter.setNewStatsList(new BusNumberStatsList());
                } else if (position == 1) {
                    adapter.setNewStatsList(new BusModelStatsList());
                } else if (position == 2) {
                    adapter.setNewStatsList(new BusRouteStatsList());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }
}