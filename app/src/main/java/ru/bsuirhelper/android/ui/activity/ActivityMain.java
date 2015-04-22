package ru.bsuirhelper.android.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.ApplicationSettings;
import ru.bsuirhelper.android.core.cache.CacheHelper;
import ru.bsuirhelper.android.core.cache.ScheduleManager;
import ru.bsuirhelper.android.core.models.StudentGroup;
import ru.bsuirhelper.android.ui.activity.base.ActivityBase;
import ru.bsuirhelper.android.ui.adapter.DrawerMenuAdapter;
import ru.bsuirhelper.android.ui.adapter.SpinnerGroupsAdapter;
import ru.bsuirhelper.android.ui.asynctask.DownloadScheduleTask;
import ru.bsuirhelper.android.ui.dialog.DialogAddGroup;
import ru.bsuirhelper.android.ui.dialog.DialogEditGroupName;
import ru.bsuirhelper.android.ui.fragment.FragmentNoGroups;
import ru.bsuirhelper.android.ui.fragment.FragmentSchedule;
import ru.bsuirhelper.android.ui.listener.AsyncTaskListener;
import ru.bsuirhelper.android.ui.listener.OnDeleteScheduleListener;
import ru.bsuirhelper.android.ui.listener.OnDialogEditGroupNameComplete;
import ru.bsuirhelper.android.ui.listener.OnEditScheduleListener;

/**
 * Created by Влад on 29.10.13.
 */
