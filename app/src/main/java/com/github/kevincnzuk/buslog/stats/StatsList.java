package com.github.kevincnzuk.buslog.stats;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.kevincnzuk.buslog.helper.MyDatabaseHelper;
import com.github.kevincnzuk.buslog.vo.StatsVO;

import java.util.ArrayList;
import java.util.List;

public class StatsList implements IStatsList {

    private Context context;
    private String columnName;

    public StatsList(Context context, String columnName) {
        this.context = context;
        this.columnName = columnName;
    }

    @Override
    public List<StatsVO> getStats() {
        SQLiteOpenHelper helper = new MyDatabaseHelper(context, "log.db", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor cursor = db.query(
                "logs",
                new String[]{columnName, "COUNT(" + columnName + ") AS frequency"},
                null,
                null,
                columnName,
                null,
                "frequency DESC, bus_number ASC"
        );

        List<StatsVO> voList = new ArrayList<>();

        while (cursor.moveToNext()) {
            String model = cursor.getString(cursor.getColumnIndexOrThrow(columnName));
            int count = cursor.getInt(cursor.getColumnIndexOrThrow("frequency"));

            voList.add(new StatsVO(model, count));
        }

        cursor.close();

        return voList;
    }
}
