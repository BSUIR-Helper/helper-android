package ru.bsuirhelper.android.core.cache;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

/**
 * Created by vladislav on 4/18/15.
 */

public class CacheContentProvider extends ContentProvider {
    public static final String AUTHORITY = "ru.bsuirhelper.android.core.database.cache";

    private static final String LESSON_PATH = "schedule";
    private static final int LESSON = 1;
    private static final int LESSON_ID = 2;
    public static final Uri LESSON_URI = Uri.parse("content://" + AUTHORITY + "/" + LESSON_PATH);

    private static final String NOTE_PATH = "note";
    private static final int NOTE = 3;
    private static final int NOTE_ID = 4;
    public static final Uri NOTE_URI = Uri.parse("content://" + AUTHORITY + "/" + NOTE_PATH);


    private static final String STUDENTGROUP_PATH = "studentgroup";
    private static final int STUDENTGROUP = 5;
    private static final int STUDENTGROUP_ID = 6;
    public static final Uri STUDENTGROUP_URI = Uri.parse("content://" + AUTHORITY + "/" + STUDENTGROUP_PATH);

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, LESSON_PATH, LESSON);
        uriMatcher.addURI(AUTHORITY, LESSON_PATH + "/#", LESSON_ID);
        uriMatcher.addURI(AUTHORITY, NOTE_PATH, NOTE);
        uriMatcher.addURI(AUTHORITY, NOTE_PATH + "/#", NOTE_ID);
        uriMatcher.addURI(AUTHORITY, STUDENTGROUP_PATH, STUDENTGROUP);
        uriMatcher.addURI(AUTHORITY, STUDENTGROUP_PATH + "/#", STUDENTGROUP_ID);
    }

    CacheHelper cacheHelper;
    SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        cacheHelper = new CacheHelper(getContext(), CacheHelper.DB_NAME, null, CacheHelper.DB_VERSION);
        database = cacheHelper.getWritableDatabase();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String table = null;
        switch (uriMatcher.match(uri)) {
            case LESSON:
                table = CacheHelper.Lessons.TABLE_NAME;
                break;
            case LESSON_ID:
                table = CacheHelper.Lessons.TABLE_NAME;
                selection = CacheHelper.Lessons._ID + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                break;
            case STUDENTGROUP:
                table = CacheHelper.StudentGroups.TABLE_NAME;
                break;
            case STUDENTGROUP_ID:
                table = CacheHelper.StudentGroups.TABLE_NAME;
                selection = CacheHelper.StudentGroups._ID + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                break;
        }
        Cursor cursor;
        if (table != null) {
            cursor = database.query(table, projection, selection, selectionArgs, null, null, sortOrder);
        } else {
            return null;
        }
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri uriWithId = null;
        Uri currentUri = null;
        long id = -1;
        switch (uriMatcher.match(uri)) {
            case LESSON:
                id = database.replace(CacheHelper.Lessons.TABLE_NAME, null, values);
                if (id >= 0) {
                    uriWithId = ContentUris.withAppendedId(LESSON_URI, id);
                    currentUri = LESSON_URI;
                }
                break;
            case STUDENTGROUP:
                id = database.replace(CacheHelper.StudentGroups.TABLE_NAME, null, values);
                if (id >= 0) {
                    uriWithId = ContentUris.withAppendedId(STUDENTGROUP_URI, id);
                    currentUri = STUDENTGROUP_URI;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        if (uriWithId != null) {
            getContext().getContentResolver().notifyChange(uriWithId, null);
            getContext().getContentResolver().notifyChange(currentUri, null);
        }
        return uriWithId;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted = 0;
        String id;
        switch (uriMatcher.match(uri)) {
            case LESSON:
                rowsDeleted = database.delete(CacheHelper.Lessons.TABLE_NAME, selection, selectionArgs);
                break;
            case LESSON_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(id)) {
                    rowsDeleted = database.delete(CacheHelper.Lessons.TABLE_NAME, CacheHelper.Lessons._ID +
                            " " + id, selectionArgs);
                } else {
                    rowsDeleted = database.delete(CacheHelper.Lessons.TABLE_NAME, CacheHelper.Lessons._ID +
                            " " + id, selectionArgs);
                }
                break;
            case STUDENTGROUP:
                rowsDeleted = database.delete(CacheHelper.StudentGroups.TABLE_NAME, selection, selectionArgs);
                break;
            case STUDENTGROUP_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(id)) {
                    rowsDeleted = database.delete(CacheHelper.StudentGroups.TABLE_NAME, CacheHelper.StudentGroups._ID +
                            " " + id, selectionArgs);
                } else {
                    rowsDeleted = database.delete(CacheHelper.StudentGroups.TABLE_NAME, CacheHelper.StudentGroups._ID +
                            " " + id, selectionArgs);
                }
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated = 0;
        String id;
        switch (uriMatcher.match(uri)) {
            case LESSON:
                rowsUpdated = database.update(CacheHelper.Lessons.TABLE_NAME, values, selection, selectionArgs);
                break;
            case LESSON_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = database.update(CacheHelper.Lessons.TABLE_NAME, values,
                            CacheHelper.Lessons._ID + " = " + id, null);
                } else {
                    rowsUpdated = database.update(CacheHelper.Lessons.TABLE_NAME, values,
                            CacheHelper.Lessons._ID + " = " + id + " and " + selection, null);
                }
                break;
            case STUDENTGROUP:
                rowsUpdated = database.update(CacheHelper.Lessons.TABLE_NAME, values, selection, selectionArgs);
                break;
            case STUDENTGROUP_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = database.update(CacheHelper.StudentGroups.TABLE_NAME, values,
                            CacheHelper.StudentGroups._ID + " = " + id, null);
                } else {
                    rowsUpdated = database.update(CacheHelper.StudentGroups.TABLE_NAME, values,
                            CacheHelper.StudentGroups._ID + " = " + id + " and " + selection, null);
                }
                break;

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        int numInserted = 0;
        String table = null;
        switch (uriMatcher.match(uri)) {
            case LESSON:
            case LESSON_ID:
                table = CacheHelper.Lessons.TABLE_NAME;
                break;
            case STUDENTGROUP:
            case STUDENTGROUP_ID:
                table = CacheHelper.StudentGroups.TABLE_NAME;
                break;
        }
        if (table != null) {
            database.beginTransaction();
            try {
                for (ContentValues cv : values) {
                    long newId = database.replace(table, null, cv);
                    Logger.i(newId + "" + "\n" + cv);
                    if(newId > 0) {
                        numInserted++;
                    }
                }
                Logger.i("Table" + table + "Inserted:" + numInserted + "\n" + "Size:" + values.length);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                database.endTransaction();
            }
        }
        return numInserted;
    }
}
