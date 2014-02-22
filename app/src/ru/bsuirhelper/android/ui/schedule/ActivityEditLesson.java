
package ru.bsuirhelper.android.ui.schedule;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import ru.bsuirhelper.android.R;

/**
 * Created by Влад on 17.02.14.
 */
public class ActivityEditLesson extends Activity {

    Spinner mSpinnerSubjectTypes;
    Spinner mSpinnerWeekDay;
    Spinner mSpinnerWorkWeek;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_editlesson);
        mSpinnerSubjectTypes = (Spinner) findViewById(R.id.spinner_subject_types);
        mSpinnerWeekDay = (Spinner) findViewById(R.id.spinner_week_day);
        mSpinnerWorkWeek = (Spinner) findViewById(R.id.spinner_work_week);

        mSpinnerSubjectTypes.setAdapter(ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.lessons_types, android.R.layout.simple_spinner_dropdown_item));
        mSpinnerWeekDay.setAdapter(ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.week_days, android.R.layout.simple_spinner_dropdown_item));
        mSpinnerWorkWeek.setAdapter(ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.work_weeks, android.R.layout.simple_spinner_dropdown_item));

    }
}
