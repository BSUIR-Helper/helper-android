package ru.bsuirhelper.android.app.db.converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.convert.FieldConverter;
import nl.qbusict.cupboard.convert.FieldConverterFactory;

/**
 * Created by 7times6 on 21.10.14.
 */
public class GenericFieldConverterFactory implements FieldConverterFactory {
    @Override
    public FieldConverter<?> create(Cupboard cupboard, Type type) {

        if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) type;
            return new JsonParametrizedTypeConverter(parameterizedType);
        }

        return null;
    }
}
