package com.ultimate39.android.apps.bsuirguide;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Влад on 20.10.13.
 */
class DownloaderTaskFragment extends Fragment {
    private final String mProgressDialogMessage;
    private ProgressDialog mPogressDialog;
    private boolean mRunning = false;
    private TaskCallbacks mCallbacks;
    private DownloadScheduleTask mTask;

    static interface TaskCallbacks {
        void onPreExecute();

        void onProgressUpdate(int percent);

        void onCancelled();

        void onPostExecute(String result);
    }

    public DownloaderTaskFragment(String progressDialogMessage) {
        mProgressDialogMessage = progressDialogMessage;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (TaskCallbacks) activity;
        if (mRunning) {
            mPogressDialog = new ProgressDialog(getActivity());
            mPogressDialog.setMessage(mProgressDialogMessage);
            mPogressDialog.show();
        }
        Log.d(MainActivity.LOG_TAG, "OnAttach:" + activity);
    }

    public void start(String groupId) {
        if (!mRunning) {
            mTask = new DownloadScheduleTask();
            mTask.execute(groupId);
            mRunning = true;
            showProgressDialog();
        }
    }

    /**
     * Cancel the background task.
     */
    public void cancel() {
        if (mRunning) {
            mTask.cancel(false);
            mTask = null;
            mRunning = false;
        }
    }

    private void showProgressDialog() {
        mPogressDialog = new ProgressDialog(getActivity());
        mPogressDialog.setMessage(mProgressDialogMessage);
        mPogressDialog.setCancelable(false);
        mPogressDialog.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private class DownloadScheduleTask extends AsyncTask<String, Integer, String> {
        final ScheduleManager mScheduleManager = new ScheduleManager(getActivity());

        private File downloadScheduleFromInternet(String groupId) {
            final String LIST_URL = "http://www.bsuir.by/psched/rest/";
            final String TEMP_FILE_NAME = "schedule.xml";
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(LIST_URL + groupId);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                // download the file
                input = connection.getInputStream();
                output = getActivity().openFileOutput(TEMP_FILE_NAME, Context.MODE_PRIVATE);

                byte data[] = new byte[4096];
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (connection != null)
                    connection.disconnect();
            }
            return new File(getActivity().getFilesDir() + "/" + TEMP_FILE_NAME);
        }

        @Override
        protected void onPreExecute() {
            if (mCallbacks != null) {
                mCallbacks.onPreExecute();
            }
        }

        @Override
        protected String doInBackground(String... urls) {
            String groupId = urls[0];
            //DOWNLOAD SCHEDULE
            File xmlFile = downloadScheduleFromInternet(groupId);
            if (xmlFile == null) {
                return "Error";
            }
            //PARSE SCHEDULE
            ArrayList<Lesson> lessons = ScheduleParser.parseXmlSchedule(xmlFile);
            //ADD FILE TO DATABASE
            mScheduleManager.addSchedule(groupId, lessons);
            return "Success";
        }

        @Override
        protected void onProgressUpdate(Integer... percent) {
            if (mCallbacks != null) {
                mCallbacks.onProgressUpdate(percent[0]);
            }
        }

        @Override
        protected void onCancelled() {
            if (mCallbacks != null) {
                mCallbacks.onCancelled();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (mCallbacks != null) {
                mCallbacks.onPostExecute(result);
            }
            if (mPogressDialog.isShowing()) {
                mPogressDialog.dismiss();
            }
            mRunning = false;
        }
    }
}
