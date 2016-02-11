package ru.bsuirhelper.android.app.ui.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;


import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Weeks;

import butterknife.Bind;
import butterknife.ButterKnife;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.app.core.StudentCalendar;

/**
 * Created by Grishechko on 04.02.2016.
 */
public class DaysAdapter extends PagerAdapter {
    private DateTime startSemester;
    private ViewPager mViewPager;
    private View mSelector;
    private DateTime dateTimeNow = DateTime.now();
    private int mSelectorSideOffset = 0;
    private int mWeeks;
    private DateTime mSelectedDay;
    private static final String ITEM = "item:";
    private OnDaySelectListener onDaySelectListener;

    public DaysAdapter(@NonNull ViewPager viewPager) {
        startSemester = StudentCalendar.getStartSemester().withDayOfWeek(DateTimeConstants.MONDAY);
        DateTime endSemester = StudentCalendar.getEndSemester().withDayOfWeek(DateTimeConstants.MONDAY);
        mWeeks = Weeks.weeksBetween(startSemester, endSemester).getWeeks();
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    if (mSelectedDay != null) {
                        mSelector.setVisibility(View.INVISIBLE);
                        mSelector = mViewPager.findViewWithTag(ITEM + mViewPager.getCurrentItem()).findViewById(R.id.selector);
                        moveSelector(mSelectedDay.getDayOfWeek());
                        mSelectedDay = null;
                    }
                }
            }
        });
    }

    @Override
    public int getCount() {
        return mWeeks;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        DateTime dateTime = startSemester.plusWeeks(position);
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_schedule_week, container, false);
        DaysHolder daysHolder = new DaysHolder(view);

        updateViewDay(dateTime, daysHolder, daysHolder.tvMo, container.getResources());
        updateViewDay(dateTime.plusDays(1), daysHolder, daysHolder.tvTu, container.getResources());
        updateViewDay(dateTime.plusDays(2), daysHolder, daysHolder.tvWe, container.getResources());
        updateViewDay(dateTime.plusDays(3), daysHolder, daysHolder.tvTh, container.getResources());
        updateViewDay(dateTime.plusDays(4), daysHolder, daysHolder.tvFr, container.getResources());
        updateViewDay(dateTime.plusDays(5), daysHolder, daysHolder.tvSa, container.getResources());
        updateViewDay(dateTime.plusDays(6), daysHolder, daysHolder.tvSu, container.getResources());

        container.addView(view);

        view.setTag(ITEM + position);

        if (mSelector == null) {
            moveSelector(StudentCalendar.getStartSemester().getDayOfWeek());
        }

        return view;
    }

    public void setDay(@NonNull DateTime day) {
        mSelectedDay = day;
        int selectedPosition = getWeekPosition(mSelectedDay);
        if (selectedPosition != mViewPager.getCurrentItem()) {
            mViewPager.setCurrentItem(selectedPosition);
        } else {
            moveSelector(day.getDayOfWeek());
        }
    }

    private void moveSelector(int dayOfWeek) {
        if (mSelector != null) {
            mSelector.setVisibility(View.INVISIBLE);
        }
        mSelector = mViewPager.findViewWithTag(ITEM + mViewPager.getCurrentItem()).findViewById(R.id.selector);
        mSelector.setVisibility(View.VISIBLE);
        if (mSelectorSideOffset == 0) {
            mSelector.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    animateSelector(mSelector, dayOfWeek);
                    mSelector.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        } else {
            animateSelector(mSelector, dayOfWeek);
        }
    }

    private int getWeekPosition(DateTime day) {
        return Weeks.weeksBetween(startSemester, day.withDayOfWeek(DateTimeConstants.SUNDAY).plusDays(1)).getWeeks() - 1;
    }

    private void updateViewDay(DateTime day, DaysHolder daysHolder, TextView textView, Resources res) {
        boolean isFirstSemester = dateTimeNow.getMonthOfYear() >= DateTimeConstants.SEPTEMBER;
        int resColor = android.R.color.white;
        if (day.getMonthOfYear() == DateTimeConstants.AUGUST) {
            resColor = R.color.brigth_gray;
        } else if (isFirstSemester && day.getMonthOfYear() == DateTimeConstants.JANUARY) {
            resColor = R.color.brigth_gray;
        } else if (!isFirstSemester && day.getMonthOfYear() == DateTimeConstants.DECEMBER) {
            resColor = R.color.brigth_gray;
        }
        textView.setTextColor(res.getColor(resColor));
        textView.setText(String.valueOf(day.getDayOfMonth()));
        if (resColor != R.color.brigth_gray) {
            textView.setOnClickListener(new OnDayClickListener(daysHolder));
        }
        textView.setTag(day.getDayOfYear());
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    class OnDayClickListener implements View.OnClickListener {
        private DaysHolder daysHolder;

        public OnDayClickListener(DaysHolder daysHolder) {
            this.daysHolder = daysHolder;
        }

        @Override
        public void onClick(View v) {
            if (v instanceof TextView) {
                moveSelector(DateTime.now().withDayOfYear((Integer) v.getTag()).getDayOfWeek());
                try {
                    if (onDaySelectListener != null) {
                        onDaySelectListener.onDaySelect(DateTime.now().withDayOfYear((Integer) v.getTag()));
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException("TextView must contain DateTime");
                }
            }
        }
    }

    private void animateSelector(View selector, int dayOfWeek) {
        int positionSelector = (dayOfWeek - 1) * (selector.getWidth() + mSelectorSideOffset * 2);
        ObjectAnimator animator = ObjectAnimator.ofFloat(selector, "x", selector.getX(), positionSelector + mSelectorSideOffset).setDuration(selector.getResources().
                getInteger(android.R.integer.config_shortAnimTime));
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                selector.setX(positionSelector + mSelectorSideOffset);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public String getFormattedDate(Resources resources, int position) {
        return String.format("%s %d", resources.getString(getMonthName(startSemester.plusWeeks(position).getMonthOfYear())), startSemester.plusWeeks(position).getYear());
    }

    public int getWorkWeek(int position) {
        return StudentCalendar.getWorkWeek(startSemester.plusWeeks(position));
    }

    @StringRes
    public int getMonthName(int month) {
        switch (month) {
            case DateTimeConstants.SEPTEMBER:
                return R.string.september;
            case DateTimeConstants.OCTOBER:
                return R.string.october;
            case DateTimeConstants.NOVEMBER:
                return R.string.november;
            case DateTimeConstants.DECEMBER:
                return R.string.december;
            case DateTimeConstants.JANUARY:
                return R.string.january;
            case DateTimeConstants.FEBRUARY:
                return R.string.february;
            case DateTimeConstants.MARCH:
                return R.string.march;
            case DateTimeConstants.APRIL:
                return R.string.april;
            case DateTimeConstants.MAY:
                return R.string.may;
            case DateTimeConstants.JUNE:
                return R.string.june;
            case DateTimeConstants.JULY:
                return R.string.july;
            case DateTimeConstants.AUGUST:
                return R.string.august;
        }
        return R.string.september;
    }

    public interface OnDaySelectListener {
        void onDaySelect(DateTime day);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    class DaysHolder {
        @Bind(R.id.tv_day_mo_under) TextView tvMo;
        @Bind(R.id.tv_day_tu_under) TextView tvTu;
        @Bind(R.id.tv_day_we_under) TextView tvWe;
        @Bind(R.id.tv_day_th_under) TextView tvTh;
        @Bind(R.id.tv_day_fr_under) TextView tvFr;
        @Bind(R.id.tv_day_sa_under) TextView tvSa;
        @Bind(R.id.tv_day_su_under) TextView tvSu;
        @Bind(R.id.selector) View selector;

        View view;

        public DaysHolder(View view) {
            this.view = view;
            ButterKnife.bind(this, view);
            selector.setVisibility(View.INVISIBLE);
            if (mSelectorSideOffset == 0) {
                view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        PercentRelativeLayout.LayoutParams params = (PercentRelativeLayout.LayoutParams) selector.getLayoutParams();
                        mSelectorSideOffset = params.leftMargin;
                        view.getViewTreeObserver().removeOnPreDrawListener(this);
                        return true;
                    }
                });
            }
        }

        public TextView getTextViewByDayOfWeek(int dayOfWeek) {
            switch (dayOfWeek) {
                case DateTimeConstants.MONDAY:
                    return tvMo;
                case DateTimeConstants.TUESDAY:
                    return tvTu;
                case DateTimeConstants.WEDNESDAY:
                    return tvWe;
                case DateTimeConstants.THURSDAY:
                    return tvTh;
                case DateTimeConstants.FRIDAY:
                    return tvFr;
                case DateTimeConstants.SATURDAY:
                    return tvSa;
                case DateTimeConstants.SUNDAY:
                    return tvSu;
            }
            return null;
        }
    }

}
