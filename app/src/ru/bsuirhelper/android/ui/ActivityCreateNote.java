package ru.bsuirhelper.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import ru.bsuirhelper.android.Note;
import ru.bsuirhelper.android.NoteDatabase;
import ru.bsuirhelper.android.bsuirhelper.R;

/**
 * Created by Влад on 01.11.13.
 */
public class ActivityCreateNote extends ActionBarActivity {
    private NoteDatabase mNoteDatabase;
    private EditText noteTitle;
    private EditText noteText;
    private EditText noteSubject;
    private int mNoteId = -1;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnote);
        mNoteDatabase = new NoteDatabase(this);
        noteTitle = (EditText) findViewById(R.id.edittext_createnotetitle);
        noteText = (EditText) findViewById(R.id.edittext_createnotetext);
        noteSubject = (EditText) findViewById(R.id.edittext_createnotesubject);
    }
    @Override
    public void onResume(){
        super.onResume();
        Intent intent = getIntent();
        mNoteId = intent.getIntExtra("note_id",-1);
        if(mNoteId != -1){
            Note note = mNoteDatabase.fetchNote(mNoteId);
            noteTitle.setText(note.title);
            noteText.setText(note.text);
            noteSubject.setText(note.subject);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_createnote_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menu){
        switch(menu.getItemId()){
            case R.id.action_cancelcreatenote:
                startActivity(new Intent(this,ActivityNotes.class));
                return true;
            case R.id.action_createnote:
                if(noteTitle.getText().length() != 0) {
                 Note note = new Note(noteTitle.getText().toString(),noteText.getText().toString(),
                         noteSubject.getText().toString(),System.currentTimeMillis());
                 if(mNoteId != -1){
                    mNoteDatabase.updateNote(mNoteId,note);
                 } else {
                    mNoteDatabase.addNote(note);
                 }
                }
                startActivity(new Intent(this,ActivityNotes.class));
                return true;
            default:
                return super.onOptionsItemSelected(menu);
        }
    }

}
