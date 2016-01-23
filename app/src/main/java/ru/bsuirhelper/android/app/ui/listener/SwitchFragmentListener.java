package ru.bsuirhelper.android.app.ui.listener;

import android.support.v4.app.Fragment;

/**
 * Created by vladislav on 4/21/15.
 */
public interface SwitchFragmentListener {
    void switchFragment(Fragment fragment, boolean add);

    void switchFragment(Fragment fragment, String tag, boolean add);

    void switchFragment(Fragment fragment, int res);
}
