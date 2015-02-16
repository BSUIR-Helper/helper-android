package ru.bsuirhelper.android.core.schedule;

/**
 * Created by Влад on 12.10.13.
 */
public class StudentGroup {
    public final String groupId;
    public final String faculty;
    public final String updatedTime;
    public boolean isChecked = false;

    public StudentGroup(String groupId, String faculty, String updatedTime) {
        this.groupId = groupId;
        this.updatedTime = updatedTime;
        this.faculty = faculty;

    }

    @Override
    public String toString() {
        return groupId + " (" + faculty + ")";
    }

}
