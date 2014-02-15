package ru.bsuirhelper.android.ui.schedule;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.Toast;
import com.google.analytics.tracking.android.EasyTracker;
import org.joda.time.DateTime;
import ru.bsuirhelper.android.ApplicationSettings;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.StudentCalendar;
import ru.bsuirhelper.android.ui.ActivityDrawerMenu;
import ru.bsuirhelper.android.ui.DownloaderTaskFragment;
import ru.bsuirhelper.android.ui.RotationViewPager;

public class ActivitySchedule extends ActivityDrawerMenu implements DownloaderTaskFragment.TaskCallbacks {
    public static final String LOG_TAG = "BSUIR_DEBUG";
    public static final String EDIT_PREFS = "settings.txt";
    private ViewPager mPager;
    private ActionBar mActionBar;
    private String mGroupId;
    private StudentCalendar mStudentCalendar;
    private ApplicationSettings mSettings;
    private DownloaderTaskFragment mDownloaderTaskFragment;

    Bundle savedInstanceState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_schedule);
        //Get fragment for downloader task
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        mDownloaderTaskFragment = (DownloaderTaskFragment) fragmentManager.findFragmentByTag("downloader");
        if (mDownloaderTaskFragment == null) {
            mDownloaderTaskFragment = new DownloaderTaskFragment();
            mDownloaderTaskFragment.setMessage("Обновление расписания");
            fragmentManager.beginTransaction().add(mDownloaderTaskFragment, "downloader").commit();
        }

        mSettings = ApplicationSettings.getInstance(this);
        mStudentCalendar = new StudentCalendar();
        mPager = (ViewPager) findViewById(R.id.schedule_pager);
        if (Build.VERSION.SDK_INT > 10) {
            mPager.setPageTransformer(true, new RotationViewPager());
        }
        mActionBar = getSupportActionBar();

        //Get value if intent from activity - ScheduleManagerGroups
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
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                mActionBar.setSubtitle("уч.неделя " + mStudentCalendar.getWorkWeek(StudentCalendar.convertToDefaultDateTime(i)));
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mActionBar.setTitle("Группа " + mGroupId);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mPager.setCurrentItem(mStudentCalendar.getDayOfYear() - 1);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (ApplicationSettings.getInstance(this).getBoolean("firststart", true)) {
            showDialogSubjectTypeHelper();
            ApplicationSettings.getInstance(this).putBoolean("firststart", false);
        }
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private MenuItem mSubgroup1;
    private MenuItem mSubgroup2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_schedule_activity_actions, menu);
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
                DialogDatePicker newFragment = new DialogDatePicker() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        ActivitySchedule.this.mPager.setCurrentItem(mStudentCalendar.getDayOfYear(new DateTime(year, month + 1, day, 1, 1)));
                    }
                };
                newFragment.show(getSupportFragmentManager(), "timePicker");
                return true;
            case R.id.subgroup1:
                mSubgroup1.setChecked(true);
                mSettings.putInt(mGroupId, 1);
                refreshSchedule(1);
                mActionBar.setSubtitle("подгруппа 1");
                return true;
            case R.id.subgroup2:
                mSubgroup2.setChecked(true);
                mSettings.putInt(mGroupId, 2);
                refreshSchedule(2);
                mActionBar.setSubtitle("подгруппа 2");
                return true;
            /*
            case android.R.id.home:
                Intent homeintent = new Intent(this, ActivityManagerGroups.class);
                homeintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeintent);
                return true;
                */
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showDialogSubjectTypeHelper() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_sybject_type_helper, null));
        builder.setTitle("Типы занятий");
        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
        mPager.setCurrentItem(mStudentCalendar.getDayOfYear() - 1);
    }

    void refreshSchedule(int subgroup) {
        SchedulePagerAdapter adapter = new SchedulePagerAdapter(getSupportFragmentManager(), mGroupId, subgroup);
        int position = mPager.getCurrentItem();
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
