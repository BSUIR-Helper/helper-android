package ru.bsuirhelper.android.app.db.entities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import java.util.List;


public class Employee implements DbConverter<ru.bsuirhelper.android.app.api.entities.Employee, Employee> {

    @NonNull
    private Long _id;

    @NonNull
    private List<String> academicDepartment;

    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

    @NonNull
    private String middleName;

    @Nullable
    private String rank;

    public Employee() {
    }

    public Employee(@NonNull long id, @NonNull List<String> academicDepartment, @NonNull String firstName, @NonNull String lastName, @NonNull String middleName, String rank) {
        this._id = id;
        this.academicDepartment = academicDepartment;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.rank = rank;
    }

    @NonNull
    public long getId() {
        return _id;
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

    @Override
    public Employee setDataFrom(ru.bsuirhelper.android.app.api.entities.Employee employee) {
        _id = employee.getId();
        academicDepartment = employee.getAcademicDepartment();
        firstName = employee.getFirstName();
        lastName = employee.getLastName();
        middleName = employee.getMiddleName();
        rank = employee.getRank();
        return this;
    }
}
