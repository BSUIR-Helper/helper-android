package ru.bsuirhelper.android.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ListView;
import com.google.analytics.tracking.android.EasyTracker;
import ru.bsuirhelper.android.ApplicationSettings;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.database.ScheduleManager;
import ru.bsuirhelper.android.core.models.StudentGroup;
import ru.bsuirhelper.android.ui.adapter.GroupsViewAdapter;

import java.util.ArrayList;

/**
 * Created by Влад on 13.10.13.
 */
public class ActivityDeleteGroups extends ActionBarActivity {
    private ListView mListView;
    private ApplicationSettings mSettings;
    private ScheduleManager mScheduleManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managerschedule);
        mListView = (ListView) findViewById(R.id.listview_groups);
        mScheduleManager = ScheduleManager.getInstance(this);
        mSettings = ApplicationSettings.getInstance(this);
        ArrayList<StudentGroup> groups = mScheduleManager.getGroups();
        final GroupsViewAdapter vaGroups = new GroupsViewAdapter(this, groups, R.layout.view_deletegroup);
        if (vaGroups.getCount() != 0) {
            mListView.setAdapter(vaGroups);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_groupsdelete_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_deletegroup:
                deleteGroupsWhichChecked(mListView);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteGroupsWhichChecked(ListView listView) {
        GroupsViewAdapter adapter = (GroupsViewAdapter) listView.getAdapter();
        for (int position = 0; position < listView.getCount(); position++) {
            CheckBox checkBox = (CheckBox) listView.getChildAt(position).findViewById(R.id.checkbox_fordelete);
            if (checkBox.isChecked()) {
                mScheduleManager.deleteSchedule(adapter.getItem(position).groupId);
                String defaultGroup = mSettings.getString(ApplicationSettings.DEFAULT_GROUP_OF_SCHEDULE, null);
                if (!(defaultGroup == null)) {
                    if (defaultGroup.equals(adapter.getItem(position).groupId)) {
                        mSettings.putString(ApplicationSettings.DEFAULT_GROUP_OF_SCHEDULE, null);
                    }
                }
            }
        }
    }


}
