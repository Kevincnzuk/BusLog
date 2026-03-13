package com.github.kevincnzuk.buslog;

import static android.view.View.GONE;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder>{
    private static final String TAG = "MainAdapter";

    private Context context;
    private List<ListItem> items;
    private OnItemInteractionListener listener;

    public static abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class HeaderViewHolder extends ViewHolder {
        private TextView date;

        public HeaderViewHolder(@NonNull View view) {
            super(view);

            date = view.findViewById(R.id.item_main_entries_header_date);
        }
    }

    public static class LogViewHolder extends ViewHolder {
        private MaterialCardView cardView;
        private TextView busNumber;
        private TextView busModel;
        private TextView busRouteNumber;
        private TextView busRouteDestination;
        private TextView createTime;
        private TextView note;

        public LogViewHolder (View view) {
            super(view);

            cardView = view.findViewById(R.id.item_main_entries_card);
            busNumber = view.findViewById(R.id.item_main_entries_bus_number);
            busModel = view.findViewById(R.id.item_main_entries_bus_model);
            busRouteNumber = view.findViewById(R.id.item_main_entries_bus_route_number);
            busRouteDestination = view.findViewById(R.id.item_main_entries_bus_route_destination);
            createTime = view.findViewById(R.id.item_main_entries_create_time);
            note = view.findViewById(R.id.item_main_entries_note);
        }
    }

    public MainAdapter(Context c, List<ListItem> itemsNew, OnItemInteractionListener aListener) {
        this.context = c;
        this.items = itemsNew;
        this.listener = aListener;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ListItem.TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_main_entries_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_main_entries, parent, false);
            return new LogViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListItem listItem = items.get(position);

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).date.setText(listItem.getDateText());
        } else if (holder instanceof LogViewHolder) {
            EntryVO vo = listItem.getLog();

            ((LogViewHolder) holder).cardView.setOnLongClickListener(v -> {
                PopupMenu menu = new PopupMenu(context, v);

                menu.getMenuInflater().inflate(R.menu.card_menu, menu.getMenu());

                menu.setOnMenuItemClickListener(item -> {
                    int id = item.getItemId();

                    if (id == R.id.menu_card_edit) {
                        Intent intent = new Intent(context, AddActivity.class);
                        intent.putExtra("id", vo.getId());
                        context.startActivity(intent);
                        return true;
                    } else if (id == R.id.menu_card_delete) {
                        listener.onDelete(vo, position);
                        return true;
                    }
                    return false;
                });

                menu.show();

                return true;
            });

            ((LogViewHolder) holder).busNumber.setText(vo.getBusNumber());
            ((LogViewHolder) holder).busModel.setText(vo.getBusModel());
            ((LogViewHolder) holder).busRouteNumber.setText(vo.getBusRouteNumber());
            ((LogViewHolder) holder).busRouteDestination.setText(vo.getBusRouteDestination());

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(vo.getCreateTime());
            ((LogViewHolder) holder).createTime.setText(DateFormat
                    .getTimeInstance(DateFormat.SHORT).format(c.getTime()));

            if (vo.getNote() == null || vo.getNote().isBlank()) {
                ((LogViewHolder) holder).note.setVisibility(GONE);
            }
            ((LogViewHolder) holder).note.setText(vo.getNote());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setListener(OnItemInteractionListener listener) {
        this.listener = listener;
    }
}
