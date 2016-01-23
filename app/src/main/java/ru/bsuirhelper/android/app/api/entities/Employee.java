package ru.bsuirhelper.android.app.api.entities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Grishechko on 22.01.2016.
 */
@Root(name = "employee")
public class Employee {

    private static final String XML_ELEMENT_ID = "id";
    private static final String XML_ELEMENT_ACADEMIC_DEPARTMENT = "academicDepartment";
    private static final String XML_ELEMENT_FIRST_NAME = "firstName";
    private static final String XML_ELEMENT_LAST_NAME = "lastName";
    private static final String XML_ELEMENT_MIDDLE_NAME = "middleName";
    private static final String XML_ELEMENT_RANK = "rank";

    @NonNull
    @Element(name = XML_ELEMENT_ID)
    private long id;

    @NonNull
    @ElementListUnion({
            @ElementList(entry = XML_ELEMENT_ACADEMIC_DEPARTMENT, type = String.class, inline = true, required = false)
    })
    private List<String> academicDepartment;

    @NonNull
    @Element(name = XML_ELEMENT_FIRST_NAME)
    private String firstName;

    @NonNull
    @Element(name = XML_ELEMENT_LAST_NAME)
    private String lastName;

    @NonNull
    @Element(name = XML_ELEMENT_MIDDLE_NAME)
    private String middleName;

    @Nullable
    @Element(name = XML_ELEMENT_RANK, required = false)
    private String rank;

    @NonNull
    public long getId() {
        return id;
    }

    @NonNull
    public List<String> getAcademicDepartment() {
        return academicDepartment;
    }

    @NonNull
    public String getFirstName() {
        return firstName;
    }

    @NonNull
    public String getLastName() {
        return lastName;
    }

    @NonNull
    public String getMiddleName() {
        return middleName;
    }

    @Nullable
    public String getRank() {
        return rank;
    }
}
