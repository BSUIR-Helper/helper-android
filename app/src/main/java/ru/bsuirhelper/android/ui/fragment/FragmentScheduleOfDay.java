package ru.bsuirhelper.android.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.DateTime;

import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.StudentCalendar;
import ru.bsuirhelper.android.core.cache.ScheduleManager;
import ru.bsuirhelper.android.core.models.Lesson;
import ru.bsuirhelper.android.ui.adapter.ViewAdapterLessons;

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
        Context context = getActivity();
        Bundle args = getArguments();
        TextView dateInText = (TextView) fragmentView.findViewById(R.id.date);
        DateTime day = StudentCalendar.convertToDefaultDateTime(args.getInt("day"));
        dateInText.setText(day.getDayOfMonth() + " " + day.monthOfYear().getAsText() + " " + day.year().getAsText() + "");

        String groupId = args.getString("groupId");
        int subgroup = args.getInt("subgroup");
        mAdapterLessons = new ViewAdapterLessons(context, ScheduleManager.getLessonsOfDay(context, groupId, day, subgroup));
        dateInText.setBackgroundColor(getMostColor());
        if (isSummer(day) || isOtherSemester(day)) {
            TextView view = (TextView) fragmentView.findViewById(R.id.textView);
            view.setText(getString(R.string.lessons_are_not_known));
            view.setVisibility(View.VISIBLE);
        } else if (mAdapterLessons.getCount() != 0) {
            mListOfLessons = (ListView) fragmentView.findViewById(R.id.listView);
            mListOfLessons.setAdapter(mAdapterLessons);
        /*    mListOfLessons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
            });*/
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


    /*
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
*/
    private boolean isSummer(DateTime day) {
        return (day.getMonthOfYear() >= 7 && day.getMonthOfYear() <= 8);
    }

    private boolean isOtherSemester(DateTime day) {
        return mStudentCalendar.getSemester() != mStudentCalendar.getSemesterByDay(day);
    }

    public int getMostColor() {
        int type[][] = new int[2][3];
        type[0][0] = getResources().getColor(R.color.red);
        type[0][1] = getResources().getColor(R.color.orange);
        type[0][2] = getResources().getColor(R.color.green);
        for (int i = 0; i < mAdapterLessons.getCount(); i++) {
            Lesson lesson = (Lesson) mAdapterLessons.getItem(i);
            String lessonType = lesson.getType();
            if (lessonType.equals("лк")) {
                type[1][2] += 1;
            } else if (lessonType.equals("лр")) {
                type[1][0] += 1;
            } else {
                type[1][1] += 1;
            }
        }

        int max = type[1][2];
        int color = type[0][2];
        for (int i = 0; i < 3; i++) {
            if (max < type[1][i]) {
                max = type[1][i];
                color = type[0][i];
            }
        }
        return color;
    }
}
