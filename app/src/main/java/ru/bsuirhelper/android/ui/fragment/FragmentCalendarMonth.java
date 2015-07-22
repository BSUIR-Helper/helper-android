package ru.bsuirhelper.android.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import butterknife.Bind;
import butterknife.ButterKnife;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.ui.fragment.base.BaseFragment;
import timber.log.Timber;

/**
 * Created by Grishechko on 20.07.2015.
 */
public class FragmentCalendarMonth extends BaseFragment {
    private static final String KEY_MONTH = "KEY_MONTH";
    @Bind(R.id.rv_calendar)
    RecyclerView mRecyclerView;
    private int mMonth;
    private CalendarAdapter mCalendarAdapter;

    public static FragmentCalendarMonth newInstance(int month) {
        Bundle args = new Bundle();
        args.putInt(KEY_MONTH, month);
        FragmentCalendarMonth fragment = new FragmentCalendarMonth();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMonth = getArguments().getInt(KEY_MONTH);
        mCalendarAdapter = new CalendarAdapter(mMonth);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_month, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 7);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mCalendarAdapter);

    }

    private int getHeight() {
        Timber.d("Height of fragment:" + getView().getHeight());
        return getView().getHeight();
    }

    class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
        private final int MAX_COUNT = 49;
        private final int DAYS_IN_WEEK = 7;
        private String[] weekDays = getResources().getStringArray(R.array.week_days);
        private LocalDate mLocalDate;
        private DateTime mCurrenTime;

        public CalendarAdapter(int month) {
            mLocalDate = new LocalDate(2015, month, 1);
            mCurrenTime = new DateTime();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setVisibility(View.VISIBLE);
            if (position >= 0 && position < 7) {
                holder.setVisibility(View.VISIBLE);
                holder.setWeekDayName(String.valueOf(weekDays[position].charAt(0)));
            } else if (position >= getPositionWhenDateNumbersBegin() && position <= getPositionWhenDateNumbersEnd()) {
                holder.setToday(isToday(position));
                holder.setVisibility(View.VISIBLE);
                holder.setNumberDate(String.valueOf(getDateNumberByPosition(position)));
            } else {
                holder.setVisibility(View.INVISIBLE);
            }
        }

        private int getPositionWhenDateNumbersBegin() {
            int pos = mLocalDate.getDayOfWeek() + DAYS_IN_WEEK - 1;
            return pos;
        }

        private int getPositionWhenDateNumbersEnd() {
            int pos = getPositionWhenDateNumbersBegin() + mLocalDate.dayOfMonth().getMaximumValue() - 1;
            return pos;
        }

        private int getDateNumberByPosition(int pos) {
            return pos - getPositionWhenDateNumbersBegin() + 1;
        }

        private boolean isToday(int position) {
            return mMonth == mCurrenTime.getMonthOfYear() && getDateNumberByPosition(position) == mCurrenTime.getDayOfMonth();
        }

        @Override
        public int getItemCount() {
            return MAX_COUNT;
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.ll_root)
            View root;
            @Bind(R.id.fl_number_container)
            View numberContainer;
            @Bind(R.id.fl_week_day_container)
            View weekDayContainer;
            @Bind(R.id.tv_weekday_name)
            TextView tvWeekDay;
            @Bind(R.id.tv_date_number)
            TextView tvDateNumber;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            private void setWeekDayName(String text) {
                numberContainer.setVisibility(View.GONE);
                weekDayContainer.setVisibility(View.VISIBLE);
                tvWeekDay.setText(text);
            }

            private void setNumberDate(String text) {
                numberContainer.setVisibility(View.VISIBLE);
                weekDayContainer.setVisibility(View.GONE);
                tvDateNumber.setText(text);
            }

            private void setToday(boolean isToday) {
                if(isToday) {
                    numberContainer.setBackgroundResource(R.drawable.blue_circle);
                    tvDateNumber.setTextColor(Color.WHITE);
                } else {
                    numberContainer.setBackgroundResource(android.R.color.transparent);
                    tvDateNumber.setTextColor(Color.BLACK);
                }
            }

            private void setVisibility(int visibility) {
                root.setVisibility(visibility);
            }
        }
    }
}
