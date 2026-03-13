package com.github.kevincnzuk.buslog;

import static com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class AddActivity extends AppCompatActivity {

    private static final String TAG = "AddActivity";

    private MaterialToolbar toolbar;

    private EntryVO vo;
    private Calendar localCal;
    private Calendar utcCal;
    private SQLiteOpenHelper helper;
    private SQLiteDatabase db;
    private boolean editMode = false;

    private TextInputLayout tiLayoutBusNumber;
    private TextInputLayout tiLayoutBusModel;
    private TextInputLayout tiLayoutBusRouteNumber;
    private TextInputLayout tiLayoutBusRouteDestination;
    private TextInputLayout tiLayoutDate;
    private TextInputLayout tiLayoutTime;
    private TextInputLayout tiLayoutNote;

    private MaterialAutoCompleteTextView tiEditTextBusNumber;
    private MaterialAutoCompleteTextView tiEditTextBusModel;
    private MaterialAutoCompleteTextView tiEditTextBusRouteNumber;
    private MaterialAutoCompleteTextView tiEditTextBusRouteDestination;
    private TextInputEditText tiEditTextDate;
    private TextInputEditText tiEditTextTime;
    private TextInputEditText tiEditTextNote;

    private MaterialButton buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        helper = new MyDatabaseHelper(this, "log.db", null, 1);
        db = helper.getWritableDatabase();

        localCal = Calendar.getInstance();
        utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            int id = intent.getIntExtra("id", -1);

            try {
                if (id < 0)
                    throw new IndexOutOfBoundsException(getString(R.string.add_error_entry_not_exist));

                Cursor cursor = db.query(
                        "logs",
                        null,
                        "id = ?",
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        null
                );

                if (cursor.moveToFirst()) {
                    vo = new EntryVO();

                    long createTime = cursor.getLong(cursor.getColumnIndexOrThrow("create_time"));

                    vo.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    vo.setBusNumber(cursor.getString(cursor.getColumnIndexOrThrow("bus_number")));
                    vo.setBusModel(cursor.getString(cursor.getColumnIndexOrThrow("bus_model")));
                    vo.setBusRouteNumber(cursor.getString(cursor.getColumnIndexOrThrow("bus_route_number")));
                    vo.setBusRouteDestination(cursor.getString(cursor.getColumnIndexOrThrow("bus_route_destination")));
                    vo.setCreateTime(createTime);
                    vo.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));

                    localCal.setTimeInMillis(createTime);

                    editMode = true;
                } else {
                    throw new IndexOutOfBoundsException(getString(R.string.add_error_entry_not_exist));
                }

                cursor.close();
            } catch (Exception e) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.add_error)
                        .setMessage(e.getMessage())
                        .setCancelable(true)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            dialog.dismiss();
                            getOnBackPressedDispatcher().onBackPressed();
                        });
                builder.show();
            }
        }

        initComponents();
        initComponentAdapters();
        initComponentContents();
        initComponentActions();
    }

    private void initComponents() {
        toolbar = findViewById(R.id.add_toolbar);
        setSupportActionBar(toolbar);
        if (editMode) toolbar.setTitle(R.string.add_edit);

        tiLayoutBusNumber = findViewById(R.id.add_ti_layout_bus_number);
        tiLayoutBusModel = findViewById(R.id.add_ti_layout_bus_model);
        tiLayoutBusRouteNumber = findViewById(R.id.add_ti_layout_bus_route_number);
        tiLayoutBusRouteDestination = findViewById(R.id.add_ti_layout_bus_route_destination);
        tiLayoutDate = findViewById(R.id.add_ti_layout_date);
        tiLayoutTime = findViewById(R.id.add_ti_layout_time);
        tiLayoutNote = findViewById(R.id.add_ti_layout_note);

        tiEditTextBusNumber = findViewById(R.id.add_ti_edittext_bus_number);
        tiEditTextBusModel = findViewById(R.id.add_ti_edittext_bus_model);
        tiEditTextBusRouteNumber = findViewById(R.id.add_ti_edittext_bus_route_number);
        tiEditTextBusRouteDestination = findViewById(R.id.add_ti_edittext_bus_destination);
        tiEditTextDate = findViewById(R.id.add_ti_edittext_date);
        tiEditTextTime = findViewById(R.id.add_ti_edittext_time);
        tiEditTextNote = findViewById(R.id.add_ti_edittext_note);

        buttonSubmit = findViewById(R.id.add_button_submit);
    }

    private void initComponentAdapters() {
        ArrayAdapter<String> adapterBusNumber = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                getHistoryEntries("bus_number")
        );
        tiEditTextBusNumber.setAdapter(adapterBusNumber);
        tiEditTextBusNumber.setThreshold(1);

        ArrayAdapter<String> adapterBusModel = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                getHistoryEntries("bus_model")
        );
        tiEditTextBusModel.setAdapter(adapterBusModel);
        tiEditTextBusModel.setThreshold(1);

        ArrayAdapter<String> adapterBusRouteNumber = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                getHistoryEntries("bus_route_number")
        );
        tiEditTextBusRouteNumber.setAdapter(adapterBusRouteNumber);
        tiEditTextBusRouteNumber.setThreshold(1);

        ArrayAdapter<String> adapterBusRouteDestination = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                getHistoryEntries("bus_route_destination")
        );
        tiEditTextBusRouteDestination.setAdapter(adapterBusRouteDestination);
        tiEditTextBusRouteDestination.setThreshold(1);
    }

    private void initComponentContents() {
        if (vo == null) {
            tiEditTextDate.setText(DateFormat.getDateInstance()
                    .format(utcCal.getTime()));
            tiEditTextTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT)
                    .format(localCal.getTime()));
            return;
        }

        tiEditTextBusNumber.setText(vo.getBusNumber());
        tiEditTextBusModel.setText(vo.getBusModel());
        tiEditTextBusRouteNumber.setText(vo.getBusRouteNumber());
        tiEditTextBusRouteDestination.setText(vo.getBusRouteDestination());
        tiEditTextDate.setText(DateFormat.getDateInstance()
                .format(vo.getCreateTime()));
        tiEditTextTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT)
                .format(vo.getCreateTime()));
        tiEditTextNote.setText(vo.getNote());
    }

    private void initComponentActions() {
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        buttonSubmit.setOnClickListener(v -> {
            buttonSubmit.setEnabled(false);
            if (submit()) getOnBackPressedDispatcher().onBackPressed();
        });

        initDateTimeSelection();
    }

    private void initDateTimeSelection() {
        // DateFormat.getDateInstance().format(myDate);
        long today = MaterialDatePicker.todayInUtcMilliseconds();

        // 1. 获取系统 12/24 小时制设置
        boolean is24HourLayout = android.text.format.DateFormat.is24HourFormat(this);

        // 2. 根据设置配置 TimeFormat
        int clockFormat = is24HourLayout ? TimeFormat.CLOCK_24H : TimeFormat.CLOCK_12H;

        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.before(today))
                .build();

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setSelection(today)
                .setCalendarConstraints(constraints)
                .setTitleText(R.string.add_input_create_date)
                .build();

        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(clockFormat)
                .setInputMode(INPUT_MODE_CLOCK)
                .setHour(localCal.get(Calendar.HOUR_OF_DAY))
                .setMinute(localCal.get(Calendar.MINUTE))
                .setTitleText(R.string.add_input_create_time)
                .build();

        tiEditTextDate.setOnClickListener(v ->
                datePicker.show(getSupportFragmentManager(), "DATE_PICKER"));

        tiEditTextTime.setOnClickListener(v ->
                timePicker.show(getSupportFragmentManager(), "TIME_PICKER"));

        datePicker.addOnPositiveButtonClickListener(selection -> {
            utcCal.setTimeInMillis(selection);

            tiEditTextDate.setText(DateFormat.getDateInstance()
                    .format(utcCal.getTime()));
        });

        timePicker.addOnPositiveButtonClickListener(v -> {
            localCal.set(utcCal.get(Calendar.YEAR),
                    utcCal.get(Calendar.MONTH),
                    utcCal.get(Calendar.DAY_OF_MONTH),
                    timePicker.getHour(),
                    timePicker.getMinute(),
                    0
            );

            tiEditTextTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT)
                    .format(localCal.getTime()));
        });
    }

    private boolean preSubmitCheck() {
        return !(isBlank(tiLayoutBusNumber, tiEditTextBusNumber) ||
                isBlank(tiLayoutBusModel, tiEditTextBusModel) ||
                isBlank(tiLayoutBusRouteNumber, tiEditTextBusRouteNumber) ||
                isBlank(tiLayoutBusRouteDestination, tiEditTextBusRouteDestination) ||
                isBlank(tiLayoutDate, tiEditTextDate) ||
                isBlank(tiLayoutTime, tiEditTextTime)
        );
    }

    private boolean submit() {
        if (!preSubmitCheck()) return false;

        ContentValues values = new ContentValues();
        values.put("bus_number", getText(tiEditTextBusNumber));
        values.put("bus_model", getText(tiEditTextBusModel));
        values.put("bus_route_number", getText(tiEditTextBusRouteNumber));
        values.put("bus_route_destination", getText(tiEditTextBusRouteDestination));
        values.put("create_time", localCal.getTimeInMillis());
        Log.d(TAG, "submit: create_time = " + localCal.getTimeInMillis());
        values.put("note", getText(tiEditTextNote));

        if (editMode) {
            int count = db.update("logs", values,
                    "id = ?", new String[]{String.valueOf(vo.getId())});
            return count == 1;
        } else {
            long newRowId = db.insert("logs", null, values);
            Log.d(TAG, "submit: new row id = " + newRowId);
            return newRowId > -1;
        }
    }

    private String getText(EditText editText) {
        return Objects.requireNonNull(editText.getText()).toString();
    }

    private boolean isBlank(TextInputLayout layout, EditText editText) {
        if (getText(editText).isBlank()) {
            layout.setError(getText(R.string.input_required));
            return true;
        }
        return false;
    }

    private List<String> getHistoryEntries(String columnName) {
        List<String> suggestions = new ArrayList<>();

        Cursor cursor = db.query(
                "logs",
                new String[]{columnName},
                null,
                null,
                columnName,
                null,
                "COUNT(" + columnName + ") DESC, MAX(create_time) DESC",
                "5"
        );

        while (cursor.moveToNext()) {
            suggestions.add(cursor.getString(
                    cursor.getColumnIndexOrThrow(columnName)
            ));
        }
        cursor.close();

        return suggestions;
    }
}