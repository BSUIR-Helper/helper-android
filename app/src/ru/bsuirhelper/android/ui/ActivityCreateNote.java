package ru.bsuirhelper.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import ru.bsuirhelper.android.Note;
import ru.bsuirhelper.android.NoteDatabase;
import ru.bsuirhelper.android.bsuirhelper.R;

/**
 * Created by Влад on 01.11.13.
 */
public class ActivityCreateNote extends ActivityDrawerMenu {
    private NoteDatabase mNoteDatabase;
    private TextView noteTitle;
    private TextView noteText;
    private int mNoteId = -1;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnote);
        mNoteDatabase = new NoteDatabase(this);
        noteTitle = (TextView) findViewById(R.id.edittext_createnotetitle);
        noteText = (TextView) findViewById(R.id.edittext_createnotetext);
    }
    @Override
    public void onResume(){
        super.onResume();
        Intent intent = getIntent();
        mNoteId = intent.getIntExtra("note_id",-1);
        if(mNoteId != -1){
            Note note = mNoteDatabase.fetchNote(mNoteId);
            Log.d(ActivityMain.LOG_TAG,"Note"+note);
            noteTitle.setText(note.title);
            noteText.setText(note.text);
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
                if(noteTitle.getText().length() != 0 && noteText.getText().length() != 0) {
                 Note note = new Note(noteTitle.getText().toString(),noteText.getText().toString(),"",System.currentTimeMillis());
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
