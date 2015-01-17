package ru.bsuirhelper.android.ui.schedule;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.preference.PreferenceManager;
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
import ru.bsuirhelper.android.ui.ActivitySettings;

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

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.view_lesson, parent, false);
            setViewHolder(convertView);
        }
        final ViewHolder vh = (ViewHolder) convertView.getTag();

        Lesson lesson = mValues[position];
        String subject = lesson.fields.get("subject");
        String timePeriod = lesson.fields.get("timePeriod");
        String auditorium = lesson.fields.get("auditorium");
        String teacher = lesson.fields.get("teacher");
        String subjectType = lesson.fields.get("subjectType");

        if (!subjectType.equals(mContext.getString(R.string.ab_curator_hour))) {
            boolean isShowSubjectTypes = PreferenceManager.getDefaultSharedPreferences(
                    convertView.getContext()).getBoolean(ActivitySettings.KEY_SHOW_SUBJECTS_TYPE, false);
            if (isShowSubjectTypes && !subjectType.equals("")) {
                vh.lessonName.setText(subject);
                vh.lessonSubject.setVisibility(View.VISIBLE);
                vh.lessonSubject.setText(" (" + subjectType + ")");
            } else {
                vh.lessonName.setText(subject);
                vh.lessonSubject.setVisibility(View.GONE);
            }
        } else {
            vh.lessonName.setText(mContext.getString(R.string.ab_curator_hour).toUpperCase());
        }
        vh.lessonTime.setText(timePeriod);
        vh.lessonTeacher.setText(teacher);
        Note note = NoteDatabase.getInstance(convertView.getContext()).fetchNoteByLessonId(lesson.id);
        if (note != null) {
            vh.ivNote.setVisibility(View.VISIBLE);
        } else {
            vh.ivNote.setVisibility(View.INVISIBLE);
        }
        vh.lessonAuditorium.setText(auditorium);

        GradientDrawable typeOfSubject = (GradientDrawable) vh.lView.getBackground();

        if (subjectType.toLowerCase().equals(mContext.getString(R.string.ab_lab_id))) {
            typeOfSubject.setColor(mContext.getResources().getColor(R.color.red));
        } else if (subjectType.toLowerCase().equals(mContext.getString(R.string.ab_work_lesson_id))) {
            typeOfSubject.setColor(mContext.getResources().getColor(R.color.orange));
        } else if (subjectType.toLowerCase().equals(mContext.getString(R.string.ab_lecture_id))) {
            typeOfSubject.setColor(mContext.getResources().getColor(R.color.green));
        } else {
            typeOfSubject.setColor(mContext.getResources().getColor(R.color.white));
        }

        View verticalLine = convertView.findViewById(R.id.customview);
        verticalLine.setBackgroundColor(Color.BLACK);
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    public void setViewHolder(View rowView) {
        ViewHolder vh = new ViewHolder();
        vh.lView = rowView.findViewById(R.id.lesson_type_color);
        vh.lessonTime = (TextView) rowView.findViewById(R.id.lesson_time);
        vh.lessonAuditorium = (TextView) rowView.findViewById(R.id.lesson_auditorium);
        vh.lessonTeacher = (TextView) rowView.findViewById(R.id.lesson_teacher);
        vh.lessonName = (TextView) rowView.findViewById(R.id.lesson_name);
        vh.lessonSubject = (TextView) rowView.findViewById(R.id.lesson_subject_type);
        vh.ivNote = (ImageView) rowView.findViewById(R.id.imageview_note);
        rowView.setTag(vh);
    }

    class ViewHolder {
        View lView;
        TextView lessonTime;
        TextView lessonAuditorium;
        TextView lessonTeacher;
        TextView lessonName;
        TextView lessonSubject;
        ImageView ivNote;

    }
}
