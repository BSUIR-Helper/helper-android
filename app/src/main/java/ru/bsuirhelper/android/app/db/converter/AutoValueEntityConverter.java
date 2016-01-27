package ru.bsuirhelper.android.app.db.converter;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.FieldConverter;

/**
 * Created by Grishechko on 24.01.2016.
 */
public class AutoValueEntityConverter<T> implements EntityConverter<T> {

    private final Class<T> mClass;
    private final Class<? extends T> mAutoValueEntity;
    private final Cupboard mCupboard;
    private final boolean mUseAnnotations;
    private final List<Column> mColumns;
    private final Property[] mProperties;
    private Property mIdProperty;
    private static final String _ID = "id";

    @Override
    public T fromCursor(Cursor cursor) {
        try {
            T result = mAutoValueEntity.newInstance();
            int cols = cursor.getColumnCount();
            for (int index = 0; index < mProperties.length && index < cols; index++) {
                Property prop = mProperties[index];
                Class<?> type = prop.type;
                if (cursor.isNull(index)) {
                    if (!type.isPrimitive()) {
                        prop.field.set(result, null);
                    }
                } else {
                    prop.field.set(result, prop.fieldConverter.fromCursorValue(cursor, index));
                }
            }
            return result;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AutoValueEntityConverter(Cupboard cupboard, @NonNull Class<T> entityClass) {
        Class autoValueEntity = getAutoValueEntity(entityClass);
        if (autoValueEntity == null) {
            throw new IllegalArgumentException("Enitity " + entityClass.getName() + " must contain annotation " + AutoValueEntity.class.getName());
        }
        mAutoValueEntity = autoValueEntity;
        mClass = entityClass;
        mCupboard = cupboard;
        mUseAnnotations = cupboard.isUseAnnotations();
        Method[] methods = getAllMethods(entityClass);
        Field[] fields = getAllFields(entityClass);
        ArrayList<Column> columns = new ArrayList<>(methods.length);
        List<Property> properties = new ArrayList<>();
        for (Method method : methods) {
            if (isIgnored(method)) {
                continue;
            }
            Type type = method.getGenericReturnType();
            FieldConverter<?> converter = getFieldConverter(method);
            if (converter == null) {
                throw new IllegalArgumentException("Do not know how to convert method " + method.getName() + " in entity " + entityClass.getName() + " of type " + type);
            }

            if (converter.getColumnType() == null) {
                continue;
            }
            Property prop = new Property();
            prop.field = getFieldByMethod(fields, method);
            if (!prop.field.isAccessible()) {
                prop.field.setAccessible(true);
            }
            prop.name = getColumn(method);
            prop.fieldConverter = (FieldConverter<Object>) converter;
            prop.columnType = converter.getColumnType();
            properties.add(prop);
            if (_ID.equals(prop.name)) {
                mIdProperty = prop;
            }
            columns.add(new Column(prop.name, prop.columnType, null));
        }
        mColumns = Collections.unmodifiableList(columns);
        mProperties = properties.toArray(new Property[properties.size()]);
    }

    @Nullable
    protected Class getAutoValueEntity(@NonNull Class clz) {
        if (clz.getAnnotation(AutoValueEntity.class) != null) {
            return ((AutoValueEntity) clz.getAnnotation(AutoValueEntity.class)).value();
        }
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    protected Field getFieldByMethod(@NonNull Field[] fields, @NonNull Method method) {
        Field result = null;
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(method.getName())) {
                return result;
            }
        }
        return null;
    }

    /*
    protected Index getIndexes(Method method) {
        if (mUseAnnotations) {
            ru.bsuirhelper.android.app.db.converter.Index index = method
                    .getAnnotation(ru.bsuirhelper.android.app.db.converter.Index.class);
            if (index != null) {
                return index;
            }
        }
        return null;
    }
*/

    /**
     * Return the column name based on the method supplied. If annotation
     * processing is enabled for this converter and the method is annotated with
     * a {@link nl.qbusict.cupboard.annotation.Column} annotation, then
     * the column name is taken from the annotation. In all other cases the
     * column name is simply the name of the field.
     *
     * @param method the entity method
     * @return the database column name for this field
     */
    protected String getColumn(Method method) {
        if (mUseAnnotations) {
            ru.bsuirhelper.android.app.db.converter.Column column = method
                    .getAnnotation(ru.bsuirhelper.android.app.db.converter.Column.class);
            if (column != null) {
                return column.value();
            }
        }
        return method.getName();
    }

    /**
     * Get a {@link nl.qbusict.cupboard.convert.FieldConverter} for the specified method. This allows for subclasses
     * to provide a specific {@link nl.qbusict.cupboard.convert.FieldConverter} for a property. The default implementation
     * simply calls {@link nl.qbusict.cupboard.Cupboard#getFieldConverter(java.lang.reflect.Type)}
     *
     * @param method the method
     * @return the field converter
     */
    protected FieldConverter<?> getFieldConverter(Method method) {
        return mCupboard.getFieldConverter(method.getReturnType());
    }

    /**
     * Check if a method should be ignored. This allows subclasses to ignore method at their discretion.
     * <p>
     * The default implementation ignores all static, final or transient fields and if
     * {@link nl.qbusict.cupboard.Cupboard#isUseAnnotations()} returns true also checks for the {@link nl.qbusict.cupboard.annotation.Ignore}
     * annotation.
     *
     * @param method the method
     * @return true if this method should be ignored, false otherwise
     */
    protected boolean isIgnored(Method method) {
        int modifiers = method.getModifiers();
        boolean ignored = Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers);
        if (mUseAnnotations) {
            ignored = ignored || method.getAnnotation(Ignore.class) != null;
        }
        return ignored;
    }


    @Override
    public void toValues(Object object, ContentValues values) {
        for (Property prop : mProperties) {
            if (prop.columnType == ColumnType.JOIN) {
                continue;
            }
            try {
                Object value = prop.field.get(object);
                if (value == null) {
                    if (!prop.name.equals(_ID)) {
                        values.putNull(prop.name);
                    }
                } else {
                    prop.fieldConverter.toContentValue(value, prop.name, values);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<Column> getColumns() {
        return mColumns;
    }

    @Override
    public void setId(Long id, T instance) {
        if (mIdProperty != null) {
            try {
                mIdProperty.field.set(instance, id);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Long getId(T instance) {
        if (mIdProperty != null) {
            try {
                return (Long) mIdProperty.field.get(instance);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public String getTable() {
        return getTable(mClass);
    }

    /**
     * Get all methods for the given class, including inherited methods. Note that no
     * attempts are made to deal with duplicate field names.
     *
     * @param clz the class to get the methods for
     * @return the methods
     */
    private Method[] getAllMethods(Class<?> clz) {
        // optimize for the case where an entity is not inheriting from a base class.
        if (clz.getSuperclass() == null) {
            return extractNonAbstractMethods(clz.getDeclaredMethods());
        }
        List<Method> fields = new ArrayList<>(256);
        Class<?> c = clz;
        do {
            Method[] f = c.getDeclaredMethods();
            fields.addAll(Arrays.asList(f));
            c = c.getSuperclass();
        } while (c != null);
        Method[] result = new Method[fields.size()];
        return extractNonAbstractMethods(fields.toArray(result));
    }

    /**
     * Get all fields for the given class, including inherited fields. Note that no
     * attempts are made to deal with duplicate field names.
     *
     * @param clz the class to get the fields for
     * @return the fields
     */
    private Field[] getAllFields(Class<?> clz) {
        // optimize for the case where an entity is not inheriting from a base class.
        if (clz.getSuperclass() == null) {
            return clz.getDeclaredFields();
        }
        List<Field> fields = new ArrayList<>(256);
        Class<?> c = clz;
        do {
            Field[] f = c.getDeclaredFields();
            fields.addAll(Arrays.asList(f));
            c = c.getSuperclass();
        } while (c != null);
        Field[] result = new Field[fields.size()];
        return fields.toArray(result);
    }

    /**
     * Extract all non abstract methods
     *
     * @param methods methods which can contains non abstract methods
     * @return the abstract methods
     */
    private Method[] extractNonAbstractMethods(@NonNull Method[] methods) {
        List<Method> abstractMethods = new ArrayList<>();
        for (Method method : methods) {
            if (Modifier.isAbstract(method.getModifiers())) {
                abstractMethods.add(method);
            }
        }
        return (Method[]) abstractMethods.toArray();
    }

    private static String getTable(Class<?> clz) {
        return clz.getSimpleName();
    }

    private static class Property {
        Field field;
        String name;
        Class<?> type;
        FieldConverter<Object> fieldConverter;
        ColumnType columnType;
    }


}
