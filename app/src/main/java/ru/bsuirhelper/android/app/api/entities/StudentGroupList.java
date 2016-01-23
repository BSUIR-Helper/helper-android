package ru.bsuirhelper.android.app.api.entities;

import android.support.annotation.NonNull;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Grishechko on 23.01.2016.
 */
@Root(name = "studentGroupXmlModels")
public class StudentGroupList {

    @NonNull
    @ElementList(inline = true)
    private List<StudentGroup> studentGroups;

    @NonNull
    public List<StudentGroup> getStudentGroups() {
        return studentGroups;
    }
}
