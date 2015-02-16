package ru.bsuirhelper.android.appwidget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import org.joda.time.DateTime;

import java.util.Arrays;

import ru.bsuirhelper.android.ApplicationSettings;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.StudentCalendar;
import ru.bsuirhelper.android.core.models.Lesson;
import ru.bsuirhelper.android.core.database.ScheduleManager;
import ru.bsuirhelper.android.ui.activity.ActivityDrawerMenu;

/**
 * Created by Влад on 04.02.14.
 */
public abstract class ScheduleWidgetProviderBase extends AppWidgetProvider {
    public static final String UPDATE_ACTION = "UDATE_ACTION";

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        if (!StudentCalendar.isHolidays()) {
            notifyRecreateListView(context);
        for (int i = 0; i < appWidgetIds.length; i++) {
            Intent intent = new Intent(context, ScheduleWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            String defaultGroup = ApplicationSettings.getInstance(context).getString("defaultgroup", null);
            int subgroup = 1;
            if (defaultGroup != null) {
                subgroup = ApplicationSettings.getInstance(context).getInt(defaultGroup, 1);
            }
            ScheduleManager scheduleManager = ScheduleManager.getInstance(context);
            setOnClickWidget(context, rv, i);
            if (defaultGroup == null) {
                rv.setViewVisibility(R.id.widget_textView, View.VISIBLE);
                rv.setTextViewText(R.id.widget_textView, context.getText(R.string.load_schedule));
            } else {
                DateTime lessonDay = DateTime.now();
                lessonDay = lessonDay.plusDays(1);
                Lesson[] lessons = scheduleManager.getLessonsOfDay(defaultGroup, lessonDay,
                        ApplicationSettings.getInstance(context).getInt(defaultGroup, subgroup));
                int color = getMostColor(lessons, context);
                if (scheduleManager.isLessonsEndToday(defaultGroup, subgroup)) {
                    rv.setTextViewText(R.id.widget_date, "Расписание на завтра");
                    rv.setInt(R.id.widget_date, "setBackgroundColor", color);
                } else {
                    rv.setTextViewText(R.id.widget_date, "Расписание на сегодня");
                    rv.setInt(R.id.widget_date, "setBackgroundColor", color);
                }


                if (lessons.length > 0) {
                    rv.setViewVisibility(R.id.widget_textView, View.INVISIBLE);
                    rv.setRemoteAdapter(R.id.widget_listView, intent);
                } else {
                    rv.setViewVisibility(R.id.widget_textView, View.VISIBLE);
                    rv.setTextViewText(R.id.widget_textView, "Занятий нет");
                }
            }

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setOnClickWidget(Context context, RemoteViews rv, int appWidgetId) {
        Intent startMainActivity = new Intent(context, ActivityDrawerMenu.class);
        startMainActivity.setData(Uri.parse(startMainActivity.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, startMainActivity, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickPendingIntent(R.id.widget_schedule, pendingIntent);
        rv.setPendingIntentTemplate(R.id.widget_listView, pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (action != null && action.equals(UPDATE_ACTION)) {
            final AppWidgetManager manager = AppWidgetManager.getInstance(context);
            int appWidgetIds[] = manager.getAppWidgetIds(
                    new ComponentName(context, this.getClass()));
            if (Build.VERSION.SDK_INT > 10) {
                onUpdate(context, manager, appWidgetIds);
            }
            Intent startActivity = new Intent(context, ActivityDrawerMenu.class);
            context.startActivity(startActivity);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void notifyRecreateListView(Context context) {
        final AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = manager.getAppWidgetIds(
                new ComponentName(context, getWidgetClass()));
        manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listView);
    }

    public void updateAppWidget(Context context) {
        Intent i = new Intent(context, ScheduleWidgetProviderBase.class);
        i.setAction(UPDATE_ACTION);
        context.sendBroadcast(i);
    }

    public static void updateAllWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIdsBig = appWidgetManager.getAppWidgetIds(new ComponentName(context, ScheduleWidgetProviderBig.class));
        if (appWidgetIdsBig.length > 0) {
            new ScheduleWidgetProviderBig().onUpdate(context, appWidgetManager, appWidgetIdsBig);
        }

        int[] appWidgetIdsMedium = appWidgetManager.getAppWidgetIds(new ComponentName(context, ScheduleWidgetProviderMedium.class));
        if (appWidgetIdsBig.length > 0) {
            new ScheduleWidgetProviderMedium().onUpdate(context, appWidgetManager, appWidgetIdsMedium);
        }
    }

    public int getMostColor(Lesson[] lessons, Context context) {
        int type[][] = new int[2][3];
        type[0][0] = context.getResources().getColor(R.color.red);
        type[0][1] = context.getResources().getColor(R.color.orange);
        type[0][2] = context.getResources().getColor(R.color.green);
        for (int i = 0; i < lessons.length; i++) {
            Lesson lesson =  lessons[i];
            String subjectType = lesson.fields.get("subjectType").toLowerCase();

            Log.d(ActivityDrawerMenu.LOG_TAG, subjectType);
            if (subjectType.equals("лк")) {
                type[1][2] += 1;
            } else if (subjectType.equals("лр")) {
                type[1][0] += 1;
            } else {
                type[1][1] += 1;
            }
        }

        int max = type[1][2];
        int color = type[0][2];
        for (int i = 0; i < 3; i++) {
            if (max < type[1][i]) {
                max = type[1][i];
                color = type[0][i];
            }
        }
        Log.d(ActivityDrawerMenu.LOG_TAG, Arrays.deepToString(type));
        return color;
    }

    abstract Class getWidgetClass();
}
