package ru.bsuirhelper.android.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import ru.bsuirhelper.android.bsuirhelper.R;

/**
 * Created by Влад on 29.10.13.
 */
public class ActivityDrawerMenu extends ActionBarActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private final int DRAWER_NAVIGATION_LAYOUT_ID = R.layout.drawerlayout;
    private final String[] mMenuItems = new String[]{"Группы","Заметки"};
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
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
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);

    }
    @Override
    public void setContentView(int layoutId){
        LayoutInflater layoutInflater = (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        /*
        ViewGroup parentLayout = (ViewGroup) layoutInflater.inflate(layoutId,null);
        parentLayout.addView(layoutInflater.inflate(DRAWER_NAVIGATION_LAYOUT_ID, parentLayout, false),1);
        */

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
        ListView drawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        drawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mMenuItems));

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch(position){
                    case 0:
                        startActivity(new Intent(view.getContext(),ActivityManagerGroups.class));
                        break;
                    case 1:
                        startActivity(new Intent(view.getContext(),ActivityNotes.class));
                        break;
                }
            }
        });
        // Set the drawer toggle as the DrawerListener
        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }
}
