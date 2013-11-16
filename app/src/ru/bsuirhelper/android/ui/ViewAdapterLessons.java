package ru.bsuirhelper.android.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ru.bsuirhelper.android.Lesson;
import ru.bsuirhelper.android.R;

/**
 * Created by Влад on 21.09.13.
 */
class ViewAdapterLessons extends ArrayAdapter<Lesson> {
    private final Context mContext;
    private final Lesson[] mValues;

    public ViewAdapterLessons(Context context, Lesson[] values) {
        super(context, R.layout.view_lesson, values);
        this.mContext = context;
        this.mValues = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Lesson lesson = mValues[position];

        String subject = lesson.fields.get("subject");
        String timePeriod = lesson.fields.get("timePeriod");
        String auditorium = lesson.fields.get("auditorium");
        String teacher = lesson.fields.get("teacher");
        String subjectType = lesson.fields.get("subjectType");

        View rowView = inflater.inflate(R.layout.view_lesson, null);
        View lView = rowView.findViewById(R.id.lesson_type_color);
        TextView lessonTime = (TextView) rowView.findViewById(R.id.lesson_time);
        TextView lessonAuditorium = (TextView) rowView.findViewById(R.id.lesson_auditorium);
        TextView lessonTeacher = (TextView) rowView.findViewById(R.id.lesson_teacher);
        TextView lessonName = (TextView) rowView.findViewById(R.id.lesson_name);

        lessonName.setText(subject);
        lessonTime.setText(timePeriod);
        lessonTeacher.setText(teacher);

        if (!auditorium.equals("")) {
            lessonAuditorium.setText(lesson.fields.get("auditorium") + " аудитория");
        }

        if (subjectType.equals("лр")) {
            lView.setBackgroundColor(mContext.getResources().getColor(R.color.red));
        } else if (subjectType.equals("пз")) {
            lView.setBackgroundColor(mContext.getResources().getColor(R.color.orange));
        } else if (subjectType.equals("лк")) {
            lView.setBackgroundColor(mContext.getResources().getColor(R.color.green));
        } else {
            lView.setBackgroundColor(Color.WHITE);
        }
        View verticalLine = rowView.findViewById(R.id.customview);
        verticalLine.setBackgroundColor(Color.WHITE);
        return rowView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
