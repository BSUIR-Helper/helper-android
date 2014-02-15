package ru.bsuirhelper.android.ui.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ru.bsuirhelper.android.ApplicationSettings;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.appwidget.ScheduleWidgetProviderBig;
import ru.bsuirhelper.android.core.schedule.ScheduleManager;
import ru.bsuirhelper.android.core.schedule.StudentGroup;
import ru.bsuirhelper.android.ui.ActivityDrawerMenu;
import ru.bsuirhelper.android.ui.DialogFragmentAddGroup;
import ru.bsuirhelper.android.ui.DownloaderTaskFragment;

import java.util.ArrayList;

/**
 * Created by Влад on 12.10.13.
 */
public class ActivityManagerGroups extends ActivityDrawerMenu implements DownloaderTaskFragment.TaskCallbacks {
    private ScheduleManager mScheduleManager;
    private ListView mListGroups;
    private TextView mTextViewNotification;
    private DownloaderTaskFragment mDownloaderTaskFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managerschedule);
        mScheduleManager = ScheduleManager.getInstance(this);
        mTextViewNotification = (TextView) findViewById(R.id.textview_groupsarenotadded);
        mListGroups = (ListView) findViewById(R.id.listview_groups);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        mDownloaderTaskFragment = (DownloaderTaskFragment) fragmentManager.findFragmentByTag("downloader");

        if (mDownloaderTaskFragment == null) {
            mDownloaderTaskFragment = new DownloaderTaskFragment();
            mDownloaderTaskFragment.setMessage("Загрузка расписания");
            fragmentManager.beginTransaction().add(mDownloaderTaskFragment, "downloader").commit();
        }
        refreshListGroup();
        mListGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                StudentGroup group = (StudentGroup) mListGroups.getAdapter().getItem(position);
                ApplicationSettings.getInstance(view.getContext()).putString("defaultgroup", group.groupId);
                updateAppWidget();
                Intent intent = new Intent(view.getContext(), ActivitySchedule.class);
                intent.putExtra("groupId", group.groupId);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.wtf(ActivitySchedule.LOG_TAG, "First start ?:" + ApplicationSettings.getInstance(this).getBoolean("firststart", true));
        if (ApplicationSettings.getInstance(this).getBoolean("firststart", true)) {
            openDrawerMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_managergroups_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_startdeletegroups:
                Intent intent = new Intent(this, ActivityDeleteGroups.class);
                startActivity(intent);
                return true;
            case R.id.action_addgroup:
                DialogFragmentAddGroup dialog = new DialogFragmentAddGroup(mScheduleManager, mDownloaderTaskFragment);
                dialog.show(getSupportFragmentManager(), "");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void refreshListGroup() {
        ArrayList<StudentGroup> alGroups = mScheduleManager.getGroups();
        StudentGroup[] groups = new StudentGroup[alGroups.size()];
        groups = alGroups.toArray(groups);
        GroupsViewAdapter vaGroups = new GroupsViewAdapter(this, groups, R.layout.view_group);
        if (vaGroups.getCount() != 0) {
            mListGroups.setAdapter(vaGroups);
            mTextViewNotification.setVisibility(View.INVISIBLE);
        } else {
            mTextViewNotification.setVisibility(View.VISIBLE);
        }
    }

    private void updateAppWidget() {
        Intent i = new Intent(this, ScheduleWidgetProviderBig.class);
        i.setAction(ScheduleWidgetProviderBig.UPDATE_ACTION);
        sendBroadcast(i);
    }

    /* **********************************
     * Downloader Task callback methods
     * **********************************
     */

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onProgressUpdate(int percent) {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute(String result) {
        if (result.equals("Error")) {
            Toast.makeText(this, "Произошла ошибка", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Расписание добавлено", Toast.LENGTH_SHORT).show();
            refreshListGroup();
        }
    }

}
