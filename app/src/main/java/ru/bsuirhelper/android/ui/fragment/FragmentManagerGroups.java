package ru.bsuirhelper.android.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.ApplicationSettings;
import ru.bsuirhelper.android.core.cache.ScheduleManager;
import ru.bsuirhelper.android.core.models.StudentGroup;
import ru.bsuirhelper.android.ui.activity.ActivityDeleteGroups;
import ru.bsuirhelper.android.ui.adapter.GroupsViewAdapter;
import ru.bsuirhelper.android.ui.appwidget.ScheduleWidgetProviderBase;
import ru.bsuirhelper.android.ui.dialog.DialogFragmentAddGroup;
import ru.bsuirhelper.android.ui.listener.AsyncTaskListener;

/**
 * Created by Влад on 12.10.13.
 */
public class FragmentManagerGroups extends Fragment implements AsyncTaskListener {
    private ScheduleManager mScheduleManager;
    private ListView mListGroups;
    private TextView mTextViewNotification;
    private GroupsViewAdapter mGroupsAdapter;
    public static final String TITLE = "Расписание";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScheduleManager = ScheduleManager.getInstance(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentContent = inflater.inflate(R.layout.activity_managerschedule, container, false);
        mTextViewNotification = (TextView) fragmentContent.findViewById(R.id.textview_groupsarenotadded);
        mListGroups = (ListView) fragmentContent.findViewById(R.id.listview_groups);
        mListGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                StudentGroup group = (StudentGroup) mListGroups.getAdapter().getItem(position);
                ApplicationSettings.getInstance(view.getContext()).putString("defaultgroup", String.valueOf(group.getId()));
                ScheduleWidgetProviderBase.updateAllWidgets(getActivity().getApplicationContext());
                Bundle args = new Bundle();
                args.putString("groupId", String.valueOf(group.getId()));
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
        refreshListGroup();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
                    DialogFragmentAddGroup dialog = new DialogFragmentAddGroup();
                    dialog.show(getActivity().getSupportFragmentManager(), "");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void refreshListGroup() {
        List<StudentGroup> groups = mScheduleManager.getGroups(getActivity());
        if (mGroupsAdapter == null) {
            mGroupsAdapter = new GroupsViewAdapter(getActivity(), groups, R.layout.view_group);
        } else {
            mGroupsAdapter.values.clear();
            mGroupsAdapter.values.addAll(groups);
            mGroupsAdapter.notifyDataSetChanged();
            mGroupsAdapter.notifyDataSetInvalidated();
        }
        if (mGroupsAdapter.getCount() != 0) {
            mListGroups.setAdapter(mGroupsAdapter);
            mTextViewNotification.setVisibility(View.INVISIBLE);
        } else {
            mTextViewNotification.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onPostExecute() {
        refreshListGroup();
    }

}
