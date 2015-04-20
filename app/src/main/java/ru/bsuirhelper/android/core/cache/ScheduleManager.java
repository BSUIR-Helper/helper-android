package ru.bsuirhelper.android.core.cache;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.orhanobut.logger.Logger;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import ru.bsuirhelper.android.core.StudentCalendar;
import ru.bsuirhelper.android.core.models.Lesson;
import ru.bsuirhelper.android.core.models.StudentGroup;
import ru.bsuirhelper.android.core.models.Teacher;

/**
 * Created by Влад on 12.09.13.
 */
public class ScheduleManager {
    public static List<StudentGroup> getGroups(Context context) {
        List<StudentGroup> groups = new ArrayList<>();
        if (context != null) {
            Cursor cursor = context.getContentResolver().query(CacheContentProvider.STUDENTGROUP_URI, null, null, null, null);
            while (cursor.moveToNext()) {
                groups.add(CacheHelper.StudentGroups.fromCursor(cursor));
            }
            cursor.close();
        }
        return groups;
    }

    /**
     * Add or update schedule if student group exists
     */
    public static void replaceSchedule(Context context, StudentGroup studentGroup, List<Lesson> lessons) {
        if (context != null && lessons != null && lessons.size() > 0) {
            Cursor cursor = context.getContentResolver().query(CacheContentProvider.STUDENTGROUP_URI, null, CacheHelper.StudentGroups.NUMBER + " = " + studentGroup.getGroupNumber(), null, null);
            long groupId = -1;
            if(cursor != null && cursor.moveToNext()) {
                groupId = CacheHelper.StudentGroups.fromCursor(cursor).getId();
                clearSchedule(context, groupId);
            } else {
                Uri uri = context.getContentResolver().insert(CacheContentProvider.STUDENTGROUP_URI, CacheHelper.StudentGroups.toContentValues(studentGroup));
                if (uri != null) {
                    groupId = Long.parseLong(uri.getLastPathSegment());
                }
            }
            if(groupId == -1) {
                Logger.e(new UnknownError("groupId == -1"), "Some strange");
                return;
            }
            CacheHelper.Lessons.insertLessons(context, groupId, lessons);
            List<Teacher> teachers = new ArrayList<>();
            for(Lesson lesson : lessons) {
                teachers.add(lesson.getTeacher());
            }
            CacheHelper.Teachers.insertTeachers(context, teachers);
        }
    }

    public static int clearSchedule(Context context, long groupId) {
        if(context != null) {
            context.getContentResolver().delete(CacheContentProvider.LESSON_URI, CacheHelper.Lessons.STUDENT_GROUP_ID + " = " + groupId, null);
        }
        return 0;
    }

    public static List<Lesson> getLessonsOfDay(Context context, String studentGroupId, DateTime dayOfYear, int subgroup) {
        List<Lesson> lessons = null;
        if (context != null) {
            lessons = new ArrayList<>();
            int weekDay = dayOfYear.getDayOfWeek();
            int workWeek = StudentCalendar.getWorkWeek(dayOfYear);
            String query = "(" + CacheHelper.Lessons.WEEK_DAY + "=?) AND " +
                    "((" + CacheHelper.Lessons.WEEK_NUMBERS + " LIKE ?) OR (" + CacheHelper.Lessons.WEEK_NUMBERS + " LIKE '')) AND " +
                    "((" + CacheHelper.Lessons.SUBGROUP + " LIKE ?) OR (" + CacheHelper.Lessons.SUBGROUP + " LIKE '0')) AND " + CacheHelper.Lessons.STUDENT_GROUP_ID + " = ?";
            Cursor cursor = context.getContentResolver().query(CacheContentProvider.LESSON_URI, null, "(" + CacheHelper.Lessons.WEEK_DAY + "= ?) AND " +
                            "((" + CacheHelper.Lessons.WEEK_NUMBERS + " LIKE ?) OR (" + CacheHelper.Lessons.WEEK_NUMBERS + " LIKE '')) AND " +
                            "((" + CacheHelper.Lessons.SUBGROUP + " LIKE ?) OR (" + CacheHelper.Lessons.SUBGROUP + " LIKE '%0%')) AND " + CacheHelper.Lessons.STUDENT_GROUP_ID + " = ?",
                    new String[]{String.valueOf(weekDay), "%" + String.valueOf(workWeek) + "%", "%" + String.valueOf(subgroup) + "%", String.valueOf(studentGroupId)}, CacheHelper.Lessons.LESSON_TIME);
            while (cursor.moveToNext()) {
                lessons.add(CacheHelper.Lessons.fromCursor(cursor));
            }
            cursor.close();
        }
        return lessons;
    }

    public static void deleteSchedule(Context context, long studentGroupId) {
        if (context != null) {
            context.getContentResolver().delete(CacheContentProvider.LESSON_URI, CacheHelper.Lessons.STUDENT_GROUP_ID + " = " + studentGroupId, null);
            context.getContentResolver().delete(CacheContentProvider.STUDENTGROUP_URI, CacheHelper.StudentGroups._ID + " = " + studentGroupId, null);
        }
    }

    public static boolean isLessonsFinishedToday(Context context, String groupId, int subgroup) {
        DateTime currentTime = new DateTime();
        List<Lesson> lessons = getLessonsOfDay(context, groupId, DateTime.now(), subgroup);
        if (lessons.size() > 0) {
            Lesson lesson = lessons.get(lessons.size() - 1);
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

    private static String getFinishTimeOfLesson(Lesson lesson) {
        String time = lesson.getLessonTime();
        char c = '-';
        int pos = -1;
        while (time.charAt(++pos) != c) ;
        String result = time.substring(pos + 1, time.length());
        return result;
    }
}
