package ru.bsuirhelper.android.app.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import javax.inject.Inject;

import nl.qbusict.cupboard.Cupboard;
import ru.bsuirhelper.android.app.db.entities.StudentGroup;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by Grishechko on 24.01.2016.
 */
public class DatabaseHelperImpl implements DatabaseHelper {
    private DbSQLiteOpenHelper sqLiteOpenHelper;
    private Cupboard cupboard;

    public DatabaseHelperImpl(DbSQLiteOpenHelper sqLiteOpenHelper, Cupboard cupboard) {
        this.sqLiteOpenHelper = sqLiteOpenHelper;
        this.cupboard = cupboard;
    }

    @Override
    public DbCall<String> getTest() {
        return new DbCall<String>() {
            @Override
            public String execute() {
                return null;
            }
        };
    }

    @Override
    public DbCall<Boolean> putTest() {
        return null;
    }

    @Override
    public DbCall<Boolean> putStudentGroup(StudentGroup studentGroup) {
        return new DbCall<Boolean>() {
            @Override
            public Boolean execute() {
                SQLiteDatabase db = null;
                try {
                    db = sqLiteOpenHelper.getWritableDatabase();
                    db.beginTransaction();
                    cupboard().withDatabase(db).put(studentGroup);
                    db.setTransactionSuccessful();
                    return true;
                } catch (Exception e) {
                    Log.e("Test", "Exception, cant put stops list");
                    return false;
                } finally {
                    if (db != null) {
                        db.endTransaction();
                    }
                }
            }
        };
    }

    @Override
    public DbCall<StudentGroup> getStudentGroup() {
        return null;
    }
}
