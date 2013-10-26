package ru.bsuirhelper.android.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import org.joda.time.DateTime;
import ru.bsuirhelper.android.ScheduleManager;
import ru.bsuirhelper.android.StudentCalendar;
import ru.bsuirhelper.android.bsuirhelper.R;

/**
 * Created by Влад on 10.10.13.
 */
public class FragmentSchedule extends Fragment {

    private final StudentCalendar mStudentCalendar = new StudentCalendar();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        View fragmentView = inflater.inflate(R.layout.fragment_schedule, container, false);
        Context context = getActivity().getApplicationContext();
        ScheduleManager scheduleManager = new ScheduleManager(context);
        Bundle args = getArguments();
        DateTime day = StudentCalendar.convertToDefaultDateTime(args.getInt("day"));
        TextView dateInText = (TextView) fragmentView.findViewById(R.id.date);
        dateInText.setText(day.getDayOfMonth() + " " + day.monthOfYear().getAsText() + " " + day.year().getAsText() + "");
        String groupId = args.getString("groupId");
        int subgroup = args.getInt("subgroup");
        LessonsViewAdapter lessonsViewAdapter = new LessonsViewAdapter(context, scheduleManager.getLessonsOfDay(groupId, day, subgroup));

        if ((day.getMonthOfYear() <= 8 && mStudentCalendar.getSemester() == 1) ||
                (day.getMonthOfYear() >= 9 && mStudentCalendar.getSemester() == 2)) {
            TextView view = (TextView) fragmentView.findViewById(R.id.textView);
            view.setText("Занятия не известны");
            view.setVisibility(View.VISIBLE);
        } else if (lessonsViewAdapter.getCount() != 0) {
            ListView lv = (ListView) fragmentView.findViewById(R.id.listView);
            lv.setAdapter(lessonsViewAdapter);
        } else {
            TextView view = (TextView) fragmentView.findViewById(R.id.textView);
            view.setVisibility(View.VISIBLE);
        }

        return fragmentView;
    }

}
