package ru.bsuirhelper.android.ui.listener;

import ru.bsuirhelper.android.ui.asynctask.DownloadScheduleTask;

/**
 * Created by vladislav on 4/20/15.
 */
public interface AsyncTaskListener {
    public void onPostExecute(DownloadScheduleTask.Status status);
}
