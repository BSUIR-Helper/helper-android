package ru.bsuirhelper.android.app.core.models;

import java.util.ArrayList;
import java.util.List;

public class Lesson {
    private long id = -1;
    private String auditory;
    private String lessonTime;
    private String lessonType;
    private int subgroup;
    private String subjectName;
    private List<Integer> weekNumbers;
    private Teacher teacher;
    private StudentGroup studentGroup;
    private int weekDay;

    public Lesson() {
        id = -1;
        weekNumbers = new ArrayList<>();
        teacher = new Teacher();
        studentGroup = new StudentGroup();
    }

    public Lesson(long id, String auditory, String lessonTime, String lessonType, int subgroup, StudentGroup studentGroup, String subjectName, List<Integer> weekNumbers, Teacher teacher, int weekDay) {
        this.id = id;
        this.auditory = auditory;
        this.lessonTime = lessonTime;
        this.lessonType = lessonType;
        this.subgroup = subgroup;
        this.subjectName = subjectName;
        this.weekNumbers = weekNumbers;
        this.teacher = teacher;
        this.studentGroup = studentGroup;
        this.weekDay = weekDay;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuditory() {
        return auditory;
    }

    public void setAuditory(String auditory) {
        this.auditory = auditory;
    }

    public String getLessonTime() {
        return lessonTime;
    }

    public void setLessonTime(String lessonTime) {
        this.lessonTime = lessonTime;
    }

    public String getType() {
        return lessonType;
    }

    public void setType(String lessonType) {
        this.lessonType = lessonType;
    }

    public int getSubgroup() {
        return subgroup;
    }

    public void setSubgroup(int subgroup) {
        this.subgroup = subgroup;
    }

    public StudentGroup getStudentGroup() {
        return studentGroup;
    }

    public void setStudentGroup(StudentGroup studentGroup) {
        this.studentGroup = studentGroup;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public List<Integer> getWeekNumbers() {
        return weekNumbers;
    }

    public void setWeekNumbers(List<Integer> weekNumbers) {
        this.weekNumbers = weekNumbers;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public int getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }

    @Override
    public String toString() {
        return "ScheduleLesson{" +
                "id=" + id +
                ", auditory='" + auditory + '\'' +
                ", lessonTime='" + lessonTime + '\'' +
                ", lessonType='" + lessonType + '\'' +
                ", subgroup=" + subgroup +
                ", studentGroup=" + studentGroup +
                ", subjectName='" + subjectName + '\'' +
                ", weekNumbers=" + weekNumbers +
                ", teacher=" + teacher +
                ", weekDay='" + weekDay + '\'' +
                '}';
    }
}

