package ru.bsuirhelper.android.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.CupboardBuilder;
import nl.qbusict.cupboard.CupboardFactory;
import ru.bsuirhelper.android.app.db.converter.GenericFieldConverterFactory;
import ru.bsuirhelper.android.app.db.entities.Schedule;
import ru.bsuirhelper.android.app.db.entities.StudentGroup;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;
import static nl.qbusict.cupboard.CupboardFactory.setCupboard;

/**
 * Created by Grishechko on 21.01.2016.
 */
@Module
public class DatabaseModule {

    @Provides
    @NonNull
    @Singleton
    public DbSQLiteOpenHelper provideSQLiteOpenHelper(@NonNull Context context, @NonNull Cupboard cupboard) {
        return new DbSQLiteOpenHelper(context, cupboard);
    }

    @Provides
    @NonNull
    @Singleton
    public Cupboard provideCupboard() {
        CupboardFactory.setCupboard(new CupboardBuilder().registerFieldConverterFactory(new GenericFieldConverterFactory())
               .useAnnotations().build());
        Class[] ENTITIES = new Class[]{StudentGroup.class, Schedule.class};
        for (Class clazz : ENTITIES) {
            CupboardFactory.cupboard().register(clazz);
        }
        return CupboardFactory.cupboard();
    }

    @Provides
    @NonNull
    @Singleton
    public DatabaseHelper provideDatabaseHelper(@NonNull DbSQLiteOpenHelper sqLiteOpenHelper, @NonNull Cupboard cupboard) {
        return new DatabaseHelperImpl(sqLiteOpenHelper, cupboard);
    }
}
