package ru.bsuirhelper.android.app;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import ru.bsuirhelper.android.BuildConfig;
import ru.bsuirhelper.android.app.api.ApiModule;

/**
 * Created by vladislav on 4/18/15.
 */
public class App extends Application{
    @SuppressWarnings("NullableProblems")
    // Initialized in onCreate. But be careful if you have ContentProviders
    // -> their onCreate may be called before app.onCreate()
    // -> move initialization to attachBaseContext().
    @NonNull
    private ApplicationComponent applicationComponent;

    public static volatile Handler applicationHandler;
    public static volatile Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        if(!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        applicationHandler = new Handler(getApplicationContext().getMainLooper());
        applicationContext = getApplicationContext();
        applicationComponent = prepareApplicationComponent().build();
    }

    @NonNull
    protected DaggerApplicationComponent.Builder prepareApplicationComponent() {
        return DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                // This url may be changed dynamically for tests! See ChangeableBaseUrl.
                .apiModule(new ApiModule());
    }

    @NonNull
    public ApplicationComponent applicationComponent() {
        return applicationComponent;
    }

    @NonNull
    public static App get(@NonNull Context context) {
        return (App) context.getApplicationContext();
    }
}
