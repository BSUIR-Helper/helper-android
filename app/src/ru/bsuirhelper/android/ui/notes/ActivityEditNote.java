package ru.bsuirhelper.android.ui.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.google.analytics.tracking.android.EasyTracker;
import ru.bsuirhelper.android.ApplicationSettings;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.notes.Note;
import ru.bsuirhelper.android.core.notes.NoteDatabase;
import ru.bsuirhelper.android.core.schedule.Lesson;
import ru.bsuirhelper.android.core.schedule.ScheduleDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Влад on 01.11.13.
 */
public class ActivityEditNote extends ActionBarActivity {

    public static final int REQUEST_CODE_ADD_NOTE = 0;
    public static final int REQUEST_CODE_EDIT_NOTE = 1;

    private NoteDatabase mNoteDatabase;
    private ScheduleDatabase mScheduleDatabase;
    private EditText noteTitle;
    private EditText noteText;
    private int mNoteId = -1;
    private int mLessonId = -1;
    private Note mNoteEditable;
    private Spinner mSpinner;
    private int mRequestCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editnote);
        mNoteDatabase = NoteDatabase.getInstance(getApplicationContext());
        mScheduleDatabase = new ScheduleDatabase(getApplicationContext());

        noteTitle = (EditText) findViewById(R.id.edittext_createnotetitle);
        noteText = (EditText) findViewById(R.id.edittext_createnotetext);

        //Create spinner from subjects
        ArrayList<Lesson> lessons = mScheduleDatabase.fetchAllLessons(ApplicationSettings.getInstance(
                getApplicationContext()).getString("defaultgroup", null));
        Set<String> subjects = new HashSet<String>(lessons.size());
        ArrayList<String> list = new ArrayList<String>(subjects);
        for (Lesson lesson : lessons) {
            subjects.add(lesson.fields.get("subject") + " " + lesson.fields.get("subjectType"));
        }
        list.addAll(subjects);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list);
        mSpinner = (Spinner) findViewById(R.id.spinner_subjects);
        mSpinner.setAdapter(spinnerAdapter);

        Intent startIntent = getIntent();
        mRequestCode = startIntent.getIntExtra("REQUEST_CODE", 0);
        mNoteId = startIntent.getIntExtra("note_id", -1);
        mLessonId = startIntent.getIntExtra("lesson_id", -1);

        if (mRequestCode == REQUEST_CODE_EDIT_NOTE) {

            mNoteEditable = mNoteDatabase.fetchNote(mNoteId);
            noteTitle.setText(mNoteEditable.title);
            noteText.setText(mNoteEditable.text);

            ArrayAdapter<String> adapter = (ArrayAdapter<String>) mSpinner.getAdapter();
            int position = adapter.getPosition(mNoteEditable.subject);
            mSpinner.setSelection(position);
            //Disable spinner because note for lesson from schedule
            if (mLessonId != -1) {
                mSpinner.setEnabled(false);
            }
        } else if (mRequestCode == REQUEST_CODE_ADD_NOTE) {
            if (mLessonId != -1) {
                String subject = startIntent.getStringExtra("lesson_subject");
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) mSpinner.getAdapter();
                int position = adapter.getPosition(subject);
                mSpinner.setSelection(position);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_createnote_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
            case R.id.action_cancelcreatenote:
                finish();
                return true;
            case R.id.action_createnote:
                if (noteTitle.getText().length() != 0) {
                    Note note = new Note(noteTitle.getText().toString(), noteText.getText().toString(),
                            mSpinner.getSelectedItem().toString(), System.currentTimeMillis());
                    note.lesson_id = mLessonId;
                    if (mRequestCode == REQUEST_CODE_EDIT_NOTE) {
                        mNoteDatabase.updateNote(mNoteId, note);
                    } else if (mRequestCode == REQUEST_CODE_ADD_NOTE) {
                        mNoteDatabase.addNote(note);
                    }
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(menu);
        }
    }

}
