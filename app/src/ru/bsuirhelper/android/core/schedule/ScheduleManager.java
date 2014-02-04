package ru.bsuirhelper.android.core.schedule;

import android.content.Context;
import org.joda.time.DateTime;
import ru.bsuirhelper.android.core.StudentCalendar;

import java.util.ArrayList;

/**
 * Created by Влад on 12.09.13.
 */
public class ScheduleManager {
    private final ScheduleDatabase mScheduleDatabase;
    private StudentCalendar mStudentCalendar = new StudentCalendar();

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

}
