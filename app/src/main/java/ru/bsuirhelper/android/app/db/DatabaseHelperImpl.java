package ru.bsuirhelper.android.app.db;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.QueryResultIterable;
import ru.bsuirhelper.android.app.db.entities.StudentGroup;
import timber.log.Timber;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by Grishechko on 24.01.2016.
 */
public class DatabaseHelperImpl implements DatabaseHelper {
    private CupboardHelper cupboardHelper;

    public DatabaseHelperImpl(DbSQLiteOpenHelper sqLiteOpenHelper, Cupboard cupboard) {
        this.cupboardHelper = new CupboardHelper(cupboard, sqLiteOpenHelper);
    }

    @Override
    public DbCall<Boolean> putStudentGroups(List<StudentGroup> studentGroups) {
        return new DbCall<Boolean>() {
            @Override
            public Boolean execute() {
                return cupboardHelper.putEntities(studentGroups);
            }
        };
    }

    @Override
    public DbCall<List<StudentGroup>> getStudentGroups() {
        return new DbCall<List<StudentGroup>>() {
            @Override
            public List<StudentGroup> execute() {
                return cupboardHelper.getEnitities(StudentGroup.class);
            }
        };
    }

    class CupboardHelper {
        @NonNull
        DbSQLiteOpenHelper sqLiteOpenHelper;

        @NonNull
        Cupboard cupboard;

        public CupboardHelper(@NonNull Cupboard cupboard, @NonNull DbSQLiteOpenHelper sqLiteOpenHelper) {
            this.sqLiteOpenHelper = sqLiteOpenHelper;
            this.cupboard = cupboard;
        }


        @Nullable
        public <T> T getEntity(Class<T> type) {
            QueryResultIterable<T> itr = null;
            try {
                itr = cupboard.withDatabase(sqLiteOpenHelper.getReadableDatabase()).query(type).query();
                return itr.iterator().hasNext() ? itr.iterator().next() : null;
            } finally {
                if (itr != null) {
                    itr.close();
                }
            }
        }

        @NonNull
        public <T> List<T> getEnitities(@NonNull Class<T> type) {
            List<T> entities = new ArrayList<>();
            QueryResultIterable<T> itr = null;
            try {
                itr = cupboard.withDatabase(sqLiteOpenHelper.getReadableDatabase()).query(type).orderBy("_id" + "DESC").query();
                for (T t : itr) {
                    entities.add(t);
                }
            } finally {
                if (itr != null) {
                    itr.close();
                }
            }
            return entities;
        }

        public <T> boolean putEntity(@NonNull T object) {
            SQLiteDatabase db;
            try {
                db = sqLiteOpenHelper.getWritableDatabase();
                cupboard().withDatabase(db).put(object);
                return true;
            } catch (Exception e) {
                Timber.e(e, "Exception, can't put entity: " + object.getClass().getName());
                return false;
            }
        }

        public boolean putEntities(@NonNull Collection<?> entities) {
            SQLiteDatabase db = null;
            try {
                db = sqLiteOpenHelper.getWritableDatabase();
                cupboard().withDatabase(db).put(entities);
                return true;
            } catch (Exception e) {
                Timber.e(e, "Exception, can't put entities: " + entities.getClass().getName());
                return false;
            }
        }

        public <T> boolean deleteEntity(@NonNull T entity) {
            SQLiteDatabase db;
            try {
                db = sqLiteOpenHelper.getWritableDatabase();
                return cupboard().withDatabase(db).delete(entity);
            } catch (Exception e) {
                Timber.e(e, "Exception, can't delete enitity: " + entity.getClass().getName());
                return false;
            }
        }

        public <T> int deleteEntities(Class<T> entity) {
            try {
                return cupboard.withDatabase(sqLiteOpenHelper.getWritableDatabase()).delete(entity, null);
            } catch (Exception e) {
                Timber.e(e, "Exception, can't delete all entities: " + entity.getClass().getName());
                return -1;
            }
        }
    }
}
