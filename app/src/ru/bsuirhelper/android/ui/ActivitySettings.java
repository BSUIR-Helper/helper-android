package ru.bsuirhelper.android.ui;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;
import ru.bsuirhelper.android.R;

/**
 * Created by Влад on 15.02.14.
 */
public class ActivitySettings extends PreferenceActivity {
    public static final String KEY_SHOW_SUBJECTS_TYPE = "show_subject_type";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        if (Build.VERSION.SDK_INT > 10) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
