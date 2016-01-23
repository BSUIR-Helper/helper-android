package ru.bsuirhelper.android.app.api.entities;

import android.support.annotation.NonNull;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Grishechko on 23.01.2016.
 */
@Root(name = "scheduleXmlModels")
public class ScheduleStudentGroupList {
    private final static String XML_ELEMENT_SCHEDULE_MODEL = "scheduleModel";

    @ElementList(entry = XML_ELEMENT_SCHEDULE_MODEL, inline = true)
    private List<ScheduleModel> scheduleModels;

    public List<ScheduleModel> getScheduleModels() {
        return scheduleModels;
    }
}
