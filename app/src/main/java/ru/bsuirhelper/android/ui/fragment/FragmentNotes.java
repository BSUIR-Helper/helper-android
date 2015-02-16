package ru.bsuirhelper.android.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.*;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import ru.bsuirhelper.android.ApplicationSettings;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.models.Note;
import ru.bsuirhelper.android.core.database.NoteDatabase;
import ru.bsuirhelper.android.ui.activity.ActivityDetailNote;
import ru.bsuirhelper.android.ui.activity.ActivityEditNote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Влад on 02.11.13.
 */
public class FragmentNotes extends Fragment {
    public static final String TITLE = "Заметки";

    private ViewAdapterNotes mNotesAdapter;
    private List<Note> mNotesList;
    private List<View> mNotesForDelete;
    private ListView mListView;
    private NoteDatabase mNoteDatabase;
    private final int ANIMATION_DURATION = 600;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentContent = inflater.inflate(R.layout.fragment_notes, container, false);
        mListView = (ListView) fragmentContent.findViewById(R.id.listview_notes);
        mNoteDatabase = NoteDatabase.getInstance(getActivity().getApplicationContext());
        mNotesForDelete = new ArrayList<View>();
        return fragmentContent;
    }

    @Override
    public void onResume() {
        super.onResume();
        mNotesList = new ArrayList<Note>(Arrays.asList(mNoteDatabase.fetchAllNotes()));
        ApplicationSettings.getInstance(getActivity().getApplicationContext()).putInt("notes", mNotesList.size());
        mNotesAdapter = new ViewAdapterNotes(getActivity().getApplicationContext(), mNotesList);
        if (mNotesList.size() > 0) {
            mListView.setAdapter(mNotesAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(view.getContext(), ActivityDetailNote.class);
                    intent.putExtra("note_id", ((ViewHolder) view.getTag()).id);
                    startActivity(intent);

                }
            });
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker tracker = EasyTracker.getInstance(getActivity());
        tracker.set(Fields.SCREEN_NAME, "Окно списка заметок");
        tracker.send(MapBuilder.createAppView().build());
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(getActivity().getApplicationContext()).activityStop(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_notes_fragment_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
            case R.id.action_addnote:
                startActivity(new Intent(getActivity().getApplicationContext(), ActivityEditNote.class));
                return true;
            case R.id.action_deletenotes:
                deleteNotes();
                // updateDrawerMenu();
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
                ViewHolder vh = (ViewHolder) v.getTag();
                vh.needInflate = true;
                mNotesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
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
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        if (al != null) {
            anim.setAnimationListener(al);
        }
        anim.setDuration(ANIMATION_DURATION);
        v.startAnimation(anim);
    }

    private void deleteNotes() {
        boolean someNoteChecked = false;
        if (mListView.getCount() == 0) {
            return;
        }
        for (int i = 0; i < mListView.getCount(); i++) {
            View view = mListView.getChildAt(i);
            ViewHolder vh = (ViewHolder) view.getTag();
            if (vh.checked) {
                someNoteChecked = true;
                deleteRowInListView(view, i);
                mNoteDatabase.removeNote(vh.id);
            }
        }
        if (!someNoteChecked) {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.not_selected_note), Toast.LENGTH_SHORT).show();
        }
        ApplicationSettings.getInstance(getActivity().getApplicationContext()).putInt("notes", mNoteDatabase.fetchAllNotes().length);
    }

    class ViewAdapterNotes extends ArrayAdapter<Note> {
        private Context mContext;
        private LayoutInflater mInflater;
        Toast buttonDeleteNotes;

        public ViewAdapterNotes(Context context, List<Note> notes) {
            super(context, R.layout.view_note, notes);
            mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            buttonDeleteNotes = new Toast(context);
            buttonDeleteNotes.setView(mInflater.inflate(R.layout.toast_deletenotes, null));
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final View rowView;
            Note note = getItem(position);

            if (convertView == null) {
                rowView = mInflater.inflate(R.layout.view_note, null);
                setViewHolder(rowView, note.getId());
            } else if (((ViewHolder) convertView.getTag()).needInflate) {
                rowView = mInflater.inflate(R.layout.view_note, null);
                setViewHolder(rowView, note.getId());
            } else {
                rowView = convertView;
            }

            final ViewHolder vh = (ViewHolder) rowView.getTag();
            vh.noteTitle.setText(note.title);
            vh.noteText.setText(note.text);
            if (!note.subject.equals("")) {
                vh.noteSubject.setText(note.text);
                vh.noteSubject.setVisibility(View.VISIBLE);
            } else {
                vh.noteSubject.setVisibility(View.GONE);
            }
            vh.noteSubject.setText(note.subject);
            vh.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        vh.noteTitle.setPaintFlags(vh.noteTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        vh.noteTitle.setPaintFlags(vh.noteTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                    //buttonDeleteNotes.show();
                    vh.checked = !vh.checked;
                }

            });
            return rowView;
        }

        private void setViewHolder(View v, int id) {
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