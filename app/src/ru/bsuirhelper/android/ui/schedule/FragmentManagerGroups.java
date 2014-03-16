package ru.bsuirhelper.android.ui.schedule;

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
import android.view.*;
import android.widget.*;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import ru.bsuirhelper.android.ApplicationSettings;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.schedule.ScheduleManager;
import ru.bsuirhelper.android.core.schedule.StudentGroup;
import ru.bsuirhelper.android.ui.DownloadScheduleTask;

import java.util.ArrayList;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentContent = inflater.inflate(R.layout.activity_managerschedule, container, false);
        mTextViewNotification = (TextView) fragmentContent.findViewById(R.id.textview_groupsarenotadded);
        mListGroups = (ListView) fragmentContent.findViewById(R.id.listview_groups);
        refreshListGroup();
        mListGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                StudentGroup group = (StudentGroup) mListGroups.getAdapter().getItem(position);
                ApplicationSettings.getInstance(view.getContext()).putString("defaultgroup", group.groupId);
                //  ScheduleWidgetProviderBase.getInstance().updateAppWidget(getActivity());
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
        if (ApplicationSettings.getInstance(getActivity()).getBoolean("firststart", true)) {
            //     openDrawerMenu();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
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
                DialogFragmentAddGroup dialog = new DialogFragmentAddGroup();
                dialog.show(getActivity().getSupportFragmentManager(), "");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        Toast.makeText(context, "Расписание добавлено", Toast.LENGTH_SHORT).show();
        refreshListGroup();
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker tracker = EasyTracker.getInstance(getActivity());
        tracker.set(Fields.SCREEN_NAME, "Окно списка групп");
        tracker.send(MapBuilder.createAppView().build());
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
            builder.setTitle("Добавить группу")
                    .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                            boolean isConnected = activeNetwork != null &&
                                    activeNetwork.isConnectedOrConnecting();

                            if (!isConnected) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                alert.setTitle("Информация")
                                        .setMessage("Интернет соединение отсуствует, проверьте подключение к интернету");
                                alert.show();
                            } else {
                                String groupId = etAddGroup.getText().toString();
                                DownloadScheduleTask downloadScheduleTask = new DownloadScheduleTask(FragmentManagerGroups.this);
                                downloadScheduleTask.setPogressDialogMessage("Загрузка расписания");
                                downloadScheduleTask.execute(groupId);
                            }
                        }
                    })
                    .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DialogFragmentAddGroup.this.dismiss();
                        }
                    });
            return builder.create();

        }
    }
}
