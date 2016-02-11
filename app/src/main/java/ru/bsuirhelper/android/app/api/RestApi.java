package ru.bsuirhelper.android.app.api;

import java.util.List;

import javax.annotation.Generated;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import ru.bsuirhelper.android.app.api.entities.EmployeeList;
import ru.bsuirhelper.android.app.api.entities.ScheduleStudentGroupList;
import ru.bsuirhelper.android.app.api.entities.StudentGroupList;

/**
 * Created by Grishechko on 20.01.2016.
 */
public interface RestApi {

    @GET("employee")
    Call<EmployeeList> employees();

    @GET("studentGroup")
    Call<StudentGroupList> studentGroups();

    @GET("schedule/{id}")
    Call<ScheduleStudentGroupList> schedulesStudentGroup(@Path("id") long studentGroupId);
}
