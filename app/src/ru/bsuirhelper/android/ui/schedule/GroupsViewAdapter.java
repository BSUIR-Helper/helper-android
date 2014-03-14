package ru.bsuirhelper.android.ui.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.schedule.StudentGroup;

/**
 * Created by Влад on 12.10.13.
 */
class GroupsViewAdapter extends ArrayAdapter<StudentGroup> {
    public StudentGroup[] values;
    private final Context mContext;
    private final int mViewId;

    public GroupsViewAdapter(Context context, StudentGroup[] values, int viewId) {
        super(context, R.layout.view_group, values);
        this.values = values;
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
        String groupId = values[position].groupId;
        String faculty = values[position].faculty;
        if (faculty != null) {
            faculty = "(" + faculty + ")";
        } else {
            faculty = "";
        }
        tvGroupId.setText(groupId + " " + faculty);
        tvDateUpdate.setText("Обновлено: " + values[position].updatedTime);
        return rowView;
    }

}
