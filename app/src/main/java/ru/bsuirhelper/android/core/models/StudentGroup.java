package ru.bsuirhelper.android.core.models;

/**
 * Created by Влад on 12.10.13.
 */
public class StudentGroup {
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
}
