package ru.bsuirhelper.android.app.core;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.PeriodType;
import org.joda.time.Weeks;

/**
 * Created by Влад on 11.10.13.
 */
public class StudentCalendar {
    public static final int FIRST_SEMESTER = 1;
    public static final int SECOND_SEMESTER = 2;
    private static final DateTime mCurrentDateTime = new DateTime();
    private static int mDaysOfYear = getDaysOfYear();
    private static int mSemester = getSemesterByDay(mCurrentDateTime);
    ;

    public static int getSemesterByDay(DateTime day) {
        int semester;
        if (day.getMonthOfYear() >= 7) {
            semester = FIRST_SEMESTER;
        } else {
            semester = SECOND_SEMESTER;
        }
        return semester;
    }

    public static int getDayOfYear() {
        int dayOfYear;
        if (mCurrentDateTime.getMonthOfYear() >= 9) {
            DateTime september = new DateTime(mCurrentDateTime.getYear(), 9, 1, 0, 0, 0);
            dayOfYear = new Interval(september, mCurrentDateTime).toPeriod(PeriodType.days()).getDays();
        } else {
            DateTime september = new DateTime(mCurrentDateTime.getYear() - 1, 9, 1, 0, 0, 0);
            dayOfYear = new Interval(september, mCurrentDateTime).toPeriod(PeriodType.days()).getDays();
        }
        return dayOfYear;
    }

    public int getDayOfYear(DateTime dateTime) {
        int dayOfYear;
        if (dateTime.getMonthOfYear() >= 9) {
            DateTime september = new DateTime(dateTime.getYear(), 9, 1, 0, 0, 0);
            dayOfYear = new Interval(september, dateTime).toPeriod(PeriodType.days()).getDays();
        } else {
            DateTime september = new DateTime(dateTime.getYear() - 1, 9, 1, 0, 0, 0);
            dayOfYear = new Interval(september, dateTime).toPeriod(PeriodType.days()).getDays();
        }
        return dayOfYear;
    }

    public static int getDaysOfYear() {
        mDaysOfYear = new Interval(getStartStudentYear(), getEndStudentYear()).toPeriod(PeriodType.days()).getDays();
        return mDaysOfYear + 1;
    }

    public static int getWorkWeek(DateTime dateTime) {
        DateTime september;
        if (dateTime.getMonthOfYear() <= 7) {
            september = new DateTime(dateTime.getYear() - 1, 9, 1, 0, 0, 0);
        } else {
            september = new DateTime(dateTime.getYear(), 9, 1, 0, 0, 0);
        }

        if (september.getDayOfWeek() != DateTimeConstants.MONDAY) {
            september = september.minusDays(september.getDayOfWeek() - 1);
        }
        Interval interval = new Interval(september, dateTime);
        int workWeek = (interval.toPeriod(PeriodType.weeks()).getWeeks() + 1) % 4;
        workWeek = workWeek == 0 ? 4 : workWeek;
        return workWeek;
    }

    public static DateTime convertToDefaultDateTime(int studentDay) {
        DateTime september;
        int currentMonth = DateTime.now().getMonthOfYear();
        if (currentMonth <= 6) {
            september = new DateTime(DateTime.now().getYear() - 1, 9, 1, 0, 0, 0);
        } else {
            september = new DateTime(DateTime.now().getYear(), 9, 1, 0, 0, 0);
        }
        return september.plusDays(studentDay - 1);
    }

    public int getSemester() {
        return mSemester;
    }

    public static long getStartStudentYear() {
        DateTime september;
        if (DateTime.now().getMonthOfYear() <= 7) {
            september = new DateTime(DateTime.now().getYear() - 1, 9, 1, 0, 0, 0);
        } else {
            september = new DateTime(DateTime.now().getYear(), 9, 1, 1, 0, 0);
        }
        return september.getMillis();
    }

    public static long getEndStudentYear() {
        DateTime july;
        if (DateTime.now().getMonthOfYear() <= 7) {
            july = new DateTime(DateTime.now().getYear(), 7, 31, 1, 0, 0);
        } else {
            july = new DateTime(DateTime.now().getYear() + 1, 7, 31, 1, 0, 0);
        }
        return july.getMillis();
    }

    public static DateTime getStartSemester() {
        DateTime now = DateTime.now();
        if (now.getMonthOfYear() >= 9) {
            return new DateTime(DateTime.now().getYear(), DateTimeConstants.SEPTEMBER, 1, 0, 0, 0);
        } else {
            return new DateTime(DateTime.now().getYear(), DateTimeConstants.JANUARY, 1, 0, 0, 0);
        }
    }

    public static DateTime getEndSemester() {
        DateTime now = DateTime.now();
        if (now.getMonthOfYear() >= 9) {
            return new DateTime(DateTime.now().getYear(), DateTimeConstants.DECEMBER, 31, 0, 0, 0);
        } else {
            return new DateTime(DateTime.now().getYear(), DateTimeConstants.AUGUST, 1, 0, 0, 0);
        }
    }

    public static boolean isHolidays() {
        DateTime currentTime = DateTime.now();
        if (currentTime.getMonthOfYear() >= 7 && currentTime.getMonthOfYear() <= 8) {
            return true;
        }
        return false;
    }

    public static DateTime getCurrentDay() {
        return mCurrentDateTime;
    }

    public static int getWeeksInYear() {
        return mCurrentDateTime.weekOfWeekyear().getMaximumValue();
    }

}
