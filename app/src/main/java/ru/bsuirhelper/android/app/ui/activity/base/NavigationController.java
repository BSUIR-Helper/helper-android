package ru.bsuirhelper.android.app.ui.activity.base;

import ru.bsuirhelper.android.app.ui.fragment.base.FragmentTransaction;

public interface NavigationController {
    void switchFragment(FragmentTransaction transaction);

    void clearFullBackStack();
}