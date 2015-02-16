package ru.bsuirhelper.android.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.models.StudentGroup;

import java.util.List;

/**
 * Created by Влад on 12.10.13.
 */
public class GroupsViewAdapter extends ArrayAdapter<StudentGroup> {
    public List<StudentGroup> values;
    private final Context mContext;
    private final int mViewId;

    public GroupsViewAdapter(Context context, List<StudentGroup> groups, int viewId) {
        super(context, R.layout.view_group, groups);
        this.values = groups;
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
        String groupId = values.get(position).groupId;
        tvGroupId.setText(groupId);
        tvDateUpdate.setText(mContext.getString(R.string.updated) + ": " + values.get(position).updatedTime);
        return rowView;
    }

}
