package ru.bsuirhelper.android.app.db.entities;

import android.support.annotation.NonNull;

import java.util.List;

import ru.bsuirhelper.android.app.api.entities.ScheduleStudentGroup;

/**
 * Created by Grishechko on 26.01.2016.
 */
public class ScheduleLesson implements DbConverter<ScheduleStudentGroup, ScheduleLesson> {

    @NonNull
    private String auditory;

    @NonNull
    private Employee employee;

    @NonNull
    private String lessonTime;

    @NonNull
    private String lessonType;

    @NonNull
    private Integer numSubgroup;

    @NonNull
    private String studentGroup;

    @NonNull
    private String subject;

    @NonNull
    private List<Integer> weekNumbers;

    public ScheduleLesson() {
    }

    public ScheduleLesson(@NonNull String auditory, @NonNull Employee employee, @NonNull String lessonTime, @NonNull String lessonType, @NonNull Integer numSubgroup, @NonNull String studentGroup, @NonNull String subject, @NonNull List<Integer> weekNumbers, @NonNull boolean zaoch) {
        this.auditory = auditory;
        this.employee = employee;
        this.lessonTime = lessonTime;
        this.lessonType = lessonType;
        this.numSubgroup = numSubgroup;
        this.studentGroup = studentGroup;
        this.subject = subject;
        this.weekNumbers = weekNumbers;
        this.zaoch = zaoch;
    }

    @NonNull
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
    public String getTime() {
        return lessonTime;
    }

    @NonNull
    public String getType() {
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

    @Override
    public ScheduleLesson setDataFrom(ScheduleStudentGroup scheduleStudentGroup) {
        this.auditory = scheduleStudentGroup.getAuditory();
        if(scheduleStudentGroup.getEmployee() != null) {
            this.employee = new Employee().setDataFrom(scheduleStudentGroup.getEmployee());
        }
        this.lessonTime = scheduleStudentGroup.getLessonTime();
        this.lessonType = scheduleStudentGroup.getLessonType();
        this.numSubgroup = scheduleStudentGroup.getNumSubgroup();
        this.studentGroup = scheduleStudentGroup.getStudentGroup();
        this.subject = scheduleStudentGroup.getSubject();
        this.weekNumbers = scheduleStudentGroup.getWeekNumbers();
        this.zaoch = scheduleStudentGroup.isZaoch();
        return this;
    }
}
