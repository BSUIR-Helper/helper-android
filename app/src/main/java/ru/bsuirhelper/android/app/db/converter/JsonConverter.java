package ru.bsuirhelper.android.app.db.converter;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;

import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.FieldConverter;

/**
 * Created by 7times6 on 17.10.14.
 */
public class JsonConverter<T> implements FieldConverter<T> {

    final Class<T> clazz;
    final Gson gson;

    public JsonConverter(Class<T> clazz) {
        this.clazz = clazz;
        this.gson = new Gson();
    }

    @Override
    public EntityConverter.ColumnType getColumnType() {
        return EntityConverter.ColumnType.TEXT;
    }

    @Override
    public T fromCursorValue(Cursor cursor, int columnIndex) {
        String jsonString = cursor.getString(columnIndex);
        T result = gson.fromJson(jsonString, clazz);
        return result;
    }

    @Override
    public void toContentValue(T value, String key, ContentValues values) {
        final String jsonString = gson.toJson(value, clazz);
        values.put(key, jsonString);
    }
}
