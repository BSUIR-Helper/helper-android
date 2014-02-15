package ru.bsuirhelper.android.ui.schedule;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.notes.Note;
import ru.bsuirhelper.android.core.notes.NoteDatabase;
import ru.bsuirhelper.android.core.schedule.Lesson;

/**
 * Created by Влад on 21.09.13.
 */
class ViewAdapterLessons extends BaseAdapter {
    private final Context mContext;
    private final Lesson[] mValues;
    public static final int TAG_KEY_DAY = 0;

    public ViewAdapterLessons(Context context, Lesson[] values) {
        this.mContext = context;
        this.mValues = values;
    }

    @Override
    public int getCount() {
        return mValues.length;
    }

    @Override
    public Object getItem(int i) {
        return mValues[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
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
        ImageView ivNote = (ImageView) rowView.findViewById(R.id.imageview_note);

        if (!subjectType.equals("кч")) {
            lessonName.setText(subject);
        } else {
            lessonName.setText("КЧ");
        }
        lessonTime.setText(timePeriod);
        lessonTeacher.setText(teacher);
        Note note = NoteDatabase.getInstance(rowView.getContext()).fetchNoteByLessonId(lesson.id);
        if (note != null) {
            Log.wtf(ActivitySchedule.LOG_TAG, note.subject + " " + note.title);
            ivNote.setVisibility(View.VISIBLE);
        } else {
            ivNote.setVisibility(View.INVISIBLE);
        }

        if (!auditorium.equals("")) {
            lessonAuditorium.setText(lesson.fields.get("auditorium"));
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
        return true;
    }
}
