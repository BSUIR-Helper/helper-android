package ru.bsuirhelper.android.app.ui.activity.base;

import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;

public interface ToolbarController {

        void setToolbarTitle(@StringRes int resTitle);

        void showToolbar(boolean animation);

        void hideToolbar(boolean animation);

        Toolbar getToolbar();
    }