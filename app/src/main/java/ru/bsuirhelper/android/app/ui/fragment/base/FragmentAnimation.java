package ru.bsuirhelper.android.app.ui.fragment.base;

import android.support.annotation.AnimRes;

/**
 * Created by Grishechko on 19.01.2016.
 */
public class FragmentAnimation {
    @AnimRes private int animEnter;
    @AnimRes private int animExit;
    @AnimRes private int animPopEnter;
    @AnimRes private int animPopExit;

    public FragmentAnimation(int animEnter, int animExit, int animPopEnter, int animPopExit) {
        this.animEnter = animEnter;
        this.animExit = animExit;
        this.animPopEnter = animPopEnter;
        this.animPopExit = animPopExit;
    }

    public int getAnimEnter() {
        return animEnter;
    }

    public void setAnimEnter(int animEnter) {
        this.animEnter = animEnter;
    }

    public int getAnimExit() {
        return animExit;
    }

    public void setAnimExit(int animExit) {
        this.animExit = animExit;
    }

    public int getAnimPopEnter() {
        return animPopEnter;
    }

    public void setAnimPopEnter(int animPopEnter) {
        this.animPopEnter = animPopEnter;
    }

    public int getAnimPopExit() {
        return animPopExit;
    }

    public void setAnimPopExit(int animPopExit) {
        this.animPopExit = animPopExit;
    }
}
