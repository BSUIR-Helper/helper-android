package ru.bsuirhelper.android.app.db.entities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Grishechko on 22.01.2016.
 */
public class StudentGroup implements DbConverter<ru.bsuirhelper.android.app.api.entities.StudentGroup, StudentGroup> {

    @NonNull
    private Long _id;

    @NonNull
    private String name;

    @NonNull
    private Long facultyId;

    @Nullable
    private Integer course;

    @NonNull
    private Long specialityDepartmentEducationFormId;

    public StudentGroup() {
    }

    public StudentGroup(@NonNull Long _id, @NonNull String name, @NonNull Long facultyId, Integer course, @NonNull Long specialityDepartmentEducationFormId) {
        this._id = _id;
        this.name = name;
        this.facultyId = facultyId;
        this.course = course;
        this.specialityDepartmentEducationFormId = specialityDepartmentEducationFormId;
    }

    @NonNull
    public Long getId() {
        return _id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public Long getFacultyId() {
        return facultyId;
    }

    @Nullable
    public Integer getCourse() {
        return course;
    }

    @NonNull
    public Long getSpecialityDepartmentEducationFormId() {
        return specialityDepartmentEducationFormId;
    }

    @NonNull
    @Override
    public StudentGroup setDataFrom(@NonNull ru.bsuirhelper.android.app.api.entities.StudentGroup studentGroup) {
        this._id = studentGroup.getId();
        this.name = studentGroup.getName();
        this.facultyId = studentGroup.getFacultyId();
        this.course = studentGroup.getCourse();
        this.specialityDepartmentEducationFormId = studentGroup.getSpecialityDepartmentEducationFormId();
        return this;
    }
}
