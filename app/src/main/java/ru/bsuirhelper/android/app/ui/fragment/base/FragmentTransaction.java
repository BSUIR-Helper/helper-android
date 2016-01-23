package ru.bsuirhelper.android.app.ui.fragment.base;

import android.support.v4.app.Fragment;

import ru.bsuirhelper.android.R;


/**
 * Created by IlyaEremin on 14/01/15.
 */
public class FragmentTransaction {

    public static FragmentTransaction with(Fragment fragment) {
        return new FragmentTransaction(fragment);
    }

    public static FragmentTransaction withAnimAndBackstack(Fragment fragment) {
        return with(fragment).addToBackStack().withAnimation();
    }

    public FragmentTransaction withAnimation() {
        withAnimation(fragmentAnimation);
        return this;
    }

    public FragmentTransaction withAnimation(FragmentAnimation fragmentAnimation) {
        this.fragmentAnimation = fragmentAnimation;
        this.needAnimation = true;
        return this;
    }

    public FragmentTransaction addToBackStack() {
        this.addToBackStack = true;
        return this;
    }

    public FragmentTransaction clearBackStack() {
        this.clearBackStack = true;
        return this;
    }

    public FragmentTransaction closeDrawer() {
        this.closeNavigationDrawer = true;
        return this;
    }

    private FragmentTransaction(Fragment fragment) {
        this.fragment = fragment;
    }

    private final Fragment fragment;

    private FragmentAnimation fragmentAnimation = new FragmentAnimation(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);

    private boolean needAnimation, addToBackStack, clearBackStack, closeNavigationDrawer;


    public Fragment getFragment() {
        return fragment;
    }

    public boolean isNeedAnimation() {
        return needAnimation;
    }

    public boolean isAddToBackStack() {
        return addToBackStack;
    }

    public boolean isClearBackStack() {
        return clearBackStack;
    }

    public boolean isCloseNavigationDrawer() {
        return closeNavigationDrawer;
    }

    public FragmentAnimation getFragmentAnimation() {
        return fragmentAnimation;
    }
}
