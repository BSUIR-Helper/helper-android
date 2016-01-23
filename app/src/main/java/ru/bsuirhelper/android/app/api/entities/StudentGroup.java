package ru.bsuirhelper.android.app.api.entities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


/**
 * Created by Grishechko on 22.01.2016.
 */
@Root(name = "studentGroup")
public class StudentGroup {

    private static final String XML_ELEMENT_ID = "id";
    private static final String XML_ELEMENT_NAME = "name";
    private static final String XML_ELEMENT_COURSE = "course";
    private static final String XML_ELEMENT_FACULTY_ID = "facultyId";
    private static final String XML_ELEMENT_SPECIAL_DEPARTMENT_EDUCATION = "specialityDepartmentEducationFormId";

    @NonNull
    @Element(name = XML_ELEMENT_ID)
    private long id;

    @NonNull
    @Element(name = XML_ELEMENT_NAME)
    private String name;

    @NonNull
    @Element(name = XML_ELEMENT_FACULTY_ID)
    private long facultyId;

    @Nullable
    @Element(name = XML_ELEMENT_COURSE, required = false)
    private Integer course;

    @NonNull
    @Element(name = XML_ELEMENT_SPECIAL_DEPARTMENT_EDUCATION)
    private long specialityDepartmentEducationFormId;

    @NonNull
    public long getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public long getFacultyId() {
        return facultyId;
    }

    @Nullable
    public int getCourse() {
        return course;
    }

    @NonNull
    public long getSpecialityDepartmentEducationFormId() {
        return specialityDepartmentEducationFormId;
    }
}
