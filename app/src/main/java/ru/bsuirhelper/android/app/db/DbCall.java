package ru.bsuirhelper.android.app.db;

import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public abstract class DbCall<T> implements Call<T> {

    public void enqueue(SingleSubscriber<T> callback) {
        Single<T> observable = Single.create(new Single.OnSubscribe<T>() {
            @Override
            public void call(SingleSubscriber<? super T> singleSubscriber) {
                try {
                    T t = execute();
                    singleSubscriber.onSuccess(t);
                } catch (Exception e) {
                    singleSubscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
        observable.subscribe(callback);
    }
}