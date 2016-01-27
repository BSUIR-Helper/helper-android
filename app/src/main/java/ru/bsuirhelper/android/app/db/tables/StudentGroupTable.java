package ru.bsuirhelper.android.app.db.tables;

import android.support.annotation.NonNull;

/**
 * Created by Grishechko on 22.01.2016.
 */
public class StudentGroupTable {

    @NonNull
    public static final String TABLE = "student_group";

    @NonNull
    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_NAME = "name";

    @NonNull
    public static final String COLUMN_FACULTY_ID = "faculty_id";

    @NonNull
    public static final String COLUMN_COURSE = "course";

    @NonNull
    public static final String COLUMN_SPECIALITY_DEPARTMENT_EDUCATION = "speciality_department_education";

    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_NAME + " TEXT NOT NULL,"
                + COLUMN_FACULTY_ID + " INTEGER,"
                + COLUMN_COURSE + " INTEGER,"
                + COLUMN_SPECIALITY_DEPARTMENT_EDUCATION + " INTEGER,"
                + ");";
    }
}
