package com.github.kevincnzuk.buslog;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SQLiteDatabase db;
    private List<ListItem> displayList;

    private CoordinatorLayout layout;
    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private ExtendedFloatingActionButton buttonAddEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        displayList = new ArrayList<>();

        initComponents();
        initComponentActions();
    }

    private void initComponents() {
        layout = findViewById(R.id.main);
        toolbar = findViewById(R.id.main_toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        buttonAddEntry = findViewById(R.id.efab_add);

        setSupportActionBar(toolbar);
    }

    private void initComponentActions() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        initList();

        recyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY - oldScrollY > 0) {
                buttonAddEntry.shrink();
            } else {
                buttonAddEntry.extend();
            }
        });

        buttonAddEntry.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddActivity.class);
            startActivity(intent);
        });
    }

    private void initList() {
        initDatabase();

        MainAdapter adapter = new MainAdapter(this, displayList, null);

        OnItemInteractionListener listener = (vo, position) -> {
            displayList.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, displayList.size());

            Snackbar.make(layout, getString(R.string.main_delete_info, vo.getBusNumber()), Snackbar.LENGTH_LONG)
                    .setAction(R.string.main_delete_undo, v -> {
                        displayList.add(position, new ListItem(ListItem.TYPE_LOG, vo));
                        adapter.notifyItemInserted(position);
                    })
                    .addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            if (event != DISMISS_EVENT_ACTION) {
                                db.delete("logs", "id = ?", new String[]{String.valueOf(vo.getId())});
                            }
                        }
                    })
                    .show();
        };

        adapter.setListener(listener);

        recyclerView.setAdapter(adapter);
    }

    private void initDatabase() {
        SQLiteOpenHelper helper = new MyDatabaseHelper(this, "log.db", null, 1);
        db = helper.getWritableDatabase();

        Cursor cursor = db.query("logs", null, null, null, null, null, "create_time DESC");

        if (cursor.moveToFirst()) {
            displayList.clear();
            Calendar lastDate = null;

            do {
                EntryVO vo = new EntryVO();

                vo.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                vo.setBusNumber(cursor.getString(cursor.getColumnIndexOrThrow("bus_number")));
                vo.setBusModel(cursor.getString(cursor.getColumnIndexOrThrow("bus_model")));
                vo.setBusRouteNumber(cursor.getString(cursor.getColumnIndexOrThrow("bus_route_number")));
                vo.setBusRouteDestination(cursor.getString(cursor.getColumnIndexOrThrow("bus_route_destination")));
                long createTime = cursor.getLong(cursor.getColumnIndexOrThrow("create_time"));
                vo.setCreateTime(createTime);
                vo.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));

                Calendar thisDate = Calendar.getInstance();
                thisDate.setTimeInMillis(createTime);

                String thisDateFormatted = DateFormat.getDateInstance(DateFormat.MEDIUM).format(thisDate.getTime());

                if (lastDate == null || !thisDateFormatted
                        .equals(DateFormat.getDateInstance(DateFormat.MEDIUM).format(lastDate.getTime()))) {
                    displayList.add(new ListItem(ListItem.TYPE_HEADER, thisDateFormatted));
                    lastDate = thisDate;
                }

                displayList.add(new ListItem(ListItem.TYPE_LOG, vo));
            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initList();
    }
}