package ru.bsuirhelper.android.bsuirhelper;

import android.content.Context;
import org.joda.time.DateTime;

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

    public Lesson[] getLessonsOfDay(String groupId, DateTime dateTime, int subgroup) {
        return mScheduleDatabase.getLessonsOfDay(groupId, dateTime.getDayOfWeek(), mStudentCalendar.getWorkWeek(dateTime), subgroup);
    }

    public void deleteSchedule(String groupId) {
        mScheduleDatabase.deleteSchedule(groupId);
    }

}
