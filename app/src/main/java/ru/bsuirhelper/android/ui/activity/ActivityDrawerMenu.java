package ru.bsuirhelper.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.ApplicationSettings;
import ru.bsuirhelper.android.core.cache.ScheduleManager;
import ru.bsuirhelper.android.core.models.StudentGroup;
import ru.bsuirhelper.android.ui.adapter.SpinnerGroupsAdapter;
import ru.bsuirhelper.android.ui.dialog.DialogFragmentAddGroup;
import ru.bsuirhelper.android.ui.fragment.FragmentManagerGroups;
import ru.bsuirhelper.android.ui.fragment.FragmentSchedule;
import ru.bsuirhelper.android.ui.listener.AsyncTaskListener;

/**
 * Created by Влад on 29.10.13.
 */
public class ActivityDrawerMenu extends ActionBarActivity implements AsyncTaskListener {
    private final int SCHEDULE_FRAGMENT = 1;
    private final int ACTIVITY_SETTINGS = 2;
    private boolean isScheduleClicked;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar mActionBar;
    private String[] mMenuItems;
    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawerlayout);
        mMenuItems = getResources().getStringArray(R.array.menu_items);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        drawerInitialize();
        actionBarInitialize();
        //spinnerInitialize();
        FragmentManager fm = getSupportFragmentManager();
        String defaultGroup = ApplicationSettings.getInstance(this).getString(ApplicationSettings.ACTIVE_STUDENTGROUP, null);

        if (defaultGroup != null) {
            fm.beginTransaction().replace(R.id.content_frame, new FragmentSchedule()).commit();
        } else {
            fm.beginTransaction().replace(R.id.content_frame, new FragmentManagerGroups()).commit();
        }

        if (ApplicationSettings.getInstance(this).getBoolean("isFirstShowDrawer", true)) {
            openDrawerMenu();
            ApplicationSettings.getInstance(this).putBoolean("isFirstShowDrawer", false);
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mDrawerToggle.syncState();
    }

    private void drawerInitialize() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ){
            @Override
            public void onDrawerClosed(View drawerView) {
                if(isScheduleClicked) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentSchedule()).commit();
                    isScheduleClicked = false;
                }
            }};
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
        DrawerArrayAdapter drawerAdapter = new DrawerArrayAdapter(this, mMenuItems);
        View view = LayoutInflater.from(this).inflate(R.layout.header_drawermenu, null);
        Spinner mSpinnerGroups = (Spinner) view.findViewById(R.id.sp_schedules);
        final BaseAdapter groupsAdapter = new SpinnerGroupsAdapter(this, ScheduleManager.getInstance(this).getGroups(this));
        mSpinnerGroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Logger.i("Select position in spinner:" + position);
                if (position != groupsAdapter.getCount() - 1) {
                    StudentGroup studentGroup = (StudentGroup) groupsAdapter.getItem(position);
                    ApplicationSettings.getInstance(ActivityDrawerMenu.this).
                            setActiveGroup(String.valueOf(studentGroup.getId()));
                    isScheduleClicked = true;
                    closeDrawerMenu();
                } else if (position == groupsAdapter.getCount() - 1) {
                    new DialogFragmentAddGroup().show(getSupportFragmentManager(), null);
                }
                closeDrawerMenu();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpinnerGroups.setAdapter(groupsAdapter);
        mDrawerList.addHeaderView(view);
        mDrawerList.setAdapter(drawerAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                closeDrawerMenu();
                mActionBar.setSubtitle(null);
                Intent intent = new Intent();
                switch (position) {
                    case SCHEDULE_FRAGMENT:
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentSchedule()).commit();
                        mActionBar.setTitle(FragmentManagerGroups.TITLE);
                        break;
                    case ACTIVITY_SETTINGS:
                        startActivity(intent.setClass(getApplicationContext(), ActivitySettings.class));
                        return;
                }
                closeDrawerMenu();
            }
        });
    }

    private void actionBarInitialize() {
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void openDrawerMenu() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    protected void closeDrawerMenu() {
        if(mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(findViewById(R.id.navigation_drawer));
        }
    }

    @Override
    public void onPostExecute() {
        Toast.makeText(this, getString(R.string.schedule_is_updated), Toast.LENGTH_SHORT).show();
    }

    class DrawerArrayAdapter extends ArrayAdapter<String> {
        LayoutInflater mInflater;

        public DrawerArrayAdapter(Context context, String[] menuItems) {
            super(context, R.layout.drawer_list_item, menuItems);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parentView) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.drawer_list_item, null);
                setViewHolder(convertView);
            }
            ViewHolder vh = (ViewHolder) convertView.getTag();
            vh.menuName.setText(getItem(position));
            switch (position + 1) {
                case SCHEDULE_FRAGMENT:
                    vh.icon.setImageResource(R.drawable.ic_timetable);
                    break;
               /* case NOTE_FRAGMENT:
                    counterOfNotes.setVisibility(View.VISIBLE);
                    counterOfNotes.setText(ApplicationSettings.getInstance(ActivityDrawerMenu.this).getInt("notes", 0) + "");
                    vh.icon.setImageResource(R.drawable.ic_notes);
                    break;*/
                case ACTIVITY_SETTINGS:
                    vh.icon.setImageResource(R.drawable.ic_settings);
                    break;
            }
            return convertView;
        }

        class ViewHolder {
            ImageView icon;
            TextView menuName;
        }

        private void setViewHolder(View v) {
            ViewHolder vh = new ViewHolder();
            vh.icon = (ImageView) v.findViewById(R.id.imageview_itemicon);
            vh.menuName = (TextView) v.findViewById(R.id.textview_itemname);
            v.setTag(vh);
        }
    }

}
