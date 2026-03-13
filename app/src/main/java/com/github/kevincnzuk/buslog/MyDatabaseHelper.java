package com.github.kevincnzuk.buslog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    private static final String CREATE_TABLE = "CREATE TABLE logs (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  bus_number TEXT," +
            "  bus_model TEXT," +
            "  bus_route_number TEXT," +
            "  bus_route_destination TEXT," +
            "  create_time INTEGER," +
            "  note TEXT" +
            ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
