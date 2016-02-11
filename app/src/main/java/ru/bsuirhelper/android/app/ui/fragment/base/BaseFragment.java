package ru.bsuirhelper.android.app.ui.fragment.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.f2prateek.dart.Dart;

import butterknife.ButterKnife;
import ru.bsuirhelper.android.app.App;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.app.core.AndroidUtils;
import ru.bsuirhelper.android.app.ui.activity.base.BaseActivity;
import ru.bsuirhelper.android.app.ui.activity.base.NavigationController;
import ru.bsuirhelper.android.app.ui.activity.base.ToolbarController;

/**
 * Created by IlyaEremin on 14/01/15.
 */
public abstract class BaseFragment extends Fragment {
    protected NavigationController mNavigationController;
    protected ToolbarController mToolBarController;
    protected FragmentInfo fragmentInfo;
    private boolean mFirstAttach = true;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        if (fragmentInfo.isNeedArgumentInject()) {
            Dart.inject(this, getArguments());
        }
        if (savedState != null) {
            restoreState(savedState);
        } else {
            firstFragmentLaunch();
        }
        mNavigationController = activity();
        mToolBarController = activity();
    }

    /**
     * called in {@link #onCreate(Bundle)} when savedState is null
     */
    protected void firstFragmentLaunch() {
    }

    protected void restoreState(Bundle savedState) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentInfo = getFragmentInfo();
        if (mFirstAttach && fragmentInfo.isNeedDaggerInject()) {
            inject();
            mFirstAttach = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(fragmentInfo.getLayoutId(), container, false);

        ButterKnife.bind(this, v);
        Toolbar toolbar = ButterKnife.findById(v, R.id.toolbar);
        if (toolbar != null) {
            activity().setUpActionBar(toolbar);
        }
        return v;
    }

    abstract protected FragmentInfo getFragmentInfo();

    @Override
    public void onResume() {
        super.onResume();
        if (fragmentInfo.getTitleId() != R.string.dont_change) {
            activity().setToolbarTitle(fragmentInfo.getTitleId());
        }
        if (fragmentInfo.getHomeBtn() != HomeButtonAction.DONT_CHANGE) {
            activity().setHomeBtn(fragmentInfo.getHomeBtn());
        }
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (fragmentInfo.getHomeBtn() == HomeButtonAction.BACK) {
                AndroidUtils.hideKeyboard(getActivity());
                getActivity().onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public String trim(TextView textView) {
        return textView.getText().toString().trim();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(fragmentInfo.getActionBarMenuId(), menu);
    }

    public void inject() {

    }

    protected String textOf(TextView tv) {
        return tv.getText().toString();
    }

    protected boolean isEmpty(TextView tv) {
        return TextUtils.isEmpty(textOf(tv));
    }

    protected boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }

    protected int getColor(@ColorRes int colorRes) {
        return getResources().getColor(colorRes);
    }

    protected String[] getStringArray(@ArrayRes int arrayRes) {
        return getResources().getStringArray(arrayRes);
    }

    protected void closeCurrentFragment() {
        AndroidUtils.hideKeyboard(getActivity());
        getActivity().getFragmentManager().popBackStackImmediate();
    }

    public void finish() {
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    protected BaseActivity activity() {
        return (BaseActivity) getActivity();
    }

    protected Context context() {
        return getActivity();
    }

    public void startFragmentForResult(Fragment current, Fragment newFragment, int requestCode) {
        newFragment.setTargetFragment(current, requestCode);
        FragmentTransaction.with(newFragment).addToBackStack();
    }

    /**
     * @param data       passed in onActivityResult of target fragment
     * @param resultCode
     */
    public void setResult(Intent data, int resultCode) {
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, data);
        finish();
    }

    @NonNull
    private static final Handler MAIN_THREAD_HANDLER = new Handler(Looper.getMainLooper());

    protected void runOnUiThreadIfFragmentAlive(@NonNull Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper() && isFragmentAlive()) {
            runnable.run();
        } else {
            MAIN_THREAD_HANDLER.post(() -> {
                if (isFragmentAlive()) {
                    runnable.run();
                }
            });
        }
    }

    protected boolean isFragmentAlive() {
        return getActivity() != null && isAdded() && !isDetached() && getView() != null && !isRemoving();
    }

    @Override
    public void onDestroy() {
        App.get(getContext()).applicationComponent().leakCanaryProxy().watch(this);
        super.onDestroy();
    }

}
