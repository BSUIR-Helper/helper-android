package ru.bsuirhelper.android.app.ui.fragment;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.app.core.StudentCalendar;
import ru.bsuirhelper.android.app.db.DatabaseHelper;
import ru.bsuirhelper.android.app.db.entities.Employee;
import ru.bsuirhelper.android.app.db.entities.ScheduleLesson;
import ru.bsuirhelper.android.app.ui.adapter.DaysAdapter;
import ru.bsuirhelper.android.app.ui.adapter.ScheduleOfDayAdapter;
import ru.bsuirhelper.android.app.ui.fragment.base.BaseFragment;
import ru.bsuirhelper.android.app.ui.fragment.base.FragmentInfo;
import ru.bsuirhelper.android.app.ui.views.ScheduleDatePicker;

/**
 * Created by Grishechko on 01.02.2016.
 */
public class FragmentSchedule extends BaseFragment{

    @Bind(R.id.vp_schedule) ViewPager vpSchedule;
    @Bind(R.id.vp_days) ViewPager vpDays;
    @Bind(R.id.tv_month_and_year) TextView tvMonthAndYear;
    @Bind(R.id.toolbar_schedule) Toolbar mToolbar;
    @Bind(R.id.app_bar_layout) AppBarLayout appBar;
    private DateTime dateTime = new DateTime();
    private DaysAdapter mDaysAdapter;

    @Inject
    DatabaseHelper dbHelper;

    public static FragmentSchedule newInstance(int studentGroupId) {
        Bundle args = new Bundle();
        FragmentSchedule fragment = new FragmentSchedule();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FragmentInfo getFragmentInfo() {
        return new FragmentInfo(R.layout.fragment_schedule);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToolBarController.hideToolbar(false);
        vpSchedule.setAdapter(new ScheduleOfDayAdapter(generateTestData()));
        vpSchedule.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mDaysAdapter = new DaysAdapter(vpDays);
        vpDays.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                dateTime = dateTime.withWeekOfWeekyear(position + 1).withDayOfWeek(DateTimeConstants.MONDAY);
                tvMonthAndYear.setText(mDaysAdapter.getFormattedDate(getResources(), position));
                //TODO remove fake group
                mToolbar.setTitle(String.format("%s, %d%s", "313801", mDaysAdapter.getWorkWeek(position), getString(R.string.work_week_postfix)));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vpDays.setAdapter(mDaysAdapter);
        changeToolbarSize();
        if (isFragmentAlive()) {
            activity().setUpActionBar(mToolbar);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_schedule, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_selecttoday:
                showDayPickerDialog();
                return true;
        }
        return true;
    }

    private void showDayPickerDialog() {
        if(isFragmentAlive()) {
            DateTime now = DateTime.now();
            new ScheduleDatePicker(activity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mDaysAdapter.setDay(new DateTime(year, monthOfYear + 1, dayOfMonth, 1, 1, 1));
                }
            }, now.getYear(), now.getMonthOfYear() - 1, now.getDayOfMonth()).show();
        }
    }

    private void changeToolbarSize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            appBar.setPadding(0, getStatusBarHeight(), 0, 0);
        }
    }

    @Override
    public void inject() {
        super.inject();
    }

    private List<ScheduleLesson> generateTestData() {
        List<ScheduleLesson> scheduleLessons = new ArrayList<>();
        Employee employee = new Employee(1, null, "Владислав", "Гришечко", "Андреевич", "профессор");
        for (int pos = 0; pos < 6; pos++) {
            if (pos > 4) {
                scheduleLessons.add(new ScheduleLesson("321-3к", employee, "11:00 - 12:00", "лр", 1, "313801", "ОАиП", null, false));
            } else if (pos > 2) {
                scheduleLessons.add(new ScheduleLesson("321-3к", employee, "11:00 - 12:00", "пз", 1, "313801", "ОАиП", null, false));
            } else {
                scheduleLessons.add(new ScheduleLesson("321-3к", employee, "11:00 - 12:00", "лк", 1, "313801", "ОАиП", null, false));
            }
        }
        return scheduleLessons;
    }


    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


}
