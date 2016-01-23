package ru.bsuirhelper.android.app.ui.listener;

import ru.bsuirhelper.android.app.ui.asynctask.DownloadScheduleTask;

/**
 * Created by vladislav on 4/20/15.
 */
public interface AsyncTaskListener {
    public void onPostExecute(DownloadScheduleTask.Status status);
}
