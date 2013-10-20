package com.ultimate39.android.apps.bsuirguide;

import android.content.Context;
import org.joda.time.*;

import java.util.*;

/**
 * Created by Влад on 12.09.13.
 */
public class ScheduleManager {
    private Context mContext;
    private ScheduleDatabase mScheduleDatabase;
    private ScheduleParser scheduleParser;
    private StudentCalendar studentCalendar;

    public ScheduleManager(Context context) {
        this.mContext = context;
        mScheduleDatabase = new ScheduleDatabase(context);
        scheduleParser = new ScheduleParser();
        studentCalendar = new StudentCalendar();
    }

    public ArrayList<StudentGroup> getGroups() {
        return mScheduleDatabase.getGroups();
    }

    public void addSchedule(String groupId, ArrayList<Lesson> lessons) {
        mScheduleDatabase.addSchedule(lessons, groupId);
    }

    /*
    private Lesson[] getAllLessons(String groupId){
        mScheduleDatabase.open();
        Cursor cursor = mScheduleDatabase.db.rawQuery("SELECT*FROM schedule_313801 WHERE (weekDay='пн') AND ((weekList LIKE '%%') OR (weekList LIKE '')) ORDER BY timePeriodStart",null);
        Lesson[] lessons = new Lesson[cursor.getCount()];
        while(cursor.moveToNext())  {
           Lesson lesson = new Lesson();
           lesson.setDataFromCursor(cursor);
           lessons[cursor.getPosition()] = lesson;
           lesson.consolePrint();
        }
        mScheduleDatabase.close();
        return lessons;
    }
    */

    public Lesson[] getLessonsOfDay(String groupId, DateTime dateTime, int subgroup) {
        return mScheduleDatabase.getLessonsOfDay(groupId, dateTime.getDayOfWeek(), studentCalendar.getWorkWeek(dateTime), subgroup);
    }

    public void deleteSchedule(String groupId) {
        mScheduleDatabase.deleteSchedule(groupId);
    }
    /*

     ScheduleAsyncTask need for downloading xml file in separate thread inside method of ScheduleManager

     */

}
