package ru.bsuirhelper.android.app.db.converter;

import com.google.auto.value.AutoValue;

import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.EntityConverterFactory;
import nl.qbusict.cupboard.convert.ReflectiveEntityConverter;

/**
 * Created by Grishechko on 24.01.2016.
 */
public class AutoValueEntityCoverterFactory implements EntityConverterFactory {

    @Override
    public <T> EntityConverter<T> create(Cupboard cupboard, Class<T> type) {
        boolean useAutoValueEntityFactory = type.getAnnotation(AutoValueEntity.class) != null;
        if (useAutoValueEntityFactory) {
            return new AutoValueEntityConverter<T>(cupboard, type);
        } else {
            return new ReflectiveEntityConverter<T>(cupboard, type);
        }
    }

}
