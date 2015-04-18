package ru.bsuirhelper.android.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.appwidget.ScheduleWidgetProviderBase;
import ru.bsuirhelper.android.core.ApplicationSettings;
import ru.bsuirhelper.android.core.cache.ScheduleManager;
import ru.bsuirhelper.android.core.models.StudentGroup;
import ru.bsuirhelper.android.ui.activity.ActivityDeleteGroups;
import ru.bsuirhelper.android.ui.adapter.GroupsViewAdapter;
import ru.bsuirhelper.android.ui.asynctask.DownloadScheduleTask;

/**
 * Created by Влад on 12.10.13.
 */
public class FragmentManagerGroups extends Fragment implements DownloadScheduleTask.CallBack {
    private ScheduleManager mScheduleManager;
    private ListView mListGroups;
    private TextView mTextViewNotification;
    private Context context;
    private GroupsViewAdapter mGroupsAdapter;
    public static final String TITLE = "Расписание";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScheduleManager = ScheduleManager.getInstance(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentContent = inflater.inflate(R.layout.activity_managerschedule, container, false);
        mTextViewNotification = (TextView) fragmentContent.findViewById(R.id.textview_groupsarenotadded);
        mListGroups = (ListView) fragmentContent.findViewById(R.id.listview_groups);
        mListGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                StudentGroup group = (StudentGroup) mListGroups.getAdapter().getItem(position);
                ApplicationSettings.getInstance(view.getContext()).putString("defaultgroup", group.groupId);
                ScheduleWidgetProviderBase.updateAllWidgets(getActivity().getApplicationContext());
                Bundle args = new Bundle();
                args.putString("groupId", group.groupId);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment fragmentSchedule = new FragmentSchedule();
                fragmentSchedule.setArguments(args);
                fm.beginTransaction().replace(R.id.content_frame, fragmentSchedule).commit();
            }
        });
        return fragmentContent;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshListGroup();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_managergroups_fragment_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_startdeletegroups:
                Intent intent = new Intent(getActivity(), ActivityDeleteGroups.class);
                startActivity(intent);
                return true;
            case R.id.action_addgroup:
                if (isInternetAvaialable()) {
                    DialogFragmentAddGroup dialog = new DialogFragmentAddGroup();
                    dialog.show(getActivity().getSupportFragmentManager(), "");
                } else {
                    Toast.makeText(getActivity(), getString(R.string.internet_is_not_available), Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isInternetAvaialable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    void refreshListGroup() {
        ArrayList<StudentGroup> groups = mScheduleManager.getGroups();
        if (mGroupsAdapter == null) {
            mGroupsAdapter = new GroupsViewAdapter(getActivity(), groups, R.layout.view_group);
        } else {
            mGroupsAdapter.values.clear();
            mGroupsAdapter.values.addAll(groups);
            mGroupsAdapter.notifyDataSetChanged();
            mGroupsAdapter.notifyDataSetInvalidated();
        }
        if (mGroupsAdapter.getCount() != 0) {
            mListGroups.setAdapter(mGroupsAdapter);
            mTextViewNotification.setVisibility(View.INVISIBLE);
        } else {
            mTextViewNotification.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onPostExecute() {
        Toast.makeText(context, getString(R.string.schedule_is_updated), Toast.LENGTH_SHORT).show();
        refreshListGroup();
    }

    class DialogFragmentAddGroup extends DialogFragment {

        @Override
        public void onDestroyView() {
            if (getDialog() != null && getRetainInstance())
                getDialog().setOnDismissListener(null);
            super.onDestroyView();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            setRetainInstance(true);
            LayoutInflater inflater = getActivity().getLayoutInflater();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View contentView = inflater.inflate(R.layout.dialog_addgroup, null);
            builder.setView(contentView);
            final EditText etAddGroup = (EditText) contentView.findViewById(R.id.edittext_addgroup);
            builder.setTitle(getString(R.string.add_group))
                    .setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            String groupId = etAddGroup.getText().toString();
                            DownloadScheduleTask downloadScheduleTask = new DownloadScheduleTask(FragmentManagerGroups.this);
                            downloadScheduleTask.setPogressDialogMessage(getActivity().getString(R.string.loading_schedule));
                            downloadScheduleTask.execute(groupId);
                        }

                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DialogFragmentAddGroup.this.dismiss();
                        }
                    });
            return builder.create();
        }
    }
}
