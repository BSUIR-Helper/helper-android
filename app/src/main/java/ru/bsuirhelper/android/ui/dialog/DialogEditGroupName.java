package ru.bsuirhelper.android.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.cache.CacheContentProvider;
import ru.bsuirhelper.android.core.cache.CacheHelper;
import ru.bsuirhelper.android.core.models.StudentGroup;
import ru.bsuirhelper.android.ui.listener.OnDialogEditGroupNameComplete;

/**
 * Created by vladislav on 4/20/15.
 */
public class DialogEditGroupName extends DialogFragment {
    private final static String KEY_STUDENT_GROUP = "student_group";

    public static DialogEditGroupName newInstance(StudentGroup studentGroup) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_STUDENT_GROUP, studentGroup);
        DialogEditGroupName dialogFragment = new DialogEditGroupName();
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    //TODO Вынести в строковые ресурсы
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final StudentGroup studentGroup = getArguments().getParcelable(KEY_STUDENT_GROUP);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_change_schedule_name, null);
        final EditText edittext = (EditText) view.findViewById(R.id.et_group_name);
        edittext.setText(studentGroup.getGroupName());
        builder.setView(view).setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!TextUtils.isEmpty(edittext.getText().toString())) {
                    studentGroup.setGroupName(edittext.getText().toString());
                    getActivity().getContentResolver().
                            update(CacheContentProvider.STUDENTGROUP_URI, CacheHelper.StudentGroups.toContentValues(studentGroup), CacheHelper.StudentGroups._ID + " = " + studentGroup.getId(), null);

                    try {
                        OnDialogEditGroupNameComplete listener = (OnDialogEditGroupNameComplete) getActivity();
                        listener.onDialogEditComplete(edittext.getText().toString());
                    } catch (ClassCastException e) {
                        throw new ClassCastException(getActivity().toString()
                                + " must implement NoticeDialogListener");
                    }
                } else {
                    edittext.setError("Введите имя");
                }
            }
        }).
                setNegativeButton("Отмена", null).
                setTitle("Изменить название");
        return builder.create();
    }
}
