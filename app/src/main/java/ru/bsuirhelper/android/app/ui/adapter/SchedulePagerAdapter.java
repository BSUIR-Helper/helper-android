package ru.bsuirhelper.android.app.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.joda.time.DateTime;

import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.app.core.StudentCalendar;
import ru.bsuirhelper.android.app.ui.fragment.FragmentScheduleOfDay;

/**
 * Created by Влад on 10.10.13.
 */
public class SchedulePagerAdapter extends FragmentStatePagerAdapter {
    private final StudentCalendar mStudentCalendar;
    private String mGroupId;
    private int mSubgroup;
    private Context mContext;

    public SchedulePagerAdapter(Context context, FragmentManager fm, String groupId, int subgroup) {
        super(fm);
        mStudentCalendar = new StudentCalendar();
        mGroupId = groupId;
        mSubgroup = subgroup;
        mContext = context;
    }

    @Override
    public Fragment getItem(int i) {
        return FragmentScheduleOfDay.newInstance(i + 1, mGroupId, mSubgroup);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String dateTime = "";
        DateTime day = StudentCalendar.convertToDefaultDateTime(position + 1);
        String nameDayOfWeek;
        if (day.getDayOfYear() == DateTime.now().getDayOfYear()) {
            nameDayOfWeek = mContext.getString(R.string.today);
        } else {
            nameDayOfWeek = day.dayOfWeek().getAsText();
            char firstCharacter = Character.toUpperCase(nameDayOfWeek.charAt(0));
            nameDayOfWeek = firstCharacter + nameDayOfWeek.substring(1);

        }
        dateTime += nameDayOfWeek;
        return dateTime;
    }

    @Override
    public int getCount() {
        return mStudentCalendar.getDaysOfYear();
    }

    public void changeGroup(String groupId, int subgroup) {
        mGroupId = groupId;
        mSubgroup = subgroup;
    }
}
