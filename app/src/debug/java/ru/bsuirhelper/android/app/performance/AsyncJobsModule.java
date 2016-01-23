package ru.bsuirhelper.android.app.performance;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.bsuirhelper.android.app.performance.AsyncJobsObserver;

@Module
public class AsyncJobsModule {

    @Provides @NonNull @Singleton
    public AsyncJobsObserver provideAsyncJobsObserver() {
        return new AsyncJobsObserverImpl();
    }
}
