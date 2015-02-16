package ru.bsuirhelper.android.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;

public class RotationViewPager implements ViewPager.PageTransformer {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void transformPage(View view, float position) {
        view.setRotationY(position * -30);
    }
}