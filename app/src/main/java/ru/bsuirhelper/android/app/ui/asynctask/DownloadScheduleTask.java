package ru.bsuirhelper.android.app.ui.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.app.core.cache.ScheduleManager;
import ru.bsuirhelper.android.app.core.models.Lesson;
import ru.bsuirhelper.android.app.core.models.StudentGroup;
import ru.bsuirhelper.android.app.core.parser.ScheduleParser;
import ru.bsuirhelper.android.app.ui.listener.AsyncTaskListener;

/**
 * Created by Влад on 18.02.14.
 */
public class DownloadScheduleTask extends AsyncTask<String, Integer, DownloadScheduleTask.Status> {
    private ProgressDialog mPogressDialog;
    private Context context;
    private String message;
    AsyncTaskListener listener;

    public enum Status {
        OK, ERROR, NOT_EXISTS
    }

    public DownloadScheduleTask(Context context, AsyncTaskListener listener) {
        this.context = context;
        this.message = context.getString(R.string.updating_schedule);
        this.listener = listener;
    }

    @Override
    protected Status doInBackground(String... groupNumbers) {
       /* String groupNumber = groupNumbers[0];
        String schedule = BsuirApi.groupSchedule(groupNumber);
        if (schedule == null) {
            return Status.ERROR;
        }

        List<ScheduleLesson> lessons = null;
        try {
            lessons = ScheduleParser.parseXmlSchedule(schedule);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (lessons == null || lessons.size() == 0) {
            return Status.NOT_EXISTS;
        }
        ScheduleManager.replaceSchedule(context, new StudentGroup(-1, groupNumber, groupNumber), lessons);
        return Status.OK;*/
        return Status.OK;
    }

    @Override
    public void onPreExecute() {
        showProgressDialog();
    }

    //TODO Вынести в строковые ресурсы
    @Override
    public void onPostExecute(Status result) {
        listener.onPostExecute(result);
        try {
            if (mPogressDialog.isShowing()) mPogressDialog.cancel();
        } catch (Exception exception) {
        }
    }
    private void showProgressDialog() {
        mPogressDialog = new ProgressDialog(context);
        mPogressDialog.setMessage(message);
        mPogressDialog.setCancelable(false);
        mPogressDialog.show();
    }

    public void setPogressDialogMessage(String message) {
        this.message = message;
    }
}