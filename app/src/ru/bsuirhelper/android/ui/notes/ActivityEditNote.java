package ru.bsuirhelper.android.ui.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
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

        createSpinnerFromLessons();

        Intent startIntent = getIntent();
        mRequestCode = startIntent.getIntExtra("REQUEST_CODE", 0);
        mNoteId = startIntent.getIntExtra("note_id", -1);
        mLessonId = startIntent.getIntExtra("lesson_id", -1);

        if (mRequestCode == REQUEST_CODE_EDIT_NOTE) {
            mNoteEditable = mNoteDatabase.fetchNote(mNoteId);
            noteTitle.setText(mNoteEditable.title);
            noteText.setText(mNoteEditable.text);
            if (mSpinner.getVisibility() != View.INVISIBLE) {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) mSpinner.getAdapter();
                int position = adapter.getPosition(mNoteEditable.subject);
                mSpinner.setSelection(position);
                //Disable spinner because note for lesson from schedule
                if (mLessonId != -1) {
                    mSpinner.setEnabled(false);
                }
            }
        } else if (mRequestCode == REQUEST_CODE_ADD_NOTE && mSpinner.getVisibility() != View.INVISIBLE) {
            if (mLessonId != -1) {
                String subject = startIntent.getStringExtra("lesson_subject");
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) mSpinner.getAdapter();
                int position = adapter.getPosition(subject);
                mSpinner.setSelection(position);
                mSpinner.setEnabled(false);
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
        EasyTracker tracker = EasyTracker.getInstance(this);
        tracker.set(Fields.SCREEN_NAME, "Окно редактирования заметки");
        tracker.send(MapBuilder.createAppView().build());
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
                            mSpinner.getVisibility() == View.INVISIBLE ? "" : mSpinner.getSelectedItem().toString(), System.currentTimeMillis());
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

    private void createSpinnerFromLessons() {
        String groupId = ApplicationSettings.getInstance(
                getApplicationContext()).getString("defaultgroup", null);
        mSpinner = (Spinner) findViewById(R.id.spinner_subjects);
        if (groupId != null) {
            ArrayList<Lesson> lessons = mScheduleDatabase.fetchAllLessons(groupId);
            Set<String> subjects = new HashSet<String>(lessons.size());
            ArrayList<String> list = new ArrayList<String>(subjects);
            for (Lesson lesson : lessons) {
                subjects.add(lesson.fields.get("subject") + " " + lesson.fields.get("subjectType"));
            }
            list.addAll(subjects);
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list);
            mSpinner.setAdapter(spinnerAdapter);
            mSpinner.setVisibility(View.VISIBLE);
        } else {
            mSpinner.setVisibility(View.INVISIBLE);
        }
    }
}
