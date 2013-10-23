package ru.bsuirhelper.android.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ru.bsuirhelper.android.Lesson;
import ru.bsuirhelper.android.bsuirhelper.R;

/**
 * Created by Влад on 21.09.13.
 */
class LessonsViewAdapter extends ArrayAdapter<Lesson> {
    private final Context context;
    private final Lesson[] values;

    public LessonsViewAdapter(Context context, Lesson[] values) {
        super(context, R.layout.view_lesson, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Lesson lesson = values[position];

        View rowView = inflater.inflate(R.layout.view_lesson, null);

        TextView lessonName = (TextView) rowView.findViewById(R.id.lesson_name);
        lessonName.setText(lesson.fields.get("subject"));
        TextView lessonTime = (TextView) rowView.findViewById(R.id.lesson_time);
        lessonTime.setText(lesson.fields.get("timePeriod"));
        TextView lessonAuditorium = (TextView) rowView.findViewById(R.id.lesson_auditorium);
        String auditorium = lesson.fields.get("auditorium");
        TextView lessonTeacher = (TextView) rowView.findViewById(R.id.lesson_teacher);
        lessonTeacher.setText(lesson.fields.get("teacher"));
        String lessonType = lesson.fields.get("subjectType");
        View lView = rowView.findViewById(R.id.lesson_type_color);

        if (!auditorium.equals("")) {
            lessonAuditorium.setText(lesson.fields.get("auditorium") + " аудитория");
        }

        if (lessonType.equals("лр")) {
            lView.setBackgroundColor(Color.parseColor("#FF4444"));
        } else if (lessonType.equals("пз")) {
            lView.setBackgroundColor(Color.parseColor("#FFBB33"));
        } else if (lessonType.equals("лк")) {
            lView.setBackgroundColor(Color.parseColor("#99CC00"));
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
