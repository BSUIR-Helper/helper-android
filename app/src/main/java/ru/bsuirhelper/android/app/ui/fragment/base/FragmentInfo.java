package ru.bsuirhelper.android.app.ui.fragment.base;

import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.StringRes;

import ru.bsuirhelper.android.R;


public class FragmentInfo {
    @StringRes private int title;
    @LayoutRes private final int layoutId;
    @MenuRes private int actionBarMenuId = R.menu.empty;
    @StringRes private int titleId = R.string.dont_change;

    public FragmentInfo(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
    }

    private HomeButtonAction homeBtn = HomeButtonAction.DONT_CHANGE;
    private boolean isNeedLeftMenu;
    private boolean isNeedDaggerInject;
    private boolean showActionBar = true;
    private boolean needArgumentInject = false;

    public FragmentInfo makeArgumentInject() {
        this.needArgumentInject = true;
        return this;
    }

    public FragmentInfo makeDependencyInject() {
        isNeedDaggerInject = true;
        return this;
    }

    public FragmentInfo leftMenuAvailable() {
        isNeedLeftMenu = true;
        return this;
    }

    public FragmentInfo doNotAddActionBar() {
        showActionBar = false;
        return this;
    }


    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public int getActionBarMenuId() {
        return actionBarMenuId;
    }

    public void setActionBarMenuId(int actionBarMenuId) {
        this.actionBarMenuId = actionBarMenuId;
    }

    public int getTitleId() {
        return titleId;
    }

    public void setTitleId(int titleId) {
        this.titleId = titleId;
    }

    public HomeButtonAction getHomeBtn() {
        return homeBtn;
    }

    public void setHomeBtn(HomeButtonAction homeBtn) {
        this.homeBtn = homeBtn;
    }

    public boolean isNeedLeftMenu() {
        return isNeedLeftMenu;
    }

    public void setNeedLeftMenu(boolean needLeftMenu) {
        isNeedLeftMenu = needLeftMenu;
    }

    public boolean isNeedDaggerInject() {
        return isNeedDaggerInject;
    }

    public void setNeedDaggerInject(boolean needDaggerInject) {
        isNeedDaggerInject = needDaggerInject;
    }

    public boolean isShowActionBar() {
        return showActionBar;
    }

    public void setShowActionBar(boolean showActionBar) {
        this.showActionBar = showActionBar;
    }

    public boolean isNeedArgumentInject() {
        return needArgumentInject;
    }

    public void setNeedArgumentInject(boolean needArgumentInject) {
        this.needArgumentInject = needArgumentInject;
    }
}
