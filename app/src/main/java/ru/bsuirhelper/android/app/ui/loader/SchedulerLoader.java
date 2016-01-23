package ru.bsuirhelper.android.app.ui.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.orhanobut.logger.Logger;

import org.joda.time.DateTime;

import java.util.List;

import ru.bsuirhelper.android.app.core.cache.ScheduleManager;
import ru.bsuirhelper.android.app.core.models.Lesson;

/**
 * Created by vladislav on 4/22/15.
 */
public class SchedulerLoader extends AsyncTaskLoader<List<Lesson>> {
    private DateTime day;
    String studentGroupId;
    int subgroup;

    @Override
    public List<Lesson> loadInBackground() {
        List<Lesson> lessons = ScheduleManager.getLessonsOfDay(getContext(), studentGroupId, day, subgroup);
        Logger.e(lessons.size() + "");
        return lessons;
    }

    public SchedulerLoader(Context context, DateTime day, String studentGroupId, int subgroup){
        super(context);
        this.day = day;
        this.studentGroupId = studentGroupId;
        this.subgroup = subgroup;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
