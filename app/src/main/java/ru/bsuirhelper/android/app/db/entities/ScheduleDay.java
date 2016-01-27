package ru.bsuirhelper.android.app.db.entities;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.bsuirhelper.android.app.api.entities.*;
import ru.bsuirhelper.android.app.api.entities.ScheduleStudentGroup;

/**
 * Created by Grishechko on 26.01.2016.
 */
public class ScheduleDay implements DbConverter<ScheduleModel, ScheduleDay> {

    @NonNull
    private List<ScheduleLesson> scheduleLessons;

    @NonNull
    private String weekDay;

    public ScheduleDay() {
    }

    public ScheduleDay(@NonNull List<ScheduleLesson> scheduleLessons, @NonNull String weekDay) {
        this.scheduleLessons = scheduleLessons;
        this.weekDay = weekDay;
    }

    @NonNull
    public List<ScheduleLesson> getScheduleLessons() {
        return scheduleLessons;
    }

    @NonNull
    public String getWeekDay() {
        return weekDay;
    }

    @Override
    public ScheduleDay setDataFrom(@NonNull ScheduleModel scheduleModel) {
        this.weekDay = scheduleModel.getWeekDay();
        scheduleLessons = new ArrayList<>();
        for (ScheduleStudentGroup scheduleStudentGroup : scheduleModel.getScheduleStudentGroups()) {
            scheduleLessons.add(new ScheduleLesson().setDataFrom(scheduleStudentGroup));
        }
        return this;
    }
}
