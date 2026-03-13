package com.github.kevincnzuk.buslog.stats;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.kevincnzuk.buslog.helper.MyDatabaseHelper;
import com.github.kevincnzuk.buslog.vo.StatsVO;

import java.util.ArrayList;
import java.util.List;

public class BusNumberStatsList implements StatsList {
    @Override
    public List<StatsVO> getStats(Context context) {
        SQLiteOpenHelper helper = new MyDatabaseHelper(context, "log.db", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();

        String columnName = "bus_number";

        Cursor cursor = db.query(
                "logs",
                new String[]{columnName, "COUNT(" + columnName + ") AS frequency"},
                null,
                null,
                columnName,
                null,
                "frequency DESC"
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
