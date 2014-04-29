package ru.bsuirhelper.android.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.lang.String;

import ru.bsuirhelper.android.R;

/**
 * Created by Влад on 13.10.13.
 */
public class DialogFragmentAddGroup extends DialogFragment implements DownloadScheduleTask.CallBack {
    private static final String ADD_GROUP = "Добавить группу";
    private static final String ADD = "Добавить":
    private static final String INFORMATION = "Информация";
    private static final String ALERT_MESSAGE = "Интернет соединение отсутствует, проверьте подключение к интернету";
    private static final String SCHEDULE_LOADING = "Загрузка расписания";
    private static final String CANCEL = "Отмена";

    private Context context;
    private DownloadScheduleTask.CallBack callBack;

    public DialogFragmentAddGroup(DownloadScheduleTask.CallBack callBack, Context context) {
        this.context = context;
        this.callBack = callBack;
    }

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
        builder.setTitle(ADD_GROUP)
                .setPositiveButton(ADD, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                        boolean isConnected = activeNetwork != null &&
                                activeNetwork.isConnectedOrConnecting();

                        if (!isConnected) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                            alert.setTitle(INFORMATION)
                                    .setMessage(ALERT_MESSAGE);
                            alert.show();
                        } else {
                            String groupId = etAddGroup.getText().toString();
                            DownloadScheduleTask downloadScheduleTask = new DownloadScheduleTask(DialogFragmentAddGroup.this);
                            downloadScheduleTask.setPogressDialogMessage(SCHEDULE_LOADING);
                            downloadScheduleTask.execute(groupId);
                        }
                    }
                })
                .setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogFragmentAddGroup.this.dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onPostExecute() {
        callBack.onPostExecute();
    }
}
