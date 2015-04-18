package ru.bsuirhelper.android.core.cache;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by vladislav on 4/18/15.
 */
@Deprecated
public class CacheContentProvider extends ContentProvider {
    public static final String AUTHORITY = "ru.bsuirhelper.android.core.database.cache";

    private static final String SCHEDULE_PATH = "schedule";
    private static final int SCHEDULE = 1;
    private static final int SCHEDULE_ID = 2;
    public static final Uri SCHEDULE_URI = Uri.parse("content://" + AUTHORITY + "/" + SCHEDULE_PATH);

    private static final String NOTE_PATH = "note";
    private static final int NOTE = 3;
    private static final int NOTE_ID = 4;
    public static final Uri NOTE_URI = Uri.parse("content://" + AUTHORITY + "/" + NOTE_PATH);

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, SCHEDULE_PATH, SCHEDULE);
        uriMatcher.addURI(AUTHORITY, SCHEDULE_PATH + "/#", SCHEDULE_ID);
        uriMatcher.addURI(AUTHORITY, NOTE_PATH, NOTE);
        uriMatcher.addURI(AUTHORITY, NOTE_PATH + "/#", NOTE_ID);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
