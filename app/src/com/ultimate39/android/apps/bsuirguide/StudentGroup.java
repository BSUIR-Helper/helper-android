package com.ultimate39.android.apps.bsuirguide;

import android.util.Log;

/**
 * Created by Влад on 12.10.13.
 */
public class StudentGroup {
    public String groupId;
    public String faculty;
    public String updatedTime;
    public boolean isChecked = false;
    String[] facultyNames = new String[]{"ФКП", "ФИТиУ", "Военный", "ФРиЭ", "ФКСиС", "ФТ", "ИЭФ"};

    public StudentGroup(String groupId, String updatedTime) {
        this.groupId = groupId;
        this.updatedTime = updatedTime;
        char code = groupId.charAt(1);
        this.faculty = getFacultyName(Character.getNumericValue(code));

    }

    private String getFacultyName(int code) {
        if (code >= facultyNames.length) {
            return null;
        }
        return facultyNames[code - 1];
    }
}
