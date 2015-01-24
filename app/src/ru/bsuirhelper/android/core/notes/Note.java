package ru.bsuirhelper.android.core.notes;

/**
 * Created by Влад on 02.11.13.
 */
public class Note {
    public String text;
    public String title;
    public String subject;
    public int lesson_id = NO_LESSON;
    public long dateCreated;
    public int id;
    public static final int NO_LESSON = -1;

    public Note(String title, String text, String subject, long dateCreated) {
        this.text = text;
        this.title = title;
        this.subject = subject;
        this.dateCreated = dateCreated;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