public class ActivityMain extends ActivityBase implements AsyncTaskListener, OnDialogEditGroupNameComplete {
    private boolean isScheduleClicked;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar mActionBar;
    private String[] mMenuItems;
    private Toolbar mToolbar;
    private SpinnerGroupsAdapter mGroupsAdapter;
    private Spinner mSpinnerGroups;
    private String mTitle;
    private String mSubTitle;
    private boolean isVisibleMenu = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawerlayout);
        mMenuItems = getResources().getStringArray(R.array.menu_items);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        drawerInitialize();
        actionBarInitialize();
        String defaultGroup = ApplicationSettings.getInstance(this).getString(ApplicationSettings.ACTIVE_STUDENTGROUP, null);
        if (defaultGroup != null) {
            StudentGroup studentGroup = CacheHelper.StudentGroups.getById(this, Long.parseLong(defaultGroup));
            switchFragment(FragmentSchedule.newInstance(studentGroup), FragmentSchedule.TAG_FRAGMENT, false, R.id.content_frame);
        } else {
            switchFragment(FragmentNoGroups.newInstance(), R.id.content_frame);
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
        ) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                try {
                    mTitle = getSupportActionBar().getTitle().toString();
                    mSubTitle = getSupportActionBar().getSubtitle().toString();
                    isVisibleMenu = false;
                    invalidateOptionsMenu();
                    getSupportActionBar().setTitle(R.string.app_name);
                    getSupportActionBar().setSubtitle("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(mTitle);
                getSupportActionBar().setSubtitle(mSubTitle);
                isVisibleMenu = true;
                invalidateOptionsMenu();
                if (isScheduleClicked) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String defaultGroup = ApplicationSettings.getInstance(getApplicationContext()).getString(ApplicationSettings.ACTIVE_STUDENTGROUP, null);
                            StudentGroup studentGroup = CacheHelper.StudentGroups.getById(getApplicationContext(), Long.parseLong(defaultGroup));
                            switchFragment(FragmentSchedule.newInstance(studentGroup), FragmentSchedule.TAG_FRAGMENT, false, R.id.content_frame);
                            isScheduleClicked = false;
                        }
                    }, 0);
                }
            }

        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
        DrawerMenuAdapter drawerAdapter = new DrawerMenuAdapter(this, mMenuItems);
        View view = LayoutInflater.from(this).inflate(R.layout.header_drawermenu, null);
        mSpinnerGroups = (Spinner) view.findViewById(R.id.sp_schedules);
        mGroupsAdapter = new SpinnerGroupsAdapter(this, ScheduleManager.getGroups(this));
        mGroupsAdapter.setOnDeleteScheduleListener(new OnDeleteScheduleListener() {
            @Override
            public void onDeleteSchedule(StudentGroup studentGroup) {
                showDeleteAlertDialog(studentGroup);
            }
        });
        mGroupsAdapter.setOnAddScheduleClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddGroupDialog();
            }
        });
        mGroupsAdapter.setOnEditScheduleListener(new OnEditScheduleListener() {
            @Override
            public void onEditScheduleListener(StudentGroup studentGroup) {
                showEditGroupDialog(studentGroup);
            }
        });
        mSpinnerGroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSpinnerGroups.getTag() != null && ((Integer) mSpinnerGroups.getTag()) == position) {
                    mSpinnerGroups.setTag(-1);
                    return;
                }
                if (position != mGroupsAdapter.getCount() - 1) {
                    StudentGroup studentGroup = (StudentGroup) mGroupsAdapter.getItem(position);
                    ApplicationSettings.getInstance(getApplicationContext()).
                            setActiveGroup(String.valueOf(studentGroup.getId()));
                    isScheduleClicked = true;
                    closeDrawerMenu();
                }
                closeDrawerMenu();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpinnerGroups.setAdapter(mGroupsAdapter);
        mDrawerList.addHeaderView(view);
        mDrawerList.setAdapter(drawerAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mActionBar.setSubtitle(null);
                Intent intent = new Intent();
                switch (position) {
                    case DrawerMenuAdapter.SCHEDULE_FRAGMENT:
                        if (mGroupsAdapter.getCount() > 1) {
                            isScheduleClicked = true;
                            mActionBar.setTitle(FragmentNoGroups.TITLE);
                        }
                        break;
                    case DrawerMenuAdapter.ACTIVITY_SETTINGS:
                        startActivity(intent.setClass(getApplicationContext(), ActivitySettings.class));
                        return;
                }
                closeDrawerMenu();
            }
        });
    }

    //TODO move text to locoalize strings
    private void showDeleteAlertDialog(final StudentGroup studentGroup) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Вы уверены?")
                .setMessage("Удалить расписание " + studentGroup.getGroupName())
                .setNegativeButton("Отмена", null)
                .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String studentGroupId = ApplicationSettings.getInstance(getApplicationContext()).getActiveGroup();
                        ScheduleManager.deleteSchedule(getApplicationContext(), studentGroup.getId());
                        List<StudentGroup> studentGroupList = ScheduleManager.getGroups(getApplicationContext());
                        if (studentGroupList.size() > 0) {
                            if (String.valueOf(studentGroup.getId()).equals(studentGroupId)) {
                                StudentGroup studentGroupActive = studentGroupList.get(studentGroupList.size() - 1);
                                ApplicationSettings.getInstance(getApplicationContext()).setActiveGroup(String.valueOf(studentGroupActive.getId()));
                            }
                        } else {
                            switchFragment(FragmentNoGroups.newInstance(), R.id.content_frame);
                            ApplicationSettings.getInstance(getApplicationContext()).setActiveGroup(null);
                        }
                        updateSpinner(studentGroupList);
                    }
                }).show();
    }

    private void showAddGroupDialog() {
        new DialogAddGroup().show(getSupportFragmentManager(), null);
    }

    private String preEditNameOfGroup;
    private void showEditGroupDialog(StudentGroup studentGroup) {
        DialogEditGroupName.newInstance(studentGroup).show(getSupportFragmentManager(), null);
        preEditNameOfGroup = studentGroup.getGroupName();
    }

    private void updateSpinner(List<StudentGroup> studentGroupList) {
        mGroupsAdapter.setGroups(studentGroupList);
        mSpinnerGroups.setSelection(mGroupsAdapter.indexOfActiveGroup());
        mSpinnerGroups.setTag(mGroupsAdapter.indexOfActiveGroup());
        /*if(studentGroupList.size() == 0) {
            Logger.i("Disabled spinner");
            mSpinnerGroups.setClickable(false);
            mSpinnerGroups.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        showAddGroupDialog();
                    }
                    return false;
                }
            });
        } else {
            mSpinnerGroups.setClickable(true);
            mSpinnerGroups.setOnTouchListener(null);
        }*/
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setVisible(isVisibleMenu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void openDrawerMenu() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    protected void closeDrawerMenu() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(findViewById(R.id.navigation_drawer));
        }
    }

    //TODO Вынести в строковые ресурсы
    @Override
    public void onPostExecute(DownloadScheduleTask.Status status) {
        updateSpinner(ScheduleManager.getGroups(getApplicationContext()));
        if (status == DownloadScheduleTask.Status.OK) {
            List<StudentGroup> groups = ScheduleManager.getGroups(getApplicationContext());
            if (mGroupsAdapter.getCount() == 2) {
                isScheduleClicked = true;
                mSpinnerGroups.setSelection(0);
                closeDrawerMenu();
                ApplicationSettings.getInstance(getApplicationContext()).
                        setActiveGroup(String.valueOf(groups.get(0).getId()));
            } else if (mGroupsAdapter.getCount() - 1 < groups.size()) {
                Toast.makeText(this, "Расписание добавлено", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Расписание обновлено", Toast.LENGTH_LONG).show();
            }
        } else if (status == DownloadScheduleTask.Status.NOT_EXISTS) {
            Toast.makeText(this, "Такой группы не существует", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Произошла ошибка", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDialogEditComplete(String groupName) {
        updateSpinner(ScheduleManager.getGroups(getApplicationContext()));
        if(preEditNameOfGroup.equals(mTitle)) {
            mTitle = groupName;
        }
    }
}
