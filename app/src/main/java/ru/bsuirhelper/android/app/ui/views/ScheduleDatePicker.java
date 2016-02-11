package ru.bsuirhelper.android.app.ui.views;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import org.joda.time.DateTime;

import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.app.core.StudentCalendar;

public class ScheduleDatePicker extends DatePickerDialog {

    public ScheduleDatePicker(Context context, OnDateSetListener callBack, int year, int month, int day) {
        super(context, callBack, year, month, day);
        setTitle(getContext().getString(R.string.work_week) + " " + StudentCalendar.getWorkWeek(new DateTime(year, month + 1, day, 1, 1)));
        getDatePicker().setMinDate(StudentCalendar.getStartSemester().getMillis());
        getDatePicker().setMaxDate(StudentCalendar.getEndSemester().minusDays(1).getMillis());
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        super.onDateChanged(view, year, month, day);
        setTitle(getContext().getString(R.string.work_week) + " " + StudentCalendar.getWorkWeek(new DateTime(year, month + 1, day, 1, 1)));
    }
}