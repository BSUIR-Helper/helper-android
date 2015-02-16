package ru.bsuirhelper.android.core.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.bsuirhelper.android.core.models.Note;

/**
 * Created by Влад on 01.11.13.
 */
public class NoteDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Note";
    private static final int VERSION = 2;
    private final String TABLE_NAME = "notes";
    private final String _ID = "id";
    private final String COLUMN_NAME_TITLE = "title";
    private final String COLUMN_NAME_NOTE = "text";
    private final String COLUMN_NAME_CREATE_DATE = "create_date";
    private final String COLUMN_NAME_LESSOND_ID = "lesson_id";
    private final String COLUMN_NAME_SUBJECT = "subject";
    private SQLiteDatabase db;
    private static NoteDatabase instance;

    private NoteDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);

    }

    public static NoteDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new NoteDatabase(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY,"
                + COLUMN_NAME_TITLE + " TEXT,"
                + COLUMN_NAME_NOTE + " TEXT,"
                + COLUMN_NAME_CREATE_DATE + " INTEGER,"
                + COLUMN_NAME_SUBJECT + " INTEGER,"
                + COLUMN_NAME_LESSOND_ID + " INTEGER"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public Note[] fetchAllNotes() {
        this.open();
        Cursor c = db.rawQuery("SELECT*FROM notes", null);
        Note[] notes = new Note[c.getCount()];
        while (c.moveToNext()) {
            String title = c.getString(c.getColumnIndex(COLUMN_NAME_TITLE));
            String text = c.getString(c.getColumnIndex(COLUMN_NAME_NOTE));
            String subject = c.getString(c.getColumnIndex(COLUMN_NAME_SUBJECT));
            long dateCreated = c.getLong(c.getColumnIndex(COLUMN_NAME_CREATE_DATE));
            Note note = new Note(title, text, subject, dateCreated);
            note.setId(c.getInt(c.getColumnIndex(_ID)));
            notes[c.getPosition()] = note;
        }
        this.close();
        return notes;
    }

    public void addNote(Note note) {
        this.open();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME_TITLE, note.title);
        cv.put(COLUMN_NAME_NOTE, note.text);
        cv.put(COLUMN_NAME_SUBJECT, note.subject);
        cv.put(COLUMN_NAME_CREATE_DATE, note.dateCreated);
        cv.put(COLUMN_NAME_LESSOND_ID, note.lesson_id);
        db.insert(TABLE_NAME, null, cv);
        this.close();
    }

    public Note fetchNote(int rowId) {
        this.open();
        Cursor c = db.rawQuery("SELECT*FROM notes WHERE " + _ID + "=" + rowId, null);
        Note note = null;
        if (c.moveToNext()) {
            String title = c.getString(c.getColumnIndex(COLUMN_NAME_TITLE));
            String text = c.getString(c.getColumnIndex(COLUMN_NAME_NOTE));
            String subject = c.getString(c.getColumnIndex(COLUMN_NAME_SUBJECT));
            long dateCreated = c.getLong(c.getColumnIndex(COLUMN_NAME_CREATE_DATE));
            int id = c.getInt(c.getColumnIndex(_ID));
            note = new Note(title, text, subject, dateCreated);
            note.setId(id);
        }
        this.close();
        return note;
    }

    public Note fetchNoteByLessonId(int lessonId) {
        this.open();
        Cursor c = db.rawQuery("SELECT*FROM notes WHERE " + COLUMN_NAME_LESSOND_ID + "=" + lessonId, null);
        Note note = null;
        if (c.moveToNext()) {
            String title = c.getString(c.getColumnIndex(COLUMN_NAME_TITLE));
            String text = c.getString(c.getColumnIndex(COLUMN_NAME_NOTE));
            String subject = c.getString(c.getColumnIndex(COLUMN_NAME_SUBJECT));
            long dateCreated = c.getLong(c.getColumnIndex(COLUMN_NAME_CREATE_DATE));
            int id = c.getInt(c.getColumnIndex(_ID));
            note = new Note(title, text, subject, dateCreated);
            note.lesson_id = lessonId;
            note.setId(id);
        }
        this.close();
        return note;
    }

    public void updateNote(int id, Note note) {
        this.open();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME_TITLE, note.title);
        cv.put(COLUMN_NAME_NOTE, note.text);
        cv.put(COLUMN_NAME_SUBJECT, note.subject);
        cv.put(COLUMN_NAME_CREATE_DATE, note.dateCreated);
        cv.put(COLUMN_NAME_LESSOND_ID, note.lesson_id);
        db.update(TABLE_NAME, cv, _ID + " = " + id, null);
        this.close();
    }

    public void removeNote(int id) {
        this.open();
        db.delete(TABLE_NAME, _ID + " = " + id, null);
        this.close();
    }

    private void open() {
        db = getWritableDatabase();
    }

    public void close() {
        db.close();
    }
}
