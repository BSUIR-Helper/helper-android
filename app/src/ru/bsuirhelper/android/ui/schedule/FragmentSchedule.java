package ru.bsuirhelper.android.ui.schedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.*;
import android.widget.DatePicker;
import android.widget.Toast;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import org.joda.time.DateTime;
import ru.bsuirhelper.android.ApplicationSettings;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.StudentCalendar;
import ru.bsuirhelper.android.ui.ActivityDrawerMenu;
import ru.bsuirhelper.android.ui.DownloadScheduleTask;
import ru.bsuirhelper.android.ui.RotationViewPager;

public class FragmentSchedule extends Fragment implements DownloadScheduleTask.CallBack {

    private String title = "Fragment Schedule";
    private ViewPager mPager;
    private String mGroupId;
    private StudentCalendar mStudentCalendar;
    private ApplicationSettings mSettings;
    private ActionBar mActionBar;
    private Context context;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = ApplicationSettings.getInstance(getActivity().getApplicationContext());
        mStudentCalendar = new StudentCalendar();
        setHasOptionsMenu(true);
        mGroupId = getDefaultGroup();
    }

    private String getDefaultGroup() {
        String defaultGroup = null;
        try {
            defaultGroup = (String) getArguments().get("groupId");
        } catch (NullPointerException ex) {

        }

        if (defaultGroup == null) {
            defaultGroup = mSettings.getString("defaultgroup", null);
            //If get null, it's mean not set default group
            if (defaultGroup == null) {
                FragmentManager fm = getChildFragmentManager();
                fm.beginTransaction().replace(R.id.content_frame, new FragmentManagerGroups()).commit();
                mActionBar.setTitle(FragmentManagerGroups.TITLE);
                return null;
            }

        }
        return defaultGroup;
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
        int subgroup = mSettings.getInt(mGroupId, 1);
        SchedulePagerAdapter adapter = new SchedulePagerAdapter(getActivity(), getChildFragmentManager(), mGroupId, subgroup);
        mPager.setAdapter(adapter);

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                mActionBar.setSubtitle(getActivity().getString(R.string.ab_work_week) + " " + mStudentCalendar.getWorkWeek(StudentCalendar.convertToDefaultDateTime(i + 1)));
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        mActionBar.setTitle(getActivity().getString(R.string.group) + " " + mGroupId);
        if (DateTime.now().getMonthOfYear() >= 9 || DateTime.now().getMonthOfYear() <= 6) {
            mPager.setCurrentItem(mStudentCalendar.getDayOfYear());
        } else {
            mPager.setCurrentItem(mStudentCalendar.getDayOfYear(new DateTime(DateTime.now().getYear(), 9, 1, 1, 1)));
        }
        return fragmentContent;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (ApplicationSettings.getInstance(getActivity().getApplicationContext()).getBoolean("firststart", true)) {
            showDialogSubjectTypeHelper();
            ApplicationSettings.getInstance(getActivity().getApplicationContext()).putBoolean("firststart", false);
        }
        EasyTracker tracker = EasyTracker.getInstance(getActivity());
        tracker.set(Fields.SCREEN_NAME, "Окно расписания");
        tracker.send(MapBuilder.createAppView().build());
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
        mGroupId = getDefaultGroup();
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
                    Toast.makeText(getActivity(), getString(R.string.internet_is_not_available), Toast.LENGTH_SHORT).show();
                } else {
                    new DownloadScheduleTask(this).execute(mGroupId);
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
                mActionBar.setSubtitle(getResources().getStringArray(R.array.action_selectsubgroup_values)[0].toLowerCase());
                return true;
            case R.id.subgroup2:
                mSubgroup2.setChecked(true);
                mSettings.putInt(mGroupId, 2);
                refreshSchedule(2);
                mActionBar.setSubtitle(getResources().getStringArray(R.array.action_selectsubgroup_values)[1].toLowerCase());
                return true;
            case R.id.action_help:
                showDialogSubjectTypeHelper();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showDialogSubjectTypeHelper() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.dialog_sybject_type_helper, null));
        builder.setTitle(getString(R.string.types_of_lessons));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    void refreshSchedule(int subgroup) {
        SchedulePagerAdapter adapter = new SchedulePagerAdapter(getActivity(), getActivity().getSupportFragmentManager(), getDefaultGroup(), subgroup);
        int position = mPager.getCurrentItem();
        ((SchedulePagerAdapter) mPager.getAdapter()).changeGroup(mGroupId, subgroup);
        mPager.setAdapter(mPager.getAdapter());
        mPager.setCurrentItem(position);
    }

    @Override
    public void onPostExecute() {
        Toast.makeText(context, getString(R.string.schedule_is_updated), Toast.LENGTH_SHORT).show();
    }
}
