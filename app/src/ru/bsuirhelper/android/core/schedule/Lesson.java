package ru.bsuirhelper.android.core.schedule;

import java.util.HashMap;

public class Lesson {
    public final HashMap<String, String> fields = new HashMap<String, String>();
    public int id;

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
}
