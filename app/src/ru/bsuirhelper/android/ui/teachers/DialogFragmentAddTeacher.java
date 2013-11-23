package ru.bsuirhelper.android.ui.teachers;

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
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.ui.DownloaderTaskFragment;

/**
 * Created by Влад on 17.11.13.
 */
public class DialogFragmentAddTeacher extends DialogFragment {

    private final DownloaderTaskFragment mDownloaderTaskFragment;

    public DialogFragmentAddTeacher(DownloaderTaskFragment downloaderTaskFragment) {
        mDownloaderTaskFragment = downloaderTaskFragment;
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
        builder.setTitle("Добавить преподавателя")
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
                            mDownloaderTaskFragment.start(groupId);
                        }
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogFragmentAddTeacher.this.dismiss();
                    }
                });
        return builder.create();
    }
}
