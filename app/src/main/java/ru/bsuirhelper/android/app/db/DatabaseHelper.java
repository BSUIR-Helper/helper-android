package ru.bsuirhelper.android.app.db;

import java.util.List;

import ru.bsuirhelper.android.app.db.entities.StudentGroup;

/**
 * Created by Grishechko on 21.01.2016.
 */
public interface DatabaseHelper {

    DbCall<Boolean> putStudentGroups(List<StudentGroup> studentGroups);


    DbCall<List<StudentGroup>> getStudentGroups();
}
