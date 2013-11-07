package ru.bsuirhelper.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ListView;
import ru.bsuirhelper.android.ApplicationSettings;
import ru.bsuirhelper.android.ScheduleManager;
import ru.bsuirhelper.android.StudentGroup;
import ru.bsuirhelper.android.bsuirhelper.R;

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
        mScheduleManager = new ScheduleManager(this);
        mSettings = ApplicationSettings.getInstance(this);
        ArrayList<StudentGroup> alGroups = mScheduleManager.getGroups();
        StudentGroup[] groups = new StudentGroup[alGroups.size()];
        groups = alGroups.toArray(groups);
        final GroupsViewAdapter vaGroups = new GroupsViewAdapter(this, groups, R.layout.view_deletegroup);
        if (vaGroups.getCount() != 0) {
            mListView.setAdapter(vaGroups);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_groupsdelete_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_deletegroup:
                deleteGroupsWhichChecked(mListView);
                Intent intent = new Intent(this, ActivityManagerGroups.class);
                startActivity(intent);
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
                //Delete from settings default group
                String defaultGroup = mSettings.getString("defaultgroup", null);

                if (!(defaultGroup == null)) {
                    if (defaultGroup.equals(adapter.getItem(position).groupId)) {
                        mSettings.putString("defaultgroup", null);
                    }
                }
            }
        }
    }


}
