package ru.bsuirhelper.android.core.schedule;

/**
 * Created by Влад on 12.10.13.
 */
public class StudentGroup {
    public final String groupId;
    public final String faculty;
    public final String updatedTime;
    public boolean isChecked = false;
    private final String[] facultyNames = new String[]{"ФКП", "ФИТиУ", "Военный", "ФРиЭ", "ФКСиС", "ФТК", "ИЭФ"};

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
