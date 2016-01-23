package ru.bsuirhelper.android.app.developer_settings;

import android.support.annotation.NonNull;
import ru.bsuirhelper.android.app.ui.fragments.DeveloperSettingsFragment;


import dagger.Subcomponent;

@Subcomponent
public interface DeveloperSettingsComponent {
    void inject(@NonNull DeveloperSettingsFragment developerSettingsFragment);
}
