package ru.bsuirhelper.android.app.db.converter;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.FieldConverter;

public class GsonFieldConverter<T> implements FieldConverter<T> {

    private final Gson mGson;
    private final Type mType;

    public GsonFieldConverter(Gson gson, Type type) {
        mGson = gson;
        mType = type;
    }

    @Override
    public T fromCursorValue(Cursor cursor, int columnIndex) {
        return mGson.fromJson(cursor.getString(columnIndex), mType);
    }

    @Override
    public EntityConverter.ColumnType getColumnType() {
        return EntityConverter.ColumnType.TEXT;
    }

    @Override
    public void toContentValue(T value, String key, ContentValues values) {
        values.put(key, mGson.toJson(value));
    }
}