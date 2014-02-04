package ru.bsuirhelper.android.core.notes;

/**
 * Created by Влад on 02.11.13.
 */
public class Note {
    public String text;
    public String title;
    public String subject;
    //Parameters for lesson
    public int lesson_id = -1;

    public long dateCreated;
    public int id;


    public Note(String title,String text,  String subject,long dateCreated){
        this.text = text;
        this.title = title;
        this.subject = subject;
        this.dateCreated = dateCreated;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }
}
