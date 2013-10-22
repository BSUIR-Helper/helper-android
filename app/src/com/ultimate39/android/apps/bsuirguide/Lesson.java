package com.ultimate39.android.apps.bsuirguide;

import android.database.Cursor;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Влад on 14.09.13.
 */
public class Lesson {
    public final HashMap<String, String> fields = new HashMap<String, String>();

    public Lesson() {
        fields.put("faculty", "");
        fields.put("year", "");
        fields.put("course", "");
        fields.put("term", "");
        fields.put("stream", "");
        fields.put("s_group", "");
        fields.put("subgroup", "");
        fields.put("weekDay", "");
        fields.put("timePeriod", "");
        fields.put("weekList", "");
        fields.put("subject", "");
        fields.put("subjectType", "");
        fields.put("auditorium", "");
        fields.put("teacher", "");
        fields.put("date", "");
        fields.put("timePeriodStart", "");
        fields.put("timePeriodEnd", "");

    }

    public void consolePrint() {
        Log.d(MainActivity.LOG_TAG, "---------------------------------------");
        for (String key : fields.keySet()) {
            Log.d(MainActivity.LOG_TAG, key + ":" + fields.get(key));
        }
        Log.d(MainActivity.LOG_TAG, "---------------------------------------");
    }

    public void setDataFromCursor(Cursor cursor) {
        for (String key : this.fields.keySet()) {
            this.fields.put(key, cursor.getString(cursor.getColumnIndex(key)));
        }
    }
}
