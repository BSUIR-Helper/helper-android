package ru.bsuirhelper.android.ui.schedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.DatePicker;
import android.widget.Toast;
import com.google.analytics.tracking.android.EasyTracker;
import org.joda.time.DateTime;
import ru.bsuirhelper.android.ApplicationSettings;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.StudentCalendar;
import ru.bsuirhelper.android.ui.DownloadScheduleTask;
import ru.bsuirhelper.android.ui.RotationViewPager;

public class FragmentSchedule extends Fragment implements DownloadScheduleTask.CallBack {
    public static final String LOG_TAG = "BSUIR_DEBUG";
    private ViewPager mPager;
    private String mGroupId;
    private StudentCalendar mStudentCalendar;
    private ApplicationSettings mSettings;
    private ActionBar mActionBar;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        mSettings = ApplicationSettings.getInstance(getActivity().getApplicationContext());
        mStudentCalendar = new StudentCalendar();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(true);
        View fragmentContent = inflater.inflate(R.layout.activity_schedule, container, false);
        mPager = (ViewPager) fragmentContent.findViewById(R.id.schedule_pager);

        if (Build.VERSION.SDK_INT > 10) {
            mPager.setPageTransformer(true, new RotationViewPager());
        }

        try {
            mGroupId = (String) getArguments().get("groupId");
        } catch (NullPointerException ex) {
        }


        if (mGroupId == null) {
            mGroupId = mSettings.getString("defaultgroup", null);
            //If get null, it's mean not set default group
            if (mGroupId == null) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.content_frame, new FragmentManagerGroups()).commit();
            }
        }

        int subgroup = mSettings.getInt(mGroupId, 1);
        SchedulePagerAdapter adapter = new SchedulePagerAdapter(getActivity().getSupportFragmentManager(), mGroupId, subgroup);
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
        mPager.setCurrentItem(mStudentCalendar.getDayOfYear() - 1);
        return fragmentContent;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (ApplicationSettings.getInstance(getActivity().getApplicationContext()).getBoolean("firststart", true)) {
            showDialogSubjectTypeHelper();
            ApplicationSettings.getInstance(getActivity().getApplicationContext()).putBoolean("firststart", false);
        }
        EasyTracker.getInstance(getActivity().getApplicationContext()).activityStart(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(getActivity().getApplicationContext()).activityStop(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private MenuItem mSubgroup1;
    private MenuItem mSubgroup2;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_schedule_activity_actions, menu);
        mSubgroup1 = menu.findItem(R.id.subgroup1);
        mSubgroup2 = menu.findItem(R.id.subgroup2);
        int subgroup = mSettings.getInt(mGroupId, 1);

        if (subgroup == 1) {
            mSubgroup1.setChecked(true);
        } else {
            mSubgroup2.setChecked(true);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_updateschedule:
                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                if (!isConnected) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle("Информация")
                            .setMessage("Интернет соединение отсуствует, проверьте подключение к интернету");
                    alert.show();
                } else {
                    DownloadScheduleTask downloadScheduleTask = new DownloadScheduleTask(this);
                    downloadScheduleTask.execute(mGroupId);
                }
                return true;
            case R.id.action_selecttoday:
                DialogDatePicker newFragment = new DialogDatePicker() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        mPager.setCurrentItem(mStudentCalendar.getDayOfYear(new DateTime(year, month + 1, day, 1, 1)));
                    }
                };
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
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
            case R.id.action_help:
                showDialogSubjectTypeHelper();
                return true;
            case R.id.action_addlesson:
                startActivity(new Intent(getActivity(), ActivityEditLesson.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showDialogSubjectTypeHelper() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.dialog_sybject_type_helper, null));
        builder.setTitle("Типы пар");
        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    void refreshSchedule(int subgroup) {
        SchedulePagerAdapter adapter = new SchedulePagerAdapter(getActivity().getSupportFragmentManager(), mGroupId, subgroup);
        int position = mPager.getCurrentItem();
        mPager.setAdapter(adapter);
        mPager.setCurrentItem(position);
    }


    @Override
    public void onPostExecute() {
        Toast.makeText(getActivity(), "Расписание обновлено", Toast.LENGTH_SHORT).show();
    }
}
