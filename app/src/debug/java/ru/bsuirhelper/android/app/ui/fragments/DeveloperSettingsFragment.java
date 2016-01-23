package ru.bsuirhelper.android.app.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.bsuirhelper.android.app.performance.AnyThread;
import ru.bsuirhelper.android.app.ui.adapters.DeveloperSettingsSpinnerAdapter;
import ru.bsuirhelper.android.app.ui.presenters.DeveloperSettingsPresenter;
import ru.bsuirhelper.android.app.ui.views.DeveloperSettingsView;
import android.view.View;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnCheckedChanged;
import butterknife.OnItemSelected;
import ru.bsuirhelper.android.app.App;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.app.ui.fragment.base.BaseFragment;
import ru.bsuirhelper.android.app.ui.fragment.base.FragmentInfo;

public class DeveloperSettingsFragment extends BaseFragment implements DeveloperSettingsView {

    @Inject
    DeveloperSettingsPresenter presenter;

    @Bind(R.id.developer_settings_git_sha_text_view)
    TextView gitShaTextView;

    @Bind(R.id.developer_settings_build_date_text_view)
    TextView buildDateTextView;

    @Bind(R.id.developer_settings_build_version_code_text_view)
    TextView buildVersionCodeTextView;

    @Bind(R.id.developer_settings_build_version_name_text_view)
    TextView buildVersionNameTextView;

    @Bind(R.id.developer_settings_stetho_switch)
    Switch stethoSwitch;

    @Bind(R.id.developer_settings_leak_canary_switch)
    Switch leakCanarySwitch;

    @Bind(R.id.developer_settings_tiny_dancer_switch)
    Switch tinyDancerSwitch;

    @Bind(R.id.developer_settings_http_logging_level_spinner)
    Spinner httpLoggingLevelSpinner;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.get(getContext()).applicationComponent().plusDeveloperSettingsComponent().inject(this);
    }

    @Override
    protected FragmentInfo getFragmentInfo() {
        return new FragmentInfo(R.layout.fragment_developer_settings);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        httpLoggingLevelSpinner
                .setAdapter(new DeveloperSettingsSpinnerAdapter<>(getActivity().getLayoutInflater())
                .setSelectionOptions(HttpLoggingLevel.allValues()));
        presenter.bindView(this);
    }

    @OnCheckedChanged(R.id.developer_settings_stetho_switch)
    void onStethoSwitchCheckedChanged(boolean checked) {
        presenter.changeStethoState(checked);
    }

    @OnCheckedChanged(R.id.developer_settings_tiny_dancer_switch)
    void onTinyDancerSwitchCheckedChanged(boolean checked) {
        presenter.changeTinyDancerState(checked);
    }

    @OnItemSelected(R.id.developer_settings_http_logging_level_spinner)
    void onHttpLoggingLevelChanged(int position) {
        presenter.changeHttpLoggingLevel(((HttpLoggingLevel) httpLoggingLevelSpinner.getItemAtPosition(position)).loggingLevel);
    }

    @Override
    @AnyThread
    public void changeGitSha(@NonNull String gitSha) {
        runOnUiThreadIfFragmentAlive(() -> {
            assert gitShaTextView != null;
            gitShaTextView.setText(gitSha);
        });
    }

    @Override
    @AnyThread
    public void changeBuildDate(@NonNull String date) {
        runOnUiThreadIfFragmentAlive(() -> {
            assert buildDateTextView != null;
            buildDateTextView.setText(date);
        });
    }

    @Override
    @AnyThread
    public void changeBuildVersionCode(@NonNull String versionCode) {
        runOnUiThreadIfFragmentAlive(() -> {
            assert buildVersionCodeTextView != null;
            buildVersionCodeTextView.setText(versionCode);
        });
    }

    @Override
    @AnyThread
    public void changeBuildVersionName(@NonNull String versionName) {
        runOnUiThreadIfFragmentAlive(() -> {
            assert buildVersionNameTextView != null;
            buildVersionNameTextView.setText(versionName);
        });
    }

    @Override
    @AnyThread
    public void changeStethoState(boolean enabled) {
        runOnUiThreadIfFragmentAlive(() -> {
            assert stethoSwitch != null;
            stethoSwitch.setChecked(enabled);
        });
    }

    @Override
    @AnyThread
    public void changeLeakCanaryState(boolean enabled) {
        runOnUiThreadIfFragmentAlive(() -> {
            assert leakCanarySwitch != null;
            leakCanarySwitch.setChecked(enabled);
        });
    }

    @Override
    @AnyThread
    public void changeTinyDancerState(boolean enabled) {
        runOnUiThreadIfFragmentAlive(() -> {
            assert tinyDancerSwitch != null;
            tinyDancerSwitch.setChecked(enabled);
        });
    }

    @Override
    @AnyThread
    public void changeHttpLoggingLevel(@NonNull HttpLoggingInterceptor.Level loggingLevel) {
        runOnUiThreadIfFragmentAlive(() -> {
            assert httpLoggingLevelSpinner != null;

            for (int position = 0, count = httpLoggingLevelSpinner.getCount(); position < count; position++) {
                if (loggingLevel == ((HttpLoggingLevel) httpLoggingLevelSpinner.getItemAtPosition(position)).loggingLevel) {
                    httpLoggingLevelSpinner.setSelection(position);
                    return;
                }
            }

            throw new IllegalStateException("Unknown loggingLevel, looks like a serious bug. Passed loggingLevel = " + loggingLevel);
        });
    }

    @SuppressLint("ShowToast") // Yeah, Lambdas and Lint are not good friends…
    @Override
    @AnyThread
    public void showMessage(@NonNull String message) {
        runOnUiThreadIfFragmentAlive(() -> Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
    }

    @OnCheckedChanged(R.id.developer_settings_leak_canary_switch)
    void onLeakCanarySwitchCheckedChanged(boolean checked) {
        presenter.changeLeakCanaryState(checked);
    }

    @SuppressLint("ShowToast") // Yeah, Lambdas and Lint are not good friends…
    @Override
    @AnyThread
    public void showAppNeedsToBeRestarted() {
        runOnUiThreadIfFragmentAlive(() -> Toast.makeText(getContext(), "To apply new settings app needs to be restarted", Toast.LENGTH_LONG).show());
    }

    @Override
    public void onDestroyView() {
        presenter.unbindView(this);
        super.onDestroyView();
    }

    private static class HttpLoggingLevel implements DeveloperSettingsSpinnerAdapter.SelectionOption {

        @NonNull
        public final HttpLoggingInterceptor.Level loggingLevel;

        public HttpLoggingLevel(@NonNull HttpLoggingInterceptor.Level loggingLevel) {
            this.loggingLevel = loggingLevel;
        }

        @NonNull
        @Override
        public String title() {
            return loggingLevel.toString();
        }

        @NonNull
        static List<HttpLoggingLevel> allValues() {
            final HttpLoggingInterceptor.Level[] loggingLevels = HttpLoggingInterceptor.Level.values();
            final List<HttpLoggingLevel> values = new ArrayList<>(loggingLevels.length);
            for (HttpLoggingInterceptor.Level loggingLevel : loggingLevels) {
                values.add(new HttpLoggingLevel(loggingLevel));
            }
            return values;
        }
    }
}
