package ru.bsuirhelper.android.ui;

import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.MenuItem;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.appwidget.ScheduleWidgetProviderBase;

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
            //getActionBar().setDisplayHomeAsUpEnabled(true);
            //getActionBar().setHomeButtonEnabled(true);
        }
        Preference myPref = (Preference) findPreference("show_subject_type");
        myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                ScheduleWidgetProviderBase.updateAllWidgets(ActivitySettings.this);
                return true;
            }
        });
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
