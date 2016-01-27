package ru.bsuirhelper.android.app.db;

import ru.bsuirhelper.android.app.db.entities.StudentGroup;

/**
 * Created by Grishechko on 21.01.2016.
 */
public interface DatabaseHelper {
    DbCall<String> getTest();

    DbCall<Boolean> putTest();

    DbCall<Boolean> putStudentGroup(StudentGroup studentGroup);

    DbCall<StudentGroup> getStudentGroup();
}
