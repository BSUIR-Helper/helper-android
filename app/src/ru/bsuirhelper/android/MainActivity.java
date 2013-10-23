package ru.bsuirhelper.android.bsuirhelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements DownloaderTaskFragment.TaskCallbacks {
    public static final String LOG_TAG = "BSUIR_DEBUG";
    public static final String EDIT_PREFS = "settings.txt";
    private ViewPager mPager;
    private ActionBar mActionBar;
    private String mGroupId;
    private StudentCalendar mStudentCalendar;
    private ScheduleManager mScheduleManager;
    private ApplicationSettings mSettings;
    private DownloaderTaskFragment mDownloaderTaskFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        mDownloaderTaskFragment = (DownloaderTaskFragment) fragmentManager.findFragmentByTag("downloader");

        if (mDownloaderTaskFragment == null) {
            mDownloaderTaskFragment = new DownloaderTaskFragment("Обновление расписания");
            fragmentManager.beginTransaction().add(mDownloaderTaskFragment, "downloader").commit();
        }

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


    private MenuItem mSubgroup1;
    private MenuItem mSubgroup2;

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
                    mDownloaderTaskFragment.start(mGroupId);
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

    void refreshSchedule(int subgroup) {
        SchedulePagerAdapter adapter = new SchedulePagerAdapter(getSupportFragmentManager(), mGroupId, 1);
        int position = mPager.getCurrentItem();
        mPager.setAdapter(new SchedulePagerAdapter(getSupportFragmentManager(), "313801", subgroup));
        mPager.setAdapter(adapter);
        mPager.setCurrentItem(position);
    }

    /* **********************************
     * Downloader Task callback methods
     * **********************************
     */

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onProgressUpdate(int percent) {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute(String result) {
        if (result.equals("Error")) {

            Toast.makeText(this, "Произошла ошибка", Toast.LENGTH_LONG).show();
        } else {
            refreshSchedule(mSettings.getInt(mGroupId, 1));
            Toast.makeText(this, "Расписание обновлено", Toast.LENGTH_SHORT).show();
        }
    }


}
