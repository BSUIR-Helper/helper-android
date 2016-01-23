package ru.bsuirhelper.android.app.developer_settings;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import ru.bsuirhelper.android.app.App;

public class LeakCanaryProxyImpl implements LeakCanaryProxy {

    @NonNull
    private final App app;

    @Nullable
    private RefWatcher refWatcher;

    public LeakCanaryProxyImpl(@NonNull App app) {
        this.app = app;
    }

    @Override
    public void init() {
        refWatcher = LeakCanary.install(app);
    }

    @Override
    public void watch(@NonNull Object object) {
        if (refWatcher != null) {
            refWatcher.watch(object);
        }
    }
}
