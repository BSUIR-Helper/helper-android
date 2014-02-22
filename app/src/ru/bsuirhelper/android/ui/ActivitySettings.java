package ru.bsuirhelper.android.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;
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
    }

}
