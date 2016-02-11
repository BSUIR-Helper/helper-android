package ru.bsuirhelper.android.app.ui.activity.base;


import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.ButterKnife;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.app.core.AndroidUtils;
import ru.bsuirhelper.android.app.ui.fragment.base.FragmentAnimation;
import ru.bsuirhelper.android.app.ui.fragment.base.FragmentTransaction;
import ru.bsuirhelper.android.app.ui.fragment.base.HomeButtonAction;

public abstract class BaseActivity extends AppCompatActivity implements ToolbarController, NavigationController {

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    private void clearFragmentBackStack() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    protected HomeButtonAction currentHomeBtnMode;

    public void setHomeBtn(HomeButtonAction event) {
        switch (event) {
            case BACK:
                this.currentHomeBtnMode = event;
                break;
            case NONE:
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                break;
        }
    }

    public void setUpActionBar(Toolbar toolbar) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void switchFragment(FragmentTransaction transaction) {
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (transaction.isClearBackStack()) {
            clearFragmentBackStack();
        }
        if (transaction.isNeedAnimation()) {
            FragmentAnimation animation = transaction.getFragmentAnimation();
            ft.setCustomAnimations(animation.getAnimEnter(), animation.getAnimExit(), animation.getAnimPopEnter(), animation.getAnimPopExit());
        }
        if (transaction.isAddToBackStack()) {
            ft.addToBackStack(null);
        }
        ft.replace(R.id.fl_main_activity_container, transaction.getFragment());
        ft.commitAllowingStateLoss();
        AndroidUtils.hideKeyboard(this);
    }

    @Override
    public void clearFullBackStack() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
