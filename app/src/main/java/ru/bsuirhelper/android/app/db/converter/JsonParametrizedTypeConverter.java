package ru.bsuirhelper.android.app.db.converter;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import javax.inject.Inject;

import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.FieldConverter;
import ru.bsuirhelper.android.app.App;

/**
 * Created by 7times6 on 17.10.14.
 */
public class JsonParametrizedTypeConverter<T> implements FieldConverter<T> {

    final Type type;
    final Gson gson;

    public JsonParametrizedTypeConverter(Type type) {
        this.type = type;
        gson = new Gson();
    }

    @Override
    public T fromCursorValue(Cursor cursor, int columnIndex) {

        String jsonString = cursor.getString(columnIndex);
        Gson gson = new Gson();
        T result = gson.fromJson(jsonString, type);

        return result;
    }

    @Override
    public void toContentValue(T value, String key, ContentValues values) {
        final String jsonString = gson.toJson(value, type);
        values.put(key, jsonString);
    }

    @Override
    public EntityConverter.ColumnType getColumnType() {
        return EntityConverter.ColumnType.TEXT;
    }
}
