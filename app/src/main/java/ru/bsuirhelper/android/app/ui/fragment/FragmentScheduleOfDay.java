package ru.bsuirhelper.android.app.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import org.joda.time.DateTime;

import java.util.List;

import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.app.core.StudentCalendar;
import ru.bsuirhelper.android.app.core.cache.ScheduleManager;
import ru.bsuirhelper.android.app.core.models.Lesson;
import ru.bsuirhelper.android.app.ui.adapter.ViewAdapterLessons;
import ru.bsuirhelper.android.app.ui.loader.SchedulerLoader;

/**
 * Created by Влад on 10.10.13.
 */
public class FragmentScheduleOfDay extends Fragment implements LoaderManager.LoaderCallbacks<List<Lesson>> {
    private int LOADER_LESSONS = 1;
    private static final String KEY_DAY = "day";
    private static final String KEY_GROUP_ID = "groupId";
    private static final String KEY_SUBGROUP = "subgroup";
    private final StudentCalendar mStudentCalendar = new StudentCalendar();
    private ListView mListOfLessons;
    private ViewAdapterLessons mAdapterLessons;
    private int mSubgroup;
    private DateTime mDay;
    private String mGroupId;
    private View fragmentView;
    private View content;
    private TextView mDateInText;
    private List<Lesson> lessons;

    public static FragmentScheduleOfDay newInstance(int day, String groupId, int subgroup) {
        Bundle args = new Bundle();
        args.putInt(KEY_DAY, day);
        args.putString(KEY_GROUP_ID, groupId);
        args.putInt(KEY_SUBGROUP, subgroup);
        FragmentScheduleOfDay fragment = new FragmentScheduleOfDay();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mGroupId = args.getString(KEY_GROUP_ID);
        mSubgroup = args.getInt(KEY_SUBGROUP);
        mDay = StudentCalendar.convertToDefaultDateTime(args.getInt(KEY_DAY));
        lessons = ScheduleManager.getLessonsOfDay(getActivity(), mGroupId, mDay, mSubgroup);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_schedule, container, false);
        mDateInText = (TextView) fragmentView.findViewById(R.id.date);
        mDateInText.setText(mDay.getDayOfMonth() + " " + mDay.monthOfYear().getAsText() + " " + mDay.year().getAsText() + "");
        mListOfLessons = (ListView) fragmentView.findViewById(R.id.listView);
        mAdapterLessons = new ViewAdapterLessons(getActivity(), lessons);
        mDateInText.setBackgroundColor(getMostColor());
        if (isSummer(mDay) || isOtherSemester(mDay)) {
            TextView view = (TextView) fragmentView.findViewById(R.id.textView);
            fragmentView.setAlpha(1);
            view.setText(getString(R.string.lessons_are_not_known));
            view.setVisibility(View.VISIBLE);
            fragmentView.setAlpha(1);
        } else if (mAdapterLessons.getCount() != 0) {
            mListOfLessons.setAdapter(mAdapterLessons);
        } else {
            TextView view = (TextView) fragmentView.findViewById(R.id.textView);
            view.setVisibility(View.VISIBLE);
            fragmentView.setAlpha(1);
        }
        /*    mListOfLessons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ScheduleLesson lesson = (ScheduleLesson) mListOfLessons.getAdapter().getItem(i);
                    Note note = NoteDatabase.getInstance(getActivity().getApplicationContext())
                            .fetchNoteByLessonId(lesson.id);
                    if (note != null) {
                        startActivity(createIntentForEditNote(lesson, note));
                    } else {
                        startActivity(createIntentForAddNote(lesson));
                    }
                }
            });*/
        content = fragmentView.findViewById(R.id.fragment_schedule);
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /*
    private Intent createIntentForAddNote(ScheduleLesson lesson) {
        Intent intent = new Intent(getActivity(), ActivityEditNote.class);
        intent.putExtra("lesson_id", lesson.id);
        intent.putExtra("lesson_subject", lesson.fields.get("subject") + " " + lesson.fields.get("subjectType"));
        intent.putExtra("REQUEST_CODE", ActivityEditNote.REQUEST_CODE_ADD_NOTE);
        return intent;
    }

    private Intent createIntentForEditNote(ScheduleLesson lesson, Note note) {
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
            String lessonType = lesson.getType().toLowerCase();
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

    @Override
    public Loader<List<Lesson>> onCreateLoader(int i, Bundle bundle) {
        Logger.e("On Create Loader");
        return new SchedulerLoader(getActivity(), mDay, mGroupId, mSubgroup);
    }

    @Override
    public void onLoadFinished(Loader<List<Lesson>> loader, List<Lesson> lessons) {
        mDateInText.setBackgroundColor(getResources().getColor(R.color.green));
        mAdapterLessons = new ViewAdapterLessons(getActivity(), lessons);
        if (isSummer(mDay) || isOtherSemester(mDay)) {
            TextView view = (TextView) fragmentView.findViewById(R.id.textView);
            fragmentView.setAlpha(1);
            view.setText(getString(R.string.lessons_are_not_known));
            view.setVisibility(View.VISIBLE);
            fragmentView.setAlpha(1);
        } else if (mAdapterLessons.getCount() != 0) {
            mListOfLessons.setAdapter(mAdapterLessons);
        } else {
            TextView view = (TextView) fragmentView.findViewById(R.id.textView);
            view.setVisibility(View.VISIBLE);
            fragmentView.setAlpha(1);
        }
        Logger.e("Data delivered");

    }

    @Override
    public void onLoaderReset(Loader<List<Lesson>> loader) {
    }
}
