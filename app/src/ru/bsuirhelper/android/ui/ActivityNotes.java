package ru.bsuirhelper.android.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import ru.bsuirhelper.android.Note;
import ru.bsuirhelper.android.NoteDatabase;
import ru.bsuirhelper.android.bsuirhelper.R;

/**
 * Created by Влад on 02.11.13.
 */
public class ActivityNotes extends ActivityDrawerMenu {
    @Override
    public void onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_notes);

    }
    @Override
    public void onResume(){
        super.onResume();
        ListView listView = (ListView) findViewById(R.id.listview_notes);
        TextView noNotes = (TextView) findViewById(R.id.textview_nonotes);
        NoteDatabase noteDatabase = new NoteDatabase(this);
        Note[] notes = noteDatabase.fetchAllNotes();
        if(notes.length > 0){
            ViewAdapterNotes notesAdapter = new ViewAdapterNotes(this,notes);
            listView.setAdapter(notesAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(view.getContext(),ActivityCreateNote.class);
                    intent.putExtra("note_id",(Integer)view.getTag());
                    startActivity(intent);
                }
            });
            noNotes.setVisibility(View.INVISIBLE);
        } else {
            noNotes.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_notes_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu){
        switch(menu.getItemId()){
            case R.id.action_addnote:
                startActivity(new Intent(this,ActivityCreateNote.class));
                return true;
            default:
                return super.onOptionsItemSelected(menu);
        }
    }


 class ViewAdapterNotes extends ArrayAdapter<Note> {
        private Note[] mNotes;
        private Context mContext;
        private LayoutInflater mInflater;
        public ViewAdapterNotes(Context context,Note[] notes) {
            super(context, R.layout.view_note, notes);
            mNotes = notes;
            mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        private class ViewHolder {
            TextView noteText;
            TextView noteTitle;
            CheckBox checkBox;
            int noteId = -1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = mInflater.inflate(R.layout.view_note,null);
            final View toastView = mInflater.inflate(R.layout.toast_deletenotes,null);
            final TextView noteTitle = (TextView) rowView.findViewById(R.id.textview_notetitle);
            TextView noteText = (TextView) rowView.findViewById(R.id.textview_notetext);
            CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkbox_notedelete);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        noteTitle.setPaintFlags(noteTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        noteTitle.setPaintFlags(noteTitle.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                    Toast toast = new Toast(mContext);
                    toast.setView(toastView);
                    toast.show();
                }

            });

            Note note = getItem(position);
            noteTitle.setText(note.title);
            noteText.setText(note.text);
            rowView.setTag(note.getId());
            return rowView;
        }

    }
}