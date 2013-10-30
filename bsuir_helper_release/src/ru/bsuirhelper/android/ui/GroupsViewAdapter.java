package ru.bsuirhelper.android.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ru.bsuirhelper.android.StudentGroup;
import ru.bsuirhelper.android.R;

/**
 * Created by Влад on 12.10.13.
 */
class GroupsViewAdapter extends ArrayAdapter<StudentGroup> {
    private final StudentGroup[] mValues;
    private final Context mContext;
    private final int mViewId;

    public GroupsViewAdapter(Context context, StudentGroup[] values, int viewId) {
        super(context, R.layout.view_group, values);
        mValues = values;
        mContext = context;
        mViewId = viewId;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(mViewId, null);
        TextView tvGroupId = (TextView) rowView.findViewById(R.id.textview_groupid);
        TextView tvDateUpdate = (TextView) rowView.findViewById(R.id.textview_dateupdated);
        String groupId = mValues[position].groupId;
        String faculty = mValues[position].faculty;
        if (faculty != null) {
            faculty = "(" + faculty + ")";
        } else {
            faculty = "";
        }
        tvGroupId.setText(groupId + " " + faculty);
        tvDateUpdate.setText("Обновлено: " + mValues[position].updatedTime);
        return rowView;
    }
}
