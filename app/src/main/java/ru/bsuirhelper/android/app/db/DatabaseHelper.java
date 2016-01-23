package ru.bsuirhelper.android.app.db;

/**
 * Created by Grishechko on 21.01.2016.
 */
public interface DatabaseHelper {
    Call<String> getTest();

    Call<Boolean> putTest();
}
