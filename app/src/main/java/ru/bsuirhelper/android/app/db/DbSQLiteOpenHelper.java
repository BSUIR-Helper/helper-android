package ru.bsuirhelper.android.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;


import nl.qbusict.cupboard.Cupboard;


public class DbSQLiteOpenHelper extends SQLiteOpenHelper {

    private static String LOG_TAG = DbSQLiteOpenHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "data.db";
    private static final int DATABASE_VERSION = 1;
    private static final int LAST_DATABASE_NUKE_VERSION = 0;
    private final Cupboard cupboard;

    public DbSQLiteOpenHelper(Context context, Cupboard cupboard) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.cupboard = cupboard;
    }

    private boolean mIsClosed;

    @Override
    public synchronized void close() {
        mIsClosed = true;
        super.close();
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        cupboard.withDatabase(db).createTables();
    }

    @Override
    public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < LAST_DATABASE_NUKE_VERSION) {
            Log.d(LOG_TAG, "Nuking Database. Old Version: " + oldVersion);
            cupboard.withDatabase(db).dropAllTables();
            onCreate(db);
        } else {
            // this will upgrade tables, adding columns and new tables.
            // Note that existing columns will not be converted
            cupboard.withDatabase(db).upgradeTables();
        }
    }

    public boolean isClosed() {
        return mIsClosed;
    }
}