package ru.bsuirhelper.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ru.bsuirhelper.android.ApplicationSettings;
import ru.bsuirhelper.android.ScheduleManager;
import ru.bsuirhelper.android.StudentGroup;
import ru.bsuirhelper.android.bsuirhelper.R;
import ru.bsuirhelper.android.appwidget.ScheduleWidgetProvider;

import java.util.ArrayList;

/**
 * Created by Влад on 12.10.13.
 */
public class ActivityManagerGroups extends ActionBarActivity implements DownloaderTaskFragment.TaskCallbacks {
    private ScheduleManager mScheduleManager;
    private ListView mListGroups;
    private TextView mTextViewNotification;
    private DownloaderTaskFragment mDownloaderTaskFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managerschedule);
        mScheduleManager = new ScheduleManager(this);
        mTextViewNotification = (TextView) findViewById(R.id.textview_groupsarenotadded);
        mListGroups = (ListView) findViewById(R.id.listview_groups);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        mDownloaderTaskFragment = (DownloaderTaskFragment) fragmentManager.findFragmentByTag("downloader");

        if (mDownloaderTaskFragment == null) {
            mDownloaderTaskFragment = new DownloaderTaskFragment("Загрузка расписания");
            fragmentManager.beginTransaction().add(mDownloaderTaskFragment, "downloader").commit();
        }
        refreshListGroup();
        mListGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                StudentGroup group = (StudentGroup) mListGroups.getAdapter().getItem(position);
                ApplicationSettings.getInstance(view.getContext()).putString("defaultgroup", group.groupId);
                updateAppWidget();
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                intent.putExtra("groupId", group.groupId);
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.managergroups_activity_actions, menu);
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
        Intent i = new Intent(this, ScheduleWidgetProvider.class);
        i.setAction(ScheduleWidgetProvider.UPDATE_ACTION);
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
