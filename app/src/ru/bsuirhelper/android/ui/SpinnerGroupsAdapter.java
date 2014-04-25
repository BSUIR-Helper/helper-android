package ru.bsuirhelper.android.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ru.bsuirhelper.android.ApplicationSettings;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.schedule.StudentGroup;

import java.util.ArrayList;

/**
 * Created by Влад on 19.03.14.
 */
public class SpinnerGroupsAdapter extends BaseAdapter {
    private ArrayList<StudentGroup> groups;
    private Context mContext;
    private ApplicationSettings mSettings;

    public SpinnerGroupsAdapter(Context context, ArrayList<StudentGroup> groups) {
        this.groups = groups;
        mContext = context;
        mSettings = ApplicationSettings.getInstance(mContext);
    }

    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public Object getItem(int i) {
        return groups.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.spinner_view_group, null);
            setViewHolder(view);
        }
        final ViewHolder vh = (ViewHolder) view.getTag();
        vh.tvGroupName.setText(groups.get(i).toString());
        return view;
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.view_dropdown_spinner, null);
            setViewHolderDropdown(view);
        }

        ViewHolderDropdown vh = (ViewHolderDropdown) view.getTag();
        Log.d(ActivityDrawerMenu.LOG_TAG, vh.tvGroupName + " ");
        vh.tvGroupName.setText(groups.get(i).groupId + " (" + groups.get(i).faculty + ")");
        if (isActiveGroup(groups.get(i).groupId)) {
            vh.tvIsActive.setText("АКТИВНО");
        } else {
            vh.tvIsActive.setText("");
        }

        return view;
    }

    private boolean isActiveGroup(String groupName) {
        String defaultGroupName = mSettings.getDefaultGroupOfSchedule();
        return groupName.equals(defaultGroupName);
    }

    private void setViewHolder(View view) {
        ViewHolder vh = new ViewHolder(view);
        view.setTag(vh);
    }

    private void setViewHolderDropdown(View view) {
        ViewHolderDropdown vh = new ViewHolderDropdown(view);
        view.setTag(vh);
    }


    class ViewHolderDropdown {
        TextView tvGroupName;
        TextView tvIsActive;

        public ViewHolderDropdown(View view) {
            tvGroupName = (TextView) view.findViewById(R.id.textview_dropdown_groupname);
            tvIsActive = (TextView) view.findViewById(R.id.textview_dropdown_group_default);
        }
    }

    class ViewHolder {
        TextView tvGroupName;

        public ViewHolder(View view) {
            tvGroupName = (TextView) view.findViewById(R.id.textview_group_name);
        }
    }
}
