package ru.bsuirhelper.android.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.analytics.tracking.android.EasyTracker;
import ru.bsuirhelper.android.ApplicationSettings;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.ui.notes.FragmentNotes;
import ru.bsuirhelper.android.ui.schedule.FragmentManagerGroups;
import ru.bsuirhelper.android.ui.schedule.FragmentSchedule;

/**
 * Created by Влад on 29.10.13.
 */
public class ActivityDrawerMenu extends ActionBarActivity {
    public static final String LOG_TAG = "BSUIR_DEBUG";
    private final int SCHEDULE_FRAGMENT = 0;
    private final int NOTE_FRAGMENT = 1;
    private final int ACTIVITY_SETTINGS = 2;
    private DrawerLayout mDrawerLayout;
    private DrawerArrayAdapter mDrawerAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private ActionBar mActionBar;
    private Runnable mPendingRunnable;
    private Spinner mSpinnerGroups;
    private Handler mHandler;
    private final String[] mMenuItems = new String[]{"Расписание", "Заметки", "Настройки"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.drawerlayout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {
            @Override
            public void onDrawerClosed(View view) {
                ActivityCompat.invalidateOptionsMenu(ActivityDrawerMenu.this);
                // If mPendingRunnable is not null, then add to the message queue
                if (mPendingRunnable != null) {
                    mHandler.post(mPendingRunnable);
                    mPendingRunnable = null;
                }
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerAdapter = new DrawerArrayAdapter(this, mMenuItems);
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                closeDrawerMenu();
                selectItem(position);
            }
        });

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        FragmentManager fm = getSupportFragmentManager();

        mHandler = new Handler(); //For smooth closing drawer

        String defaultGroup = ApplicationSettings.getInstance(this).getString("defaultgroup", null);
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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);

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

    protected void updateDrawerMenu() {
        mDrawerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("selected", true);
    }

    private void selectItem(final int position) {
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = null;
        Intent intent = new Intent();
        mActionBar.setSubtitle(null);
        switch (position) {
            case SCHEDULE_FRAGMENT:
                fragment = new FragmentManagerGroups();
                mActionBar.setTitle(FragmentManagerGroups.TITLE);
                break;
            case NOTE_FRAGMENT:
                fragment = new FragmentNotes();
                mActionBar.setTitle(FragmentNotes.TITLE);
                break;
            case ACTIVITY_SETTINGS:
                startActivity(intent.setClass(getApplicationContext(), ActivitySettings.class));
                return;
        }
        final Fragment finalFragment = fragment;
        mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // Insert the fragment by replacing any existing fragment
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, finalFragment)
                        .commit();

            }

        };
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        closeDrawerMenu();
    }

    protected void openDrawerMenu() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    protected void closeDrawerMenu() {
        mDrawerLayout.closeDrawer(findViewById(R.id.navigation_drawer));
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
            TextView counterOfNotes = (TextView) convertView.findViewById(R.id.textview_counternotes);
            vh.menuName.setText(getItem(position));
            switch (position) {
                case SCHEDULE_FRAGMENT:
                    vh.icon.setImageResource(R.drawable.ic_calendar);
                    counterOfNotes.setVisibility(View.INVISIBLE);
                    break;
                case NOTE_FRAGMENT:
                    counterOfNotes.setVisibility(View.VISIBLE);
                    counterOfNotes.setText(ApplicationSettings.getInstance(ActivityDrawerMenu.this).getInt("notes", 0) + "");
                    vh.icon.setImageResource(R.drawable.ic_notes);
                    break;
                case ACTIVITY_SETTINGS:
                    vh.icon.setImageResource(R.drawable.ic_settings);
                    counterOfNotes.setVisibility(View.INVISIBLE);
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
