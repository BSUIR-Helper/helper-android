package ru.bsuirhelper.android.ui.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import ru.bsuirhelper.android.ApplicationSettings;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.appwidget.ScheduleWidgetProviderBig;
import ru.bsuirhelper.android.core.schedule.ScheduleManager;
import ru.bsuirhelper.android.core.schedule.StudentGroup;
import ru.bsuirhelper.android.ui.DialogFragmentAddGroup;
import ru.bsuirhelper.android.ui.DownloadScheduleTask;

import java.util.ArrayList;

/**
 * Created by Влад on 12.10.13.
 */
public class FragmentManagerGroups extends Fragment implements DownloadScheduleTask.CallBack {
    private ScheduleManager mScheduleManager;
    private ListView mListGroups;
    private TextView mTextViewNotification;
    public static final String TITLE = "Расписание";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScheduleManager = ScheduleManager.getInstance(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentContent = inflater.inflate(R.layout.activity_managerschedule, container, false);
        mTextViewNotification = (TextView) fragmentContent.findViewById(R.id.textview_groupsarenotadded);
        mListGroups = (ListView) fragmentContent.findViewById(R.id.listview_groups);
        refreshListGroup();
        mListGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                StudentGroup group = (StudentGroup) mListGroups.getAdapter().getItem(position);
                ApplicationSettings.getInstance(view.getContext()).putString("defaultgroup", group.groupId);
                updateAppWidget();
                Bundle args = new Bundle();
                args.putString("groupId", group.groupId);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment fragmentSchedule = new FragmentSchedule();
                fragmentSchedule.setArguments(args);
                fm.beginTransaction().replace(R.id.content_frame, fragmentSchedule).commit();
            }
        });
        return fragmentContent;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ApplicationSettings.getInstance(getActivity()).getBoolean("firststart", true)) {
            //     openDrawerMenu();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.menu_managergroups_fragment_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_startdeletegroups:
                Intent intent = new Intent(getActivity(), ActivityDeleteGroups.class);
                startActivity(intent);
                return true;
            case R.id.action_addgroup:
                DialogFragmentAddGroup dialog = new DialogFragmentAddGroup(this, getActivity());
                dialog.show(getActivity().getSupportFragmentManager(), "");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void refreshListGroup() {
        ArrayList<StudentGroup> alGroups = mScheduleManager.getGroups();
        StudentGroup[] groups = new StudentGroup[alGroups.size()];
        groups = alGroups.toArray(groups);
        GroupsViewAdapter vaGroups = new GroupsViewAdapter(getActivity(), groups, R.layout.view_group);
        if (vaGroups.getCount() != 0) {
            mListGroups.setAdapter(vaGroups);
            mTextViewNotification.setVisibility(View.INVISIBLE);
        } else {
            mTextViewNotification.setVisibility(View.VISIBLE);
        }
    }

    private void updateAppWidget() {
        Intent i = new Intent(getActivity(), ScheduleWidgetProviderBig.class);
        i.setAction(ScheduleWidgetProviderBig.UPDATE_ACTION);
        getActivity().sendBroadcast(i);
    }

    @Override
    public void onPostExecute() {
        Toast.makeText(getActivity(), "Расписание добавлено", Toast.LENGTH_SHORT);
        refreshListGroup();
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker tracker = EasyTracker.getInstance(getActivity());
        tracker.set(Fields.SCREEN_NAME, TITLE);
        tracker.send(MapBuilder.createAppView().build());
    }
}
