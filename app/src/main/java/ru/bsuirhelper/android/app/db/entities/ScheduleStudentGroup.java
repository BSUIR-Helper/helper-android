package ru.bsuirhelper.android.app.db.entities;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.bsuirhelper.android.app.api.entities.ScheduleModel;
import ru.bsuirhelper.android.app.api.entities.ScheduleStudentGroupList;

/**
 * Created by Grishechko on 26.01.2016.
 */
public class ScheduleStudentGroup implements DbConverter<ScheduleStudentGroupList, ScheduleStudentGroup> {

    @NonNull
    private Long _id;

    @NonNull
    private StudentGroup studentGroup;

    @NonNull
    private List<ScheduleDay> scheduleModelList;

    public ScheduleStudentGroup(@NonNull StudentGroup studentGroup, @NonNull List<ScheduleDay> scheduleModelList) {
        this._id = studentGroup.getId();
        this.studentGroup = studentGroup;
        this.scheduleModelList = scheduleModelList;
    }

    @NonNull
    @Override
    public ScheduleStudentGroup setDataFrom(@NonNull ScheduleStudentGroupList scheduleStudentGroup) {
        scheduleModelList = new ArrayList<>();
        for (ScheduleModel scheduleModel : scheduleStudentGroup.getScheduleModels()) {
            scheduleModelList.add(new ScheduleDay().setDataFrom(scheduleModel));
        }
        return this;
    }

    public void setStudentGroup(@NonNull StudentGroup studentGroup) {
        this.studentGroup = studentGroup;
        this._id = studentGroup.getId();
    }
}
