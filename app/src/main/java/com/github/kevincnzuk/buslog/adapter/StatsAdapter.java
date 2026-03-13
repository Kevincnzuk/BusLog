/**
 * Statistics Adapter, handles the RecyclerView on StatisticsActivity.
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

package com.github.kevincnzuk.buslog.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.kevincnzuk.buslog.R;
import com.github.kevincnzuk.buslog.stats.StatsList;
import com.github.kevincnzuk.buslog.vo.StatsVO;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.ViewHolder> {

    private static final String TAG = "StatisticsAdapter";

    private Context context;
    private List<StatsVO> list;
    private int maximum = 0;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private LinearProgressIndicator indicator;
        private TextView count;

        public ViewHolder(@NonNull View view) {
            super(view);

            title = view.findViewById(R.id.item_stats_title);
            indicator = view.findViewById(R.id.item_stats_progress_indicator);
            count = view.findViewById(R.id.item_stats_count);
        }
    }

    public StatsAdapter(Context context, StatsList statsList) {
        this.context = context;
        this.list = new ArrayList<>();
        setNewStatsList(statsList);
    }

    public void setNewStatsList(StatsList statsList) {
        new Thread(() -> {
            List<StatsVO> voList = statsList.getStats(context);

            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(() -> {
                    this.list = voList;
                    notifyItemRangeChanged(0, voList.size());
                });
            }
        }).start();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stats_prog_bar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StatsVO vo = list.get(position);
        if (position == 0) maximum = vo.getCount();

        holder.title.setText(vo.getTitle());
        holder.count.setText(String.valueOf(vo.getCount()));

        holder.indicator.setMax(maximum);
        holder.indicator.setProgress(vo.getCount());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
