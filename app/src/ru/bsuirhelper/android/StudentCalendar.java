package ru.bsuirhelper.android;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.PeriodType;

/**
 * Created by Влад on 11.10.13.
 */
class StudentCalendar {
    private final DateTime mCurrentDateTime;
    private static int mDaysOfYear = 0;
    private static int mSemester;

    public StudentCalendar() {
        mCurrentDateTime = new DateTime();
        mDaysOfYear = getDaysOfYear();

        if (mCurrentDateTime.getMonthOfYear() >= 9) {
            mSemester = 1;
        } else {
            mSemester = 2;
        }
    }

    public int getDayOfYear() {
        int dayOfYear;

        if (mCurrentDateTime.getMonthOfYear() >= 9) {
            DateTime september = new DateTime(mCurrentDateTime.getYear(), 9, 1, 0, 0, 0);
            dayOfYear = new Interval(september, mCurrentDateTime).toPeriod(PeriodType.days()).getDays();
        } else {
            DateTime september = new DateTime(mCurrentDateTime.getYear() - 1, 9, 1, 0, 0, 0);
            dayOfYear = new Interval(september, mCurrentDateTime).toPeriod(PeriodType.days()).getDays();
        }

        return dayOfYear + 1;
    }

    public int getDaysOfYear() {
        if (mDaysOfYear == 0) {
            DateTime september;
            DateTime august;

            if (mCurrentDateTime.getMonthOfYear() <= 8) {
                september = new DateTime(mCurrentDateTime.getYear() - 1, 9, 1, 0, 0, 0);
                august = new DateTime(mCurrentDateTime.getYear(), 8, 31, 0, 0, 0);
            } else {
                september = new DateTime(mCurrentDateTime.getYear(), 8, 31, 0, 0, 0);
                august = new DateTime(mCurrentDateTime.getYear() + 1, 8, 31, 0, 0, 0);
            }

            mDaysOfYear = new Interval(september, august).toPeriod(PeriodType.days()).getDays();
        }
        return mDaysOfYear;
    }

    public int getWorkWeek(DateTime dateTime) {
        DateTime september;

        if (dateTime.getMonthOfYear() <= 8) {
            september = new DateTime(dateTime.getYear() - 1, 9, 1, 0, 0, 0);
        } else {
            september = new DateTime(dateTime.getYear(), 9, 1, 0, 0, 0);
        }

        Interval interval = new Interval(september, dateTime);
        int workWeek = (interval.toPeriod(PeriodType.weeks()).getWeeks() + 2) % 4;
        workWeek = workWeek == 0 ? 4 : workWeek;
        return workWeek;
    }

    public static DateTime convertToDefaultDateTime(int studentDay) {
        DateTime september;
        int currentMonth = DateTime.now().getMonthOfYear();
        if (currentMonth <= 8) {
            september = new DateTime(DateTime.now().getYear() - 1, 9, 1, 0, 0, 0);
        } else {
            september = new DateTime(DateTime.now().getYear(), 9, 1, 0, 0, 0);
        }
        return september.plusDays(studentDay - 1);
    }

    public int getSemester() {
        return mSemester;
    }
}
