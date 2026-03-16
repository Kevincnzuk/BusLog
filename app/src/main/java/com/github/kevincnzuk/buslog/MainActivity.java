/**
 * Main Activity, handle the majority of home page logics and interactions.
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

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.kevincnzuk.buslog.adapter.MainAdapter;
import com.github.kevincnzuk.buslog.helper.MyDatabaseHelper;
import com.github.kevincnzuk.buslog.helper.OnItemInteractionListener;
import com.github.kevincnzuk.buslog.vo.EntryVO;
import com.github.kevincnzuk.buslog.vo.ListItem;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofLocalizedDate(FormatStyle.MEDIUM);

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
            Instant lastInstance = null;

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

                Instant thisInstance = Instant.ofEpochMilli(createTime);

                ZonedDateTime zdt = thisInstance.atZone(ZoneId.systemDefault());

                if (lastInstance == null || !isSameDay(thisInstance, lastInstance)) {
                    Log.d(TAG, "initDatabase: createTime = " + createTime);
                    displayList.add(new ListItem(ListItem.TYPE_HEADER, zdt.format(FORMATTER)));
                    lastInstance = thisInstance;
                }

                displayList.add(new ListItem(ListItem.TYPE_LOG, vo));
            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    private boolean isSameDay(Instant i1, Instant i2) {
        LocalDate date1 = i1.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate date2 = i2.atZone(ZoneId.systemDefault()).toLocalDate();
        return date1.equals(date2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.main_menu_statistics) {
            Intent intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.main_menu_search) {
            Snackbar.make(layout, R.string.main_coming_soon, Snackbar.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initList();
    }
}