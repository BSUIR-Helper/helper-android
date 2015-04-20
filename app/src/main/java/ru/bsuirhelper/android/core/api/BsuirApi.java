package ru.bsuirhelper.android.core.api;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.mime.TypedByteArray;

/**
 * Created by vladislav on 4/20/15.
 */
public class BsuirApi {
    static final String API = "http://www.bsuir.by";
    static RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint(API)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build();

    public static String groupSchedule (String groupNumber) {
        StudentgroupSchedule studentgroupSchedule = restAdapter.create(StudentgroupSchedule.class);
        Response response =  studentgroupSchedule.studentgroupSchedule(groupNumber);
        if(response != null && response.getStatus() == 200) {
            return new String(((TypedByteArray) response.getBody()).getBytes());
        }
        return null;
    }

    public static void groupSchedule(String groupNumber, Callback<Response> callback) {
        StudentgroupSchedule studentGroupSchedule = restAdapter.create(StudentgroupSchedule.class);
        studentGroupSchedule.studentgroupSchedule(groupNumber, callback);
    }

    public interface StudentgroupSchedule {
        @GET("/schedule/rest/schedule/{groupnumber}")
        public Response studentgroupSchedule(@Path("groupnumber") String groupNumber);

        @GET("/schedule/rest/schedule/{groupnumber}")
        public void studentgroupSchedule(@Path("groupnumber") String groupNumber, Callback<Response> callback);
    }
}
