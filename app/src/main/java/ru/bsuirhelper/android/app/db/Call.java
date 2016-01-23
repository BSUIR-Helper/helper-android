package ru.bsuirhelper.android.app.db;

import rx.Single;
import rx.SingleSubscriber;
import rx.schedulers.Schedulers;

/**
 * Created by Grishechko on 21.01.2016.
 */
public interface Call<T> {
    abstract T execute();

    void enqueue(SingleSubscriber<T> callback);
}
