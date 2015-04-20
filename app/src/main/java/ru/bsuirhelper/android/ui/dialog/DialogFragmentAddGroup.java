package ru.bsuirhelper.android.ui.dialog;

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

import com.orhanobut.logger.Logger;

import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.ui.asynctask.DownloadScheduleTask;
import ru.bsuirhelper.android.ui.listener.AsyncTaskListener;

/**
 * Created by vladislav on 4/20/15.
 */
public class DialogFragmentAddGroup extends DialogFragment {

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
                        AsyncTaskListener listener = null;

                        try {
                            listener = (AsyncTaskListener) getParentFragment();
                            if (listener == null) {
                                listener = (AsyncTaskListener) getActivity();
                            }
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                        }

                        Logger.i(listener + "");
                        DownloadScheduleTask downloadScheduleTask = new DownloadScheduleTask(getActivity(), listener);
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

    private boolean isInternetAvaialable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}