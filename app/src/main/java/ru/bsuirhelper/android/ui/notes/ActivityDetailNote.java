package ru.bsuirhelper.android.ui.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.notes.Note;
import ru.bsuirhelper.android.core.notes.NoteDatabase;

/**
 * Created by Влад on 02.02.14.
 */
public class ActivityDetailNote extends ActionBarActivity {
    private NoteDatabase mNoteDatabase;
    private int mNoteId;
    private int mLessonId;

    private TextView tvNoteTitle;
    private TextView tvNoteSubject;
    private TextView tvNoteText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailnote);
        tvNoteTitle = (TextView) findViewById(R.id.textview_notetitle);
        tvNoteSubject = (TextView) findViewById(R.id.textview_notesubject);
        tvNoteText = (TextView) findViewById(R.id.textview_notetext);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mNoteDatabase = NoteDatabase.getInstance(getApplicationContext());
        Intent startIntent = getIntent();
        mNoteId = startIntent.getIntExtra("note_id", Note.NO_LESSON);
        mLessonId = startIntent.getIntExtra("lesson_id", Note.NO_LESSON);
        Note note = mNoteDatabase.fetchNote(mNoteId);

        tvNoteTitle.setText(note.title);
        tvNoteText.setText(note.text);
        tvNoteSubject.setText(note.subject);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detailnote, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_editnote:
                Intent intent = new Intent(this, ActivityEditNote.class);
                intent.putExtra("note_id", mNoteId);
                intent.putExtra("lesson_id", mLessonId);
                intent.putExtra("REQUEST_CODE", ActivityEditNote.REQUEST_CODE_EDIT_NOTE);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker tracker = EasyTracker.getInstance(this);
        tracker.set(Fields.SCREEN_NAME, "Окно информации о заметке");
        tracker.send(MapBuilder.createAppView().build());
    }
}
