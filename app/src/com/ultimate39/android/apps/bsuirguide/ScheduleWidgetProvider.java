package com.ultimate39.android.apps.bsuirguide;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;
import org.joda.time.DateTime;

/**
 * Created by Влад on 14.10.13.
 */
public class ScheduleWidgetProvider extends AppWidgetProvider {
    final String LOG_TAG = MainActivity.LOG_TAG;

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
                    rv.setTextViewText(R.id.widget_textView, "Занятий нет");
                }
            }


            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void setOnClickWidget(Context context, RemoteViews rv, int appWidgetId) {
        Intent startMainActivity = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, startMainActivity, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickPendingIntent(R.id.widget_schedule, pendingIntent);
    }


}
