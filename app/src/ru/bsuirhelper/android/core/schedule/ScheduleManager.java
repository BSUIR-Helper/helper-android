package ru.bsuirhelper.android.core.schedule;

import android.content.Context;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import ru.bsuirhelper.android.core.StudentCalendar;

import java.util.ArrayList;

/**
 * Created by Влад on 12.09.13.
 */
public class ScheduleManager {
    private final ScheduleDatabase mScheduleDatabase;
    private StudentCalendar mStudentCalendar = new StudentCalendar();
    private static ScheduleManager instance;

    public static ScheduleManager getInstance(Context context) {
        if (instance == null) {
            instance = new ScheduleManager(context);
        }
        return instance;
    }

    public ScheduleManager(Context context) {
        mScheduleDatabase = new ScheduleDatabase(context);
        mStudentCalendar = new StudentCalendar();
    }

    public ArrayList<StudentGroup> getGroups() {
        return mScheduleDatabase.getGroups();
    }

    public void addSchedule(String groupId, ArrayList<Lesson> lessons) {
        mScheduleDatabase.addSchedule(lessons, groupId);
    }

    public Lesson[] getLessonsOfDay(String groupId, DateTime dayOfYear, int subgroup) {
        return mScheduleDatabase.getLessonsOfDay(groupId, dayOfYear, subgroup);
    }

    public void deleteSchedule(String groupId) {
        mScheduleDatabase.deleteSchedule(groupId);
    }

    public boolean isLessonsEndToday(String groupId, int subgroup) {
        DateTime currentTime = new DateTime();
        Lesson[] lessons = mScheduleDatabase.getLessonsOfDay(groupId, DateTime.now(), subgroup);
        if (lessons.length > 0) {
            Lesson lesson = lessons[lessons.length - 1];

            DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
        DateTime dt = formatter.parseDateTime(getFinishTimeOfLesson(lesson));
        if (currentTime.getHourOfDay() + 1 > dt.getHourOfDay()) {
            return true;
        }
        } else {
            return true;
        }
        return false;
    }

    private String getFinishTimeOfLesson(Lesson lesson) {
        String time = lesson.fields.get("timePeriod");
        char c = '-';
        int pos = -1;
        while (time.charAt(++pos) != c) ;
        String result = time.substring(pos + 1, time.length());
        return result;
    }
}
