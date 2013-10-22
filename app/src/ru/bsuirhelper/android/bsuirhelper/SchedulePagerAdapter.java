package ru.bsuirhelper.android.bsuirhelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import org.joda.time.DateTime;

/**
 * Created by Влад on 10.10.13.
 */
class SchedulePagerAdapter extends FragmentStatePagerAdapter {
    private final StudentCalendar mStudentCalendar;
    private final String mGroupId;
    private final int mSubgroup;

    public SchedulePagerAdapter(FragmentManager fm, String groupId, int subgroup) {
        super(fm);
        mStudentCalendar = new StudentCalendar();
        mGroupId = groupId;
        mSubgroup = subgroup;
    }

    @Override
    public Fragment getItem(int i) {
        Bundle args = new Bundle();
        args.putInt("day", i + 1);
        args.putString("groupId", mGroupId);
        args.putInt("subgroup", mSubgroup);
        Fragment fragment = new FragmentSchedule();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String dateTime = "";
        DateTime day = StudentCalendar.convertToDefaultDateTime(position + 1);
        String sDay;
        if (day.getDayOfYear() == DateTime.now().getDayOfYear()) {
            sDay = "Сегодня";
        } else {
            //UpperCase first character friday -> Friday
            sDay = day.dayOfWeek().getAsText();
            char firstCharacter = Character.toUpperCase(sDay.charAt(0));
            sDay = firstCharacter + sDay.substring(1);

        }
        dateTime += sDay;
        //dateTime+=day.toString();
        return dateTime;
    }

    @Override
    public int getCount() {

        return mStudentCalendar.getDaysOfYear();
    }
}
