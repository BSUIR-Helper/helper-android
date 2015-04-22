package ru.bsuirhelper.android.ui.activity.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.ui.listener.SwitchFragmentListener;

/**
 * Created by vladislav on 4/21/15.
 */
public class ActivityBase extends AppCompatActivity implements SwitchFragmentListener {
    public void switchFragment(Fragment fragment, boolean isAdd) {
        switchFragment(fragment, null, isAdd, R.id.content_frame);
    }

    public void switchFragment(Fragment fragment, String tag, boolean isAdd) {
        switchFragment(fragment, tag, isAdd, R.id.content_frame);
    }

    @Override
    public void switchFragment(Fragment fragment, int res) {
        switchFragment(fragment, null, false, res);
    }

    protected void switchFragment(Fragment fragment, String tag, boolean isAdd, int res) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tr = fm.beginTransaction();
        if (!isAdd) {
            try {
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        tr.replace(res, fragment, tag);
        if (isAdd) {
            tr.addToBackStack(tag);
        }
        tr.commitAllowingStateLoss();
    }
}
