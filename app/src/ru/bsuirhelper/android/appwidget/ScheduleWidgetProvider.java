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
import android.view.View;
import android.widget.RemoteViews;
import org.joda.time.DateTime;
import ru.bsuirhelper.android.ApplicationSettings;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.schedule.Lesson;
import ru.bsuirhelper.android.core.schedule.ScheduleManager;
import ru.bsuirhelper.android.ui.schedule.ActivitySchedule;

/**
 * Created by Влад on 14.10.13.
 */
public class ScheduleWidgetProvider extends AppWidgetProvider {
    public static final String UPDATE_ACTION = "UDATE_ACTION";

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            Intent intent = new Intent(context, ScheduleWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            String defaultGroup = ApplicationSettings.getInstance(context).getString("defaultgroup", null);
            ScheduleManager manager = new ScheduleManager(context);
            setOnClickWidget(context, rv, i);

            if (defaultGroup == null) {
                rv.setViewVisibility(R.id.widget_textView, View.VISIBLE);
                rv.setTextViewText(R.id.widget_textView, "Загрузить расписание");
            } else {
                Lesson[] lessons = manager.getLessonsOfDay(defaultGroup, DateTime.now(),
                        ApplicationSettings.getInstance(context).getInt(defaultGroup, 1));
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
        notifyRecreateListView(context);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void setOnClickWidget(Context context, RemoteViews rv, int appWidgetId) {
        Intent startMainActivity = new Intent(context, ActivitySchedule.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, startMainActivity, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickPendingIntent(R.id.widget_schedule, pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(UPDATE_ACTION)) {
            final AppWidgetManager manager = AppWidgetManager.getInstance(context);
            int appWidgetIds[] = manager.getAppWidgetIds(
                    new ComponentName(context, ScheduleWidgetProvider.class));
            if (Build.VERSION.SDK_INT > 10) {
                onUpdate(context, manager, appWidgetIds);
            }
        } else {
            super.onReceive(context, intent);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void notifyRecreateListView(Context context) {
        final AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = manager.getAppWidgetIds(
                new ComponentName(context, ScheduleWidgetProvider.class));
        manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listView);
    }
}
