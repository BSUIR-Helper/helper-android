package com.ultimate39.android.apps.bsuirguide;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Влад on 12.10.13.
 */
public class ActivityManagerGroups extends ActionBarActivity {
    ScheduleManager mScheduleManager;
    ListView mListGroups;
    TextView mTextViewNotification;
    final String PREFS_NAME = "preference";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managerschedule);
        mScheduleManager = new ScheduleManager(this);
        mTextViewNotification = (TextView) findViewById(R.id.textview_groupsarenotadded);
        mListGroups = (ListView) findViewById(R.id.listview_groups);
        refreshListGroup(mListGroups);
        mListGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                StudentGroup group = (StudentGroup) mListGroups.getAdapter().getItem(position);
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                intent.putExtra("groupId", group.groupId);
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.managergroups_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_startdeletegroups:
                Intent intent = new Intent(this, ActivityDeleteGroups.class);
                startActivity(intent);
                return true;
            case R.id.action_addgroup:
                DialogFragmentAddGroup dialog = new DialogFragmentAddGroup(mScheduleManager,
                        new ScheduleAsyncTask(this, mScheduleManager));
                dialog.show(getSupportFragmentManager(), "");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void refreshListGroup(ListView listGroups) {
        ArrayList<StudentGroup> alGroups = mScheduleManager.getGroups();
        StudentGroup[] groups = new StudentGroup[alGroups.size()];
        groups = alGroups.toArray(groups);
        GroupsViewAdapter vaGroups = new GroupsViewAdapter(this, groups, R.layout.view_group);
        if (vaGroups.getCount() != 0) {
            listGroups.setAdapter(vaGroups);
            mTextViewNotification.setVisibility(View.INVISIBLE);
        } else {
            mTextViewNotification.setVisibility(View.VISIBLE);
        }
    }

    class ScheduleAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog mProgressDialog;
        ScheduleManager mScheduleManager;
        private Context mContext;

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

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                //int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = mContext.openFileOutput(TEMP_FILE_NAME, Context.MODE_PRIVATE);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    total += count;
                    // publishing the progress....
                    output.write(data, 0, count);
                }

            } catch (Exception e) {
                e.printStackTrace();
                return null;
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
            return new File(mContext.getFilesDir() + "/" + TEMP_FILE_NAME);
        }

        public ScheduleAsyncTask(Context context, ScheduleManager scheduleManager) {
            this.mContext = context;
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Загрузка расписания...");
            mProgressDialog.setCancelable(false);
            mScheduleManager = scheduleManager;
        }

        @Override
        protected String doInBackground(String... Urls) {
            String groupId = Urls[0];
            //DOWNLOAD SCHEDULE
            File xmlFile = downloadScheduleFromInternet(groupId);
            if (xmlFile == null) {
                return "error";
            }
            //PARSE SCHEDULE
            ArrayList<Lesson> lessons = ScheduleParser.parseXmlSchedule(xmlFile);
            //ADD FILE TO DATABASE
            xmlFile.delete();
            mScheduleManager.addSchedule(groupId, lessons);
            return "Success";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressDialog.dismiss();
            if (result.equals("error")) {
                Toast.makeText(mContext, "Произошла ошибка", Toast.LENGTH_LONG).show();
            } else {
                refreshListGroup(mListGroups);
                Toast.makeText(mContext, "Расписание добавлено", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
