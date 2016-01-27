package ru.bsuirhelper.android.app;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Grishechko on 20.01.2016.
 */
@Module
public class ApplicationModule {

    public static final String MAIN_THREAD_HANDLER = "main_thread_handler";

    @NonNull
    private final App app;

    public ApplicationModule(@NonNull App app) {
        this.app = app;
    }

    @Provides
    @NonNull
    @Singleton
    public App provideApp() {
        return app;
    }

    @Provides
    @NonNull
    @Singleton
    public Context provideContext(@NonNull App app) {
        return app.getApplicationContext();
    }

    @Provides
    @NonNull
    @Singleton
    public ObjectMapper provideObjectMapper() {
        return new ObjectMapper();
    }

    @Provides
    @NonNull
    @Named(MAIN_THREAD_HANDLER)
    @Singleton
    public Handler provideMainThreadHandler() {
        return new Handler(Looper.getMainLooper());
    }

    @Provides
    @NonNull
    @Singleton
    public Picasso providePicasso(@NonNull App app, @NonNull OkHttpClient okHttpClient) {
        return new Picasso.Builder(app)
                .downloader(new OkHttpDownloader(okHttpClient))
                .build();
    }

    @Provides
    @NonNull
    @Singleton
    public Gson provideGson() {
        return new Gson();
    }
}
