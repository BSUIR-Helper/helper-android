package ru.bsuirhelper.android.ui.notes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.*;
import com.google.analytics.tracking.android.EasyTracker;
import ru.bsuirhelper.android.ApplicationSettings;
import ru.bsuirhelper.android.core.notes.Note;
import ru.bsuirhelper.android.core.notes.NoteDatabase;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.ui.ActivityDrawerMenu;
import ru.bsuirhelper.android.ui.notes.ActivityCreateNote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Влад on 02.11.13.
 */
public class ActivityNotes extends ActivityDrawerMenu {
    private ViewAdapterNotes mNotesAdapter;
    private List<Note> mNotesList;
    private List<View> mNotesForDelete;
    private ListView mListView;
    private NoteDatabase mNoteDatabase;
    final int ANIMATION_DURATION = 600;
    @Override
    public void onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_notes);
       mListView = (ListView) findViewById(R.id.listview_notes);
       mNoteDatabase = new NoteDatabase(this);
       mNotesForDelete = new ArrayList<View>();
       mNotesList = new ArrayList<Note>(Arrays.asList(mNoteDatabase.fetchAllNotes()));
       ApplicationSettings.getInstance(this).putInt("notes",mNotesList.size());
       mNotesAdapter = new ViewAdapterNotes(this,mNotesList);
        if(mNotesList.size() > 0){
            mListView.setAdapter(mNotesAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(view.getContext(),ActivityCreateNote.class);
                    intent.putExtra("note_id",((ViewHolder)view.getTag()).id);
                    startActivity(intent);

                }
            });
       }
    }
    @Override
    public void onResume(){
        super.onResume();

    }

    @Override
    public void onStart(){
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop(){
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
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
            case R.id.action_deletenotes:
                deleteNotes();
                updateDrawerMenu();
                return true;
            default:
                return super.onOptionsItemSelected(menu);
        }
    }

    private class ViewHolder {
        TextView noteText;
        TextView noteTitle;
        CheckBox checkBox;
        TextView noteSubject;
        boolean needInflate;
        boolean checked = false;
        int id;
    }
    private void deleteRowInListView(final View v, final int index) {
        Animation.AnimationListener al = new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                mNotesList.remove(index);
                ViewHolder vh = (ViewHolder)v.getTag();
                vh.needInflate = true;
                mNotesAdapter.notifyDataSetChanged();
            }
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationStart(Animation animation) {}
        };

        collapse(v, al);
    }

    private void collapse(final View v, Animation.AnimationListener al) {
        final int initialHeight = v.getMeasuredHeight();

        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                }
                else {
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        if (al!=null) {
            anim.setAnimationListener(al);
        }
        anim.setDuration(ANIMATION_DURATION);
        v.startAnimation(anim);
    }
    private void deleteNotes(){
        boolean someNoteChecked = false;
        if(mListView.getCount() == 0){
            return;
        }
        for(int i = 0; i < mListView.getCount() ;i++){
            View view = mListView.getChildAt(i);
            ViewHolder vh = (ViewHolder) view.getTag();
            if(vh.checked){
             someNoteChecked = true;
             deleteRowInListView(view, i);
             mNoteDatabase.removeNote(vh.id);
            }
        }
        if(!someNoteChecked){
          Toast.makeText(this,"Не выбрана заметка для удаления",Toast.LENGTH_SHORT).show();
        }
        ApplicationSettings.getInstance(this).putInt("notes", mNoteDatabase.fetchAllNotes().length);
    }
 class ViewAdapterNotes extends ArrayAdapter<Note> {
        private Context mContext;
        private LayoutInflater mInflater;
        Toast buttonDeleteNotes;
        public ViewAdapterNotes(Context context,List<Note> notes) {
            super(context, R.layout.view_note, notes);
            mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            buttonDeleteNotes = new Toast(context);
            buttonDeleteNotes.setView(mInflater.inflate(R.layout.toast_deletenotes,null));
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final View rowView;
            Note note = getItem(position);

            if(convertView == null){
                rowView = mInflater.inflate(R.layout.view_note,null);
                setViewHolder(rowView,note.getId());
            } else if ( ((ViewHolder)convertView.getTag()).needInflate){
                rowView = mInflater.inflate(R.layout.view_note,null);
                setViewHolder(rowView,note.getId());
            } else {
                rowView = convertView;
            }

            final ViewHolder vh = (ViewHolder) rowView.getTag();
            vh.noteTitle.setText(note.title);
            vh.noteText.setText(note.text);
            if(!note.subject.equals("")){
                vh.noteSubject.setText(note.text);
                vh.noteSubject.setVisibility(View.VISIBLE);
            } else {
                vh.noteSubject.setVisibility(View.GONE);
            }
            vh.noteSubject.setText(note.subject);
            vh.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        vh.noteTitle.setPaintFlags(vh.noteTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        vh.noteTitle.setPaintFlags(vh.noteTitle.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                    //buttonDeleteNotes.show();
                    vh.checked = !vh.checked;
                }

            });
            return rowView;
        }

        private void setViewHolder(View v, int id){
            ViewHolder vh = new ViewHolder();
            vh.noteTitle = (TextView) v.findViewById(R.id.textview_notetitle);
            vh.noteText = (TextView) v.findViewById(R.id.textview_notetext);
            vh.checkBox = (CheckBox) v.findViewById(R.id.checkbox_notedelete);
            vh.noteSubject = (TextView) v.findViewById(R.id.textview_notesubject);
            vh.needInflate = false;
            vh.id = id;
            v.setTag(vh);
        }

    }
}