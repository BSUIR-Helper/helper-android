package ru.bsuirhelper.android.app.db.entities;

import android.support.annotation.NonNull;

/**
 * Created by Grishechko on 26.01.2016.
 */
public interface DbConverter<ApiEntity, DbEntity> {
    @NonNull
    DbEntity setDataFrom(@NonNull ApiEntity apiEntity);
}
