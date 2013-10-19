package com.ultimate39.android.apps.bsuirguide;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {
    public static final String LOG_TAG = "BSUIR_DEBUG";
    public static final String EDIT_PREFS = "settings.txt";
    ViewPager mPager;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    ActionBar mActionBar;
    String mGroupId;
    StudentCalendar mStudentCalendar;
    ScheduleManager mScheduleManager;
    ApplicationSettings mSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        mSettings = ApplicationSettings.getInstance(this);
        mStudentCalendar = new StudentCalendar();
        mScheduleManager = new ScheduleManager(this);
        mPager = (ViewPager) findViewById(R.id.schedule_pager);
        mActionBar = getSupportActionBar();
        //Get value if intent get from activity - ScheduleManagerGroups
        Intent intent = getIntent();
        mGroupId = intent.getStringExtra("groupId");

        if (mGroupId == null) {
            mGroupId = mSettings.getString("defaultgroup", null);
            //If get null, it's mean not set default group
            if (mGroupId == null) {
                startActivity(new Intent(this, ActivityManagerGroups.class));
                finish();
            }
        } else {
            mSettings.putString("defaultgroup", mGroupId);
        }

        int subgroup = mSettings.getInt(mGroupId, 1);
        SchedulePagerAdapter adapter = new SchedulePagerAdapter(getSupportFragmentManager(), mGroupId, subgroup);
        mPager.setAdapter(adapter);
        mActionBar.setTitle("Группа " + mGroupId);
        mActionBar.setSubtitle("подгруппа " + subgroup);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        mPager.setCurrentItem(mStudentCalendar.getDayOfYear() - 1);

    }

    MenuItem mSubgroup1;
    MenuItem mSubgroup2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.schedule_activity_actions, menu);
        mSubgroup1 = menu.findItem(R.id.subgroup1);
        mSubgroup2 = menu.findItem(R.id.subgroup2);
        int subgroup = mSettings.getInt(mGroupId, 1);

        if (subgroup == 1) {
            mSubgroup1.setChecked(true);
        } else {
            mSubgroup2.setChecked(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_updateschedule:
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                if (!isConnected) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("Информация")
                            .setMessage("Интернет соединение отсуствует, проверьте подключение к интернету");
                    alert.show();
                } else {
                    new ScheduleAsyncTask(this, mScheduleManager).execute(mGroupId);
                }
                return true;
            case R.id.action_selecttoday:
                mPager.setCurrentItem(mStudentCalendar.getDayOfYear() - 1);
                return true;
            case R.id.subgroup1:
                mSubgroup1.setChecked(true);
                mSubgroup2.setChecked(false);
                mSettings.putInt(mGroupId, 1);
                refreshSchedule(1);
                mActionBar.setSubtitle("подгруппа 1");
                return true;
            case R.id.subgroup2:
                mSubgroup2.setChecked(true);
                mSubgroup1.setChecked(false);
                mSettings.putInt(mGroupId, 2);
                refreshSchedule(2);
                mActionBar.setSubtitle("подгруппа 2");
                return true;
            case android.R.id.home:
                Intent homeintent = new Intent(this, ActivityManagerGroups.class);
                homeintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeintent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void refreshSchedule(int subgroup) {
        SchedulePagerAdapter adapter = new SchedulePagerAdapter(getSupportFragmentManager(), mGroupId, 1);
        int position = mPager.getCurrentItem();
        mPager.setAdapter(new SchedulePagerAdapter(getSupportFragmentManager(), "313801", subgroup));
        mPager.setAdapter(adapter);
        mPager.setCurrentItem(position);
    }

    /*

    This class need for update current schedule of group

     */
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
            mProgressDialog.setMessage("Обновление расписания...");
            mScheduleManager = scheduleManager;
        }

        @Override
        protected String doInBackground(String... Urls) {
            String groupId = Urls[0];
            //DOWNLOAD SCHEDULE
            File xmlFile = downloadScheduleFromInternet(groupId);
            if (xmlFile == null) {
                return "Такой группы не существует";
            }
            //PARSE SCHEDULE
            ArrayList<Lesson> lessons = ScheduleParser.parseXmlSchedule(xmlFile);
            //ADD FILE TO DATABASE
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
            if (result.equals("Такой группы не существует")) {
                Toast.makeText(mContext, "Произошла ошибка", Toast.LENGTH_LONG).show();
            } else {
                refreshSchedule(mSettings.getInt(mGroupId, 1));
                Toast.makeText(mContext, "Расписание обновлено", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
