package ru.bsuirhelper.android.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.List;

import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.ApplicationSettings;
import ru.bsuirhelper.android.core.cache.ScheduleManager;
import ru.bsuirhelper.android.core.models.StudentGroup;
import ru.bsuirhelper.android.ui.adapter.GroupsViewAdapter;

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
        List<StudentGroup> groups = mScheduleManager.getGroups(this);
        final GroupsViewAdapter vaGroups = new GroupsViewAdapter(this, groups, R.layout.view_deletegroup);
        if (vaGroups.getCount() != 0) {
            mListView.setAdapter(vaGroups);
        }

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
                mScheduleManager.deleteSchedule(this, adapter.getItem(position).getId());
                String defaultGroup = mSettings.getString(ApplicationSettings.DEFAULT_GROUP_OF_SCHEDULE, null);
                if (!(defaultGroup == null)) {
                    if (defaultGroup.equals(adapter.getItem(position).getId())) {
                        mSettings.putString(ApplicationSettings.DEFAULT_GROUP_OF_SCHEDULE, null);
                    }
                }
            }
        }
    }


}
