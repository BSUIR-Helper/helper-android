package ru.bsuirhelper.android.ui.teachers;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.ui.ActivityDrawerMenu;

public class ActivityManagerTeachers extends ActivityDrawerMenu {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_managerteachers_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_addteacher:
                Toast.makeText(getApplicationContext(), "Add teacher", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_deleteteacher:
                Toast.makeText(getApplicationContext(), "Delete teacher", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
