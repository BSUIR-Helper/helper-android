package ru.bsuirhelper.android.app.api;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import retrofit.SimpleXmlConverterFactory;
import ru.bsuirhelper.android.BuildConfig;

/**
 * Created by Grishechko on 20.01.2016.
 */
@Module
public class ApiModule {
    public static final String API_URL = "http://www.bsuir.by/schedule/rest/";
    @NonNull
    private final ChangeableBaseUrl changeableBaseUrl;

    public ApiModule() {
        changeableBaseUrl = new ChangeableBaseUrl(API_URL);
    }

    @Provides
    @NonNull @Singleton
    public ChangeableBaseUrl provideChangeableBaseUrl() {
        return changeableBaseUrl;
    }

    @Provides @NonNull @Singleton
    public AppRestApi provideRestApi(@NonNull OkHttpClient okHttpClient, @NonNull ObjectMapper objectMapper, @NonNull ChangeableBaseUrl changeableBaseUrl) {
        final Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(changeableBaseUrl)
                .client(okHttpClient)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());

        // Fail early: check Retrofit configuration at creation time
        if (BuildConfig.DEBUG) {
            builder.validateEagerly();
        }

        return builder.build().create(AppRestApi.class);
    }
}
