package ru.bsuirhelper.android.appwidget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import org.joda.time.DateTime;

import ru.bsuirhelper.android.ApplicationSettings;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.models.Lesson;
import ru.bsuirhelper.android.core.database.ScheduleManager;
import ru.bsuirhelper.android.ui.activity.ActivitySettings;

/**
 * Created by Влад on 15.10.13.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class ScheduleFactoryViews implements RemoteViewsService.RemoteViewsFactory {
    private final ScheduleManager mScheduleManager;
    private Lesson[] mLessons;
    private final Context mContext;
    private final Intent mIntent;
    private final int mWidgetId;
    private int mLessonCount;
    private final ApplicationSettings mSettings;

    public ScheduleFactoryViews(Context context, Intent intent) {
        mScheduleManager = ScheduleManager.getInstance(context);
        mIntent = intent;
        mWidgetId = mIntent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        mContext = context;
        mSettings = ApplicationSettings.getInstance(context);
    }

    @Override
    public void onCreate() {
        updateLessons();
    }

    @Override
    public void onDataSetChanged() {
        updateLessons();
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
        Lesson lesson = mLessons[position];
        rView.setTextViewText(R.id.lesson_time, lesson.fields.get("timePeriod"));
        rView.setTextViewText(R.id.lesson_teacher, lesson.fields.get("teacher"));

        if (!lesson.fields.get("auditorium").equals("")) {
            rView.setTextViewText(R.id.lesson_auditorium, lesson.fields.get("auditorium"));
        }

        String subjectType = lesson.fields.get("subjectType");

        if (!subjectType.equals("кч")) {
            boolean isShowSubjectTypes = PreferenceManager.getDefaultSharedPreferences(
                    mContext).getBoolean(ActivitySettings.KEY_SHOW_SUBJECTS_TYPE, false);
            if (isShowSubjectTypes && !subjectType.equals("")) {
                rView.setTextViewText(R.id.lesson_name, lesson.fields.get("subject"));
                rView.setViewVisibility(R.id.lesson_subject_type, View.VISIBLE);
                rView.setTextViewText(R.id.lesson_subject_type, " (" + subjectType + ")");
            } else {
                rView.setTextViewText(R.id.lesson_name, lesson.fields.get("subject"));
                rView.setViewVisibility(R.id.lesson_subject_type, View.GONE);
            }
        } else {
            rView.setTextViewText(R.id.lesson_name, "КЧ");
        }

        if (subjectType.toLowerCase().equals(mContext.getString(R.string.ab_lab_id))) {
            rView.setInt(R.id.lesson_type_color, "setBackgroundColor", mContext.getResources().getColor(R.color.red));
        } else if (subjectType.toLowerCase().equals(mContext.getString(R.string.ab_work_lesson_id))) {
            rView.setInt(R.id.lesson_type_color, "setBackgroundColor", mContext.getResources().getColor(R.color.orange));
        } else if (subjectType.toLowerCase().equals(mContext.getString(R.string.ab_lecture_id))) {
            rView.setInt(R.id.lesson_type_color, "setBackgroundColor", mContext.getResources().getColor(R.color.green));
        } else {
            rView.setInt(R.id.lesson_type_color, "setBackgroundColor", mContext.getResources().getColor(R.color.white));
        }

        //rView.setInt(R.id.separateline, "setBackgroundColor", Color.WHITE);
        Intent startMainActivity = new Intent();
        rView.setOnClickFillInIntent(R.id.widget_lesson_container, startMainActivity);
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

    public void updateLessons() {
        String groupId = mSettings.getString("defaultgroup", null);
        int subgroup = mSettings.getInt(groupId, 1);
        if (!mScheduleManager.isLessonsEndToday(groupId, subgroup)) {
            mLessons = mScheduleManager.getLessonsOfDay(groupId, DateTime.now(), subgroup);
        } else {
            mLessons = mScheduleManager.getLessonsOfDay(groupId, new DateTime().plusDays(1), subgroup);
        }
        mLessonCount = mLessons.length;
    }

}