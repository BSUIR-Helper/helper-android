package ru.bsuirhelper.android.app.ui.activity;

import android.animation.Animator;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.app.App;
import ru.bsuirhelper.android.app.api.RestApi;
import ru.bsuirhelper.android.app.api.entities.EmployeeList;
import ru.bsuirhelper.android.app.api.entities.ScheduleStudentGroupList;
import ru.bsuirhelper.android.app.api.entities.StudentGroupList;
import ru.bsuirhelper.android.app.db.DatabaseHelper;
import ru.bsuirhelper.android.app.db.entities.Schedule;
import ru.bsuirhelper.android.app.db.entities.ScheduleDay;
import ru.bsuirhelper.android.app.db.entities.StudentGroup;
import ru.bsuirhelper.android.app.ui.activity.base.BaseActivity;
import ru.bsuirhelper.android.app.ui.fragment.FragmentSchedule;
import ru.bsuirhelper.android.app.ui.fragment.base.FragmentTransaction;
import ru.bsuirhelper.android.app.ui.other.ViewModifier;
import timber.log.Timber;

import static ru.bsuirhelper.android.app.developer_settings.DeveloperSettingsModule.MAIN_ACTIVITY_VIEW_MODIFIER;

/**
 * Created by Grishechko on 19.01.2016.
 */
public class MainActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.main_drawer_layout) DrawerLayout mDrawerLayout;
    @Inject
    @Named(MAIN_ACTIVITY_VIEW_MODIFIER)
    ViewModifier viewModifier;

    @Inject
    RestApi api;

    @Inject
    DatabaseHelper dbHelper;

    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.get(this).applicationComponent().inject(this);
        setContentView(viewModifier.modify(getLayoutInflater().inflate(R.layout.activity_main, null)));
        /*api.employees().enqueue(new Callback<EmployeeList>() {
            @Override
            public void onResponse(Response<EmployeeList> response, Retrofit retrofit) {
                if (response.body() != null) {
                    Timber.d(response.body().toString());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Timber.e(t, "Error");
            }
        });
        api.studentGroups().enqueue(new Callback<StudentGroupList>() {
            @Override
            public void onResponse(Response<StudentGroupList> response, Retrofit retrofit) {
                if (response.body() != null) {
                    List<ru.bsuirhelper.android.app.api.entities.StudentGroup> apiEntities = response.body().getStudentGroups();
                    List<StudentGroup> studentGroups = new ArrayList<>();
                    for (ru.bsuirhelper.android.app.api.entities.StudentGroup studentGroup : apiEntities) {
                        studentGroups.add(new StudentGroup().setDataFrom(studentGroup));
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Timber.e(t, "Error");
            }
        });

      */
        api.schedulesStudentGroup(21019).enqueue(new Callback<ScheduleStudentGroupList>() {
            @Override
            public void onResponse(Response<ScheduleStudentGroupList> response, Retrofit retrofit) {
                StudentGroup studentGroup = new StudentGroup(21019l, "313801", 20017l, 3, 20082l);
                Schedule schedule = new Schedule();
                schedule.setStudentGroup(studentGroup);
                schedule.setDataFrom(response.body());
                schedule.setId(studentGroup.getId());
                dbHelper.putSchedule(schedule).execute();
            }

            @Override
            public void onFailure(Throwable t) {
                Timber.e(t, "Error");
            }
        });

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, getToolbar(), R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        actionBarDrawerToggle.setToolbarNavigationClickListener(v -> onBackPressed());
        if (savedInstanceState == null) {
            switchFragment(FragmentTransaction.with(new FragmentSchedule()).addToBackStack());
        }
    }

    @Override
    public void setToolbarTitle(@StringRes int resTitle) {
        if (mToolbar != null) {
            mToolbar.setTitle(getString(resTitle));
        }
    }

    @Override
    public void showToolbar(boolean animation) {
        if (mToolbar != null) {
            getToolbar().setVisibility(View.VISIBLE);
            if (animation) {
                getToolbar().animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
            }
        }
    }

    @Override
    public void hideToolbar(boolean animation) {
        if (mToolbar != null) {
            final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
                    new int[]{android.R.attr.actionBarSize});
            int mActionBarSize = (int) styledAttributes.getDimension(0, 0);
            styledAttributes.recycle();
            if (animation) {
                getToolbar().animate().translationY(-mActionBarSize).setInterpolator(
                        new AccelerateInterpolator(2)).setDuration(100).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        getToolbar().setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            } else {
                getToolbar().setVisibility(View.GONE);
            }
        }
    }

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }
}
