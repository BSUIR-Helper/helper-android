package ru.bsuirhelper.android.app.api.entities;

import android.support.annotation.NonNull;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "scheduleModel")
public class ScheduleModel {
    private static final String XML_ELEMENT_WEEK_DAY = "weekDay";
    private static final String XML_ELEMENT_SCHEDULE = "schedule";

    @NonNull
    @ElementListUnion({
            @ElementList(entry = XML_ELEMENT_SCHEDULE, type = ScheduleStudentGroup.class, inline = true, required = false)
    })
    private List<ScheduleStudentGroup> scheduleStudentGroups;

    @NonNull
    @Element(name = XML_ELEMENT_WEEK_DAY)
    private String weekDay;

    @NonNull
    public List<ScheduleStudentGroup> getScheduleStudentGroups() {
        return scheduleStudentGroups;
    }

    @NonNull
    public String getWeekDay() {
        return weekDay;
    }
}