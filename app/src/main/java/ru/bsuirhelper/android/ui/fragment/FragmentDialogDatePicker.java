package ru.bsuirhelper.android.ui.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import org.joda.time.DateTime;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.StudentCalendar;

import static android.app.DatePickerDialog.OnDateSetListener;

/**
 * Created by Влад on 07.11.13.
 */
public abstract class FragmentDialogDatePicker extends DialogFragment implements OnDateSetListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        DateTime time = null;
        if (!StudentCalendar.isHolidays()) {
            time = DateTime.now();
        } else {
            time = new DateTime(DateTime.now().getYear(), 9, 1, 1, 1);
        }
        CustomDatePicker datePickerDialog = new CustomDatePicker(getActivity(), this, time.getYear(), time.getMonthOfYear() - 1, time.getDayOfMonth());
        if (Build.VERSION.SDK_INT > 10) {
            datePickerDialog.getDatePicker().setMinDate(StudentCalendar.getStartStudentYear());
            datePickerDialog.getDatePicker().setMaxDate(StudentCalendar.getEndStudentYear());
        }
        return datePickerDialog;
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setOnDismissListener(null);
        super.onDestroyView();
    }

    class CustomDatePicker extends DatePickerDialog {

        public CustomDatePicker(Context context, OnDateSetListener callBack, int year, int month, int day) {
            super(context, callBack, year, month, day);
            setTitle(getString(R.string.work_week) + " " + StudentCalendar.getWorkWeek(new DateTime(year, month + 1, day, 1, 1)));
        }

        @Override
        public void onDateChanged(DatePicker view, int year, int month, int day) {
            setTitle(getActivity().getString(R.string.work_week) + " " + StudentCalendar.getWorkWeek(new DateTime(year, month + 1, day, 1, 1)));
        }
    }
}
