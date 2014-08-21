package ru.bsuirhelper.android.ui.schedule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import org.joda.time.DateTime;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.StudentCalendar;
import ru.bsuirhelper.android.core.notes.Note;
import ru.bsuirhelper.android.core.notes.NoteDatabase;
import ru.bsuirhelper.android.core.schedule.Lesson;
import ru.bsuirhelper.android.core.schedule.ScheduleManager;
import ru.bsuirhelper.android.ui.notes.ActivityDetailNote;
import ru.bsuirhelper.android.ui.notes.ActivityEditNote;

/**
 * Created by Влад on 10.10.13.
 */
public class FragmentScheduleOfDay extends Fragment {

    private final StudentCalendar mStudentCalendar = new StudentCalendar();
    private ListView mListOfLessons;
    private ViewAdapterLessons mAdapterLessons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_schedule, container, false);
        Context context = getActivity().getApplicationContext();
        ScheduleManager scheduleManager = ScheduleManager.getInstance(context);
        Bundle args = getArguments();
        TextView dateInText = (TextView) fragmentView.findViewById(R.id.date);
        DateTime day = StudentCalendar.convertToDefaultDateTime(args.getInt("day"));
        dateInText.setText(day.getDayOfMonth() + " " + day.monthOfYear().getAsText() + " " + day.year().getAsText() + "");
        String groupId = args.getString("groupId");
        int subgroup = args.getInt("subgroup");
        mAdapterLessons = new ViewAdapterLessons(context, scheduleManager.getLessonsOfDay(groupId, day, subgroup));

        if (isSummer(day) || isOtherSemester(day)) {
            TextView view = (TextView) fragmentView.findViewById(R.id.textView);
            view.setText(getString(R.string.lessons_are_not_known));
            view.setVisibility(View.VISIBLE);
        } else if (mAdapterLessons.getCount() != 0) {
            mListOfLessons = (ListView) fragmentView.findViewById(R.id.listView);
            mListOfLessons.setAdapter(mAdapterLessons);
            mListOfLessons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Lesson lesson = (Lesson) mListOfLessons.getAdapter().getItem(i);
                    Note note = NoteDatabase.getInstance(getActivity().getApplicationContext())
                            .fetchNoteByLessonId(lesson.id);
                    if (note != null) {
                        startActivity(createIntentForEditNote(lesson, note));
                    } else {
                        startActivity(createIntentForAddNote(lesson));
                    }
                }
            });
        } else {
            TextView view = (TextView) fragmentView.findViewById(R.id.textView);
            view.setVisibility(View.VISIBLE);
        }

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapterLessons.notifyDataSetChanged();
        mAdapterLessons.notifyDataSetInvalidated();
    }

    private Intent createIntentForAddNote(Lesson lesson) {
        Intent intent = new Intent(getActivity(), ActivityEditNote.class);
        intent.putExtra("lesson_id", lesson.id);
        intent.putExtra("lesson_subject", lesson.fields.get("subject") + " " + lesson.fields.get("subjectType"));
        intent.putExtra("REQUEST_CODE", ActivityEditNote.REQUEST_CODE_ADD_NOTE);
        return intent;
    }

    private Intent createIntentForEditNote(Lesson lesson, Note note) {
        Intent intent = new Intent(getActivity(), ActivityDetailNote.class);
        intent.putExtra("note_id", note.getId());
        intent.putExtra("lesson_id", lesson.id);
        intent.putExtra("REQUEST_CODE", ActivityEditNote.REQUEST_CODE_EDIT_NOTE);
        return intent;
    }

    private boolean isSummer(DateTime day) {
        return (day.getMonthOfYear() >= 7 && day.getMonthOfYear() <= 8);
    }

    private boolean isOtherSemester(DateTime day) {
        if (mStudentCalendar.getSemester() != mStudentCalendar.getSemesterByDay(day)) {
            return true;
        }
        return false;
    }
}
