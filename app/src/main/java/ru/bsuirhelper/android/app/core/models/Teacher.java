package ru.bsuirhelper.android.app.core.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vladislav on 4/18/15.
 */
public class Teacher {
    private long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private List<String> academicDepartments;

    public Teacher() {
        academicDepartments = new ArrayList<>();
    }

    public Teacher(long id, String firstName, String lastName, String middleName, List<String> academicDepartments) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.academicDepartments = academicDepartments;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public List<String> getAcademicDepartments() {
        return academicDepartments;
    }

    public void setAcademicDepartments(List<String> academicDepartments) {
        this.academicDepartments = academicDepartments;
    }

    public String getFullShortName() {
        if(firstName != null && middleName != null) {
            return String.format("%s %s.%s", lastName, firstName.substring(0, 1), middleName.substring(0, 1));
        }
        return "";
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", academicDepartments=" + academicDepartments +
                '}';
    }
}
