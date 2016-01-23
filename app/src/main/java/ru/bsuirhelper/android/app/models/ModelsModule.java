package ru.bsuirhelper.android.app.models;

import android.support.annotation.NonNull;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.bsuirhelper.android.app.App;

@Module
public class ModelsModule {

    @Provides @NonNull @Singleton
    public AnalyticsModel provideAnalyticsModel(@NonNull App app) {
        return new EmptyMetricaAnalytics();
    }

    static class EmptyMetricaAnalytics implements AnalyticsModel {

        @Override
        public void init() {
            //no op
        }

        @Override
        public void sendEvent(@NonNull String eventName) {
            //no op
        }

        @Override
        public void sendError(@NonNull String message, @NonNull Throwable error) {
            //no op
        }
    }
}
