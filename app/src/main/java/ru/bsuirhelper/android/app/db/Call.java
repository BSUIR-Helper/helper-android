package ru.bsuirhelper.android.app.db;

import rx.SingleSubscriber;

/**
 * Created by Grishechko on 21.01.2016.
 */
public interface Call<T> {
    T execute();

    void enqueue(SingleSubscriber<T> callback);
}
