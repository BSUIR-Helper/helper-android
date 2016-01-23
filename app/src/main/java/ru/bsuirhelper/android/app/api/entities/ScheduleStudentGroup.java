package ru.bsuirhelper.android.app.api.entities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.ElementUnion;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Grishechko on 22.01.2016.
 */
@Root(name = "schedule")
public class ScheduleStudentGroup {

    private static final String XML_ELEMENT_AUDITORY = "auditory";
    private static final String XML_ELEMENT_EMPLOYEE = "employee";
    private static final String XML_ELEMENT_LESSON_TIME = "lessonTime";
    private static final String XML_ELEMENT_LESSON_TYPE = "lessonType";
    private static final String XML_ELEMENT_NUM_SUBGROUP = "numSubgroup";
    private static final String XML_ELEMENT_STUDENT_GROUP = "studentGroup";
    private static final String XML_ELEMENT_SUBJECT = "subject";
    private static final String XML_ELEMENT_WEEK_NUMBER = "weekNumber";
    private static final String XML_ELEMENT_NOTE = "note";
    private static final String XML_ELEMENT_ZAOCH = "zaoch";

    @NonNull
    @Element(name = XML_ELEMENT_AUDITORY)
    private String auditory;

    @NonNull
    @Element(name = XML_ELEMENT_EMPLOYEE, required = false)
    private Employee employee;

    @NonNull
    @Element(name = XML_ELEMENT_LESSON_TIME)
    private String lessonTime;

    @NonNull
    @Element(name = XML_ELEMENT_LESSON_TYPE)
    private String lessonType;

    @NonNull
    @Element(name = XML_ELEMENT_NUM_SUBGROUP)
    private Integer numSubgroup;

    @NonNull
    @Element(name = XML_ELEMENT_STUDENT_GROUP)
    private String studentGroup;

    @NonNull
    @Element(name = XML_ELEMENT_SUBJECT)
    private String subject;

    @NonNull
    @ElementListUnion({
            @ElementList(entry = XML_ELEMENT_WEEK_NUMBER, type = Integer.class, inline = true, required = false)
    })
    private List<Integer> weekNumbers;

    @Nullable
    @Element(name = XML_ELEMENT_NOTE, required = false)
    private String note;

    @NonNull
    @Element(name = XML_ELEMENT_ZAOCH)
    private boolean zaoch;

    @NonNull
    public String getAuditory() {
        return auditory;
    }

    @NonNull
    public Employee getEmployee() {
        return employee;
    }

    @NonNull
    public String getLessonTime() {
        return lessonTime;
    }

    @NonNull
    public String getLessonType() {
        return lessonType;
    }

    @NonNull
    public Integer getNumSubgroup() {
        return numSubgroup;
    }

    @NonNull
    public String getStudentGroup() {
        return studentGroup;
    }

    @NonNull
    public String getSubject() {
        return subject;
    }

    @NonNull
    public List<Integer> getWeekNumbers() {
        return weekNumbers;
    }

    @NonNull
    public boolean isZaoch() {
        return zaoch;
    }

    @Nullable
    public String getNote() {
        return note;
    }
}
