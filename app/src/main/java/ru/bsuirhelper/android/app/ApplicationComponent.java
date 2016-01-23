package ru.bsuirhelper.android.app;

import ru.bsuirhelper.android.app.developer_settings.DeveloperSettingsComponent;
import ru.bsuirhelper.android.app.developer_settings.DeveloperSettingsModule;
import ru.bsuirhelper.android.app.developer_settings.LeakCanaryProxy;
import ru.bsuirhelper.android.app.performance.AsyncJobsModule;
import android.support.annotation.NonNull;


import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Singleton;

import dagger.Component;
import ru.bsuirhelper.android.app.api.ApiModule;
import ru.bsuirhelper.android.app.api.AppRestApi;
import ru.bsuirhelper.android.app.api.ChangeableBaseUrl;
import ru.bsuirhelper.android.app.models.ModelsModule;
import ru.bsuirhelper.android.app.network.NetworkModule;
import ru.bsuirhelper.android.app.performance.AsyncJobsObserver;
import ru.bsuirhelper.android.app.ui.activity.MainActivity;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        NetworkModule.class,
        ApiModule.class,
        ModelsModule.class,
        AsyncJobsModule.class,
        DeveloperSettingsModule.class,
})
public interface ApplicationComponent {


    // Provide ObjectMapper from the real app to the tests without need in injection to the test.
    @NonNull
    ObjectMapper objectMapper();

    // Provide QualityMattersRestApi from the real app to the tests without need in injection to the test.
    @NonNull
    AppRestApi qualityMattersApi();

    @NonNull
    ChangeableBaseUrl changeableBaseUrl();

    // Provide AsyncJobObserver from the real app to the tests without need in injection to the test.
    @NonNull
    AsyncJobsObserver asyncJobsObserver();

    // Provide LeakCanary without injection to leave
    @NonNull
    LeakCanaryProxy leakCanaryProxy();

    @NonNull
    DeveloperSettingsComponent plusDeveloperSettingsComponent();

    void inject(@NonNull App app);

    void inject(@NonNull MainActivity mainActivity);
}
