package com.ultimate39.android.apps.bsuirguide;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import org.joda.time.DateTime;

/**
 * Created by Влад on 15.10.13.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ScheduleFactoryViews implements RemoteViewsService.RemoteViewsFactory {
    ScheduleManager mScheduleManager;
    Lesson[] mLessonsOfToday;
    Context mContext;
    Intent mIntent;
    int mWidgetId;
    int mLessonCount;
    SharedPreferences mSettings;

    public ScheduleFactoryViews(Context context, Intent intent) {
        mScheduleManager = new ScheduleManager(context);
        mIntent = intent;
        mWidgetId = mIntent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        mContext = context;
        mSettings = context.getSharedPreferences(MainActivity.EDIT_PREFS, 0);
    }

    @Override
    public void onCreate() {
        mLessonsOfToday = mScheduleManager.getLessonsOfDay(mSettings.getString("defaultgroup", null), DateTime.now(), 1);
        mLessonCount = mLessonsOfToday.length;

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mLessonCount;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rView = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_view_lesson);
        Lesson lesson = mLessonsOfToday[position];
        rView.setTextViewText(R.id.widget_lesson_name, lesson.fields.get("subject"));
        rView.setTextViewText(R.id.widget_lesson_time, lesson.fields.get("timePeriod"));
        rView.setTextViewText(R.id.widget_lesson_teacher, lesson.fields.get("teacher"));

        if (!lesson.fields.get("auditorium").equals("")) {
            rView.setTextViewText(R.id.widget_lesson_auditorium, "аудитория " + lesson.fields.get("auditorium"));
        }

        String lessonType = lesson.fields.get("subjectType");

        if (lessonType.equals("лр")) {
            rView.setInt(R.id.widget_lesson_type_color, "setBackgroundColor", (Color.parseColor("#FF4444")));
        } else if (lessonType.equals("пз")) {
            rView.setInt(R.id.widget_lesson_type_color, "setBackgroundColor", (Color.parseColor("#FFBB33")));
        } else if (lessonType.equals("лк")) {
            rView.setInt(R.id.widget_lesson_type_color, "setBackgroundColor", (Color.parseColor("#99CC00")));
        } else {
            rView.setInt(R.id.widget_lesson_type_color, "setBackgroundColor", (Color.WHITE));
        }

        rView.setInt(R.id.widget_separateline, "setBackgroundColor", Color.WHITE);

        return rView;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
