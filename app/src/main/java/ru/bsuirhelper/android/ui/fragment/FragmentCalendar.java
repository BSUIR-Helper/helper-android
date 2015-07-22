package ru.bsuirhelper.android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;

import butterknife.Bind;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.ui.fragment.base.BaseFragment;
import timber.log.Timber;

/**
 * Created by Grishechko on 21.07.2015.
 */
public class FragmentCalendar extends BaseFragment {
    @Bind(R.id.vp_calendar)
    ViewPager mVpCalendar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVpCalendar.setAdapter(new CalendarPagerAdapter(getChildFragmentManager()));
        mVpCalendar.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                DateTime dateTime = new DateTime(2015, position + 1, 1, 1, 1);
                Timber.d(dateTime.monthOfYear().getAsText());
                Timber.d("HEIGHT:" + ((CalendarPagerAdapter) mVpCalendar.getAdapter()).getItem(position).getView().getHeight());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class CalendarPagerAdapter extends FragmentPagerAdapter {
        private Fragment[] mFragments;

        public CalendarPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragments = new Fragment[12];
            for (int pos = 0; pos < mFragments.length; pos++) {
                mFragments[pos] = FragmentCalendarMonth.newInstance(pos + 1);
            }
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return mFragments == null ? 0 : mFragments.length;
        }
    }
}
