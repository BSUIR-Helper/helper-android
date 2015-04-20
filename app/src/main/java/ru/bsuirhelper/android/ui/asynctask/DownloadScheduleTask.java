package ru.bsuirhelper.android.ui.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.List;

import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.api.BsuirApi;
import ru.bsuirhelper.android.core.cache.ScheduleManager;
import ru.bsuirhelper.android.core.models.Lesson;
import ru.bsuirhelper.android.core.models.StudentGroup;
import ru.bsuirhelper.android.core.parser.ScheduleParser;
import ru.bsuirhelper.android.ui.listener.AsyncTaskListener;

/**
 * Created by Влад on 18.02.14.
 */
public class DownloadScheduleTask extends AsyncTask<String, Integer, String> {
    private ProgressDialog mPogressDialog;
    private Context context;
    private String message;
    AsyncTaskListener listener;


    public DownloadScheduleTask(Context context, AsyncTaskListener listener) {
        this.context = context;
        this.message = context.getString(R.string.updating_schedule);
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... groupNumbers) {
        String groupNumber = groupNumbers[0];
        String schedule = BsuirApi.groupSchedule(groupNumber);
        if (schedule == null) {
            return "Error";
        }

        List<Lesson> lessons = null;
        try {
            lessons = ScheduleParser.parseXmlSchedule(schedule);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ScheduleManager.replaceSchedule(context, new StudentGroup(-1, groupNumber, groupNumber), lessons);
        return "Success";
    }

    @Override
    public void onPreExecute() {
        showProgressDialog();
    }

    @Override
    public void onPostExecute(String result) {

        try {
            if (mPogressDialog.isShowing()) mPogressDialog.cancel();
        } catch (Exception exception) {
        }

        if (result.equals("Error")) {
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.error), Toast.LENGTH_LONG).show();
        } else {
            try {
                if(listener != null) {
                    listener.onPostExecute();
                }
            } catch (ClassCastException e) {
                System.out.print("Fragment must implement CallBack Interface" + e.getMessage());
            }
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