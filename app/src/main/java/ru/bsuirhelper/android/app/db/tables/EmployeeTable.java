package ru.bsuirhelper.android.app.db.tables;

import android.support.annotation.NonNull;

/**
 * Created by Grishechko on 23.01.2016.
 */
public class EmployeeTable {

    @NonNull
    public static final String TABLE = "employee_table";

    @NonNull
    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_FIRSTNAME = "firstname";

    @NonNull
    public static final String COLUMN_LASTNAME = "lastname";

    @NonNull
    public static final String COLUMN_MIDDLENAME = "middlename";

    @NonNull
    public static final String COLUMN_RANK = "rank";

    @NonNull
    public static final String COLUMN_ACADEMIC_DEPARTMENT = "academic_department";

    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_FIRSTNAME + " TEXT,"
                + COLUMN_LASTNAME + " TEXT,"
                + COLUMN_MIDDLENAME + " TEXT,"
                + COLUMN_RANK + " TEXT,"
                + COLUMN_ACADEMIC_DEPARTMENT + " TEXT,"
                + ");";
    }
}
