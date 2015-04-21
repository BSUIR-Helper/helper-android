package ru.bsuirhelper.android.core.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Влад on 12.10.13.
 */
public class StudentGroup implements Parcelable {
    private long id;
    private String groupNumber;
    private String groupName;

    public StudentGroup() {}

    public StudentGroup(long id, String groupName, String groupNumber) {
        this.id = id;
        this.groupName = groupName;
        this.groupNumber = groupNumber;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public long getId() {
        return id;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    @Override
    public String toString() {
        return "StudentGroup{" +
                "groupName='" + groupName + '\'' +
                ", groupNumber='" + groupNumber + '\'' +
                ", id=" + id +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.groupNumber);
        dest.writeString(this.groupName);
    }

    private StudentGroup(Parcel in) {
        this.id = in.readLong();
        this.groupNumber = in.readString();
        this.groupName = in.readString();
    }

    public static final Parcelable.Creator<StudentGroup> CREATOR = new Parcelable.Creator<StudentGroup>() {
        public StudentGroup createFromParcel(Parcel source) {
            return new StudentGroup(source);
        }

        public StudentGroup[] newArray(int size) {
            return new StudentGroup[size];
        }
    };
}
