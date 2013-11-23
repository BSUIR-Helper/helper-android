package ru.bsuirhelper.android.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.*;
import ru.bsuirhelper.android.ApplicationSettings;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.ui.schedule.ActivityManagerGroups;
import ru.bsuirhelper.android.ui.notes.ActivityNotes;
import ru.bsuirhelper.android.ui.teachers.ActivityManagerTeachers;

/**
 * Created by Влад on 29.10.13.
 */
public class ActivityDrawerMenu extends ActionBarActivity {
    private final int ACTIVITY_SCHEDULE = 0;
    private final int ACTIVITY_NOTES = 1;
    private final int ACTIVITY_TEACHERS = 2;
    private DrawerLayout mDrawerLayout;
    private DrawerArrayAdapter mDrawerAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private final int DRAWER_NAVIGATION_LAYOUT_ID = R.layout.drawerlayout;
    private final String[] mMenuItems = new String[]{"Расписание","Заметки","Преподаватели"};

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
       super.onPostCreate(savedInstanceState);
       if(mDrawerToggle != null){
         mDrawerToggle.syncState();
       }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void setContentView(int layoutId){
        LayoutInflater layoutInflater = (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewGroup parentLayout = (ViewGroup) layoutInflater.inflate(DRAWER_NAVIGATION_LAYOUT_ID,null);
        parentLayout.addView(layoutInflater.inflate(layoutId, parentLayout, false),1);

        super.setContentView(parentLayout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {
        };
        ListView listView = (ListView) findViewById(R.id.left_drawer);
        mDrawerAdapter = new DrawerArrayAdapter(this, mMenuItems);
        listView.setAdapter(mDrawerAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch(position){
                    case ACTIVITY_SCHEDULE:
                        startActivity(new Intent(view.getContext(),ActivityManagerGroups.class));
                        break;
                    case ACTIVITY_NOTES:
                        startActivity(new Intent(view.getContext(),ActivityNotes.class));
                        break;
                    case ACTIVITY_TEACHERS:
                        startActivity(new Intent(view.getContext(), ActivityManagerTeachers.class));
                        break;

                }
            }
        });

        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    protected void updateDrawerMenu(){
        mDrawerAdapter.notifyDataSetChanged();
    }
    protected void openDrawerMenu(){
        mDrawerLayout.openDrawer(GravityCompat.START);
    }
    class DrawerArrayAdapter extends ArrayAdapter<String> {
        LayoutInflater mInflater;
        public DrawerArrayAdapter(Context context, String[] menuItems) {
            super(context,R.layout.drawer_list_item, menuItems);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parentView){
           if(convertView == null){
               convertView  = mInflater.inflate(R.layout.drawer_list_item,null);
               setViewHolder(convertView);
           }
           ViewHolder vh = (ViewHolder) convertView.getTag();
           TextView counterOfNotes = (TextView) convertView.findViewById(R.id.textview_counternotes);
           vh.menuName.setText(getItem(position));
           switch(position){
               case ACTIVITY_SCHEDULE:
                   vh.icon.setImageResource(R.drawable.ic_calendar);
                   counterOfNotes.setVisibility(View.INVISIBLE);
                   break;
               case ACTIVITY_NOTES:
                   counterOfNotes.setVisibility(View.VISIBLE);
                   counterOfNotes.setText(ApplicationSettings.getInstance(ActivityDrawerMenu.this).getInt("notes",0)+"");
                   vh.icon.setImageResource(R.drawable.ic_notes);
                   break;
               case ACTIVITY_TEACHERS:
                   vh.icon.setImageResource(R.drawable.ic_socialgroup);
                   counterOfNotes.setVisibility(View.INVISIBLE);
                   break;

           }
           return convertView;
        }
        class ViewHolder{
            ImageView icon;
            TextView menuName;
        }
        private void setViewHolder(View v){
            ViewHolder vh = new ViewHolder();
            vh.icon = (ImageView) v.findViewById(R.id.imageview_itemicon);
            vh.menuName = (TextView) v.findViewById(R.id.textview_itemname);
            v.setTag(vh);
        }
    }
}
