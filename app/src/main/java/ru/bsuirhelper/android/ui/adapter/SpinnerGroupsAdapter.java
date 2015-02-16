package ru.bsuirhelper.android.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ru.bsuirhelper.android.ApplicationSettings;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.models.StudentGroup;

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
        //Need for last button in spinner
        //groups.add(groups.size(), null);
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.spinner_view_group, null);
            setViewHolder(view);
        }
        final ViewHolder vh = (ViewHolder) view.getTag();
        vh.tvGroupName.setText(groups.get(position).toString());

        return view;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup viewGroup) {
        if (groups.size() - 1 == position) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.view_text_dropdown_spinner, null);
            return view;
        }

        if (view == null || view.getTag() == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.view_dropdown_spinner, null);
            setViewHolderDropdown(view);
        }
        ViewHolderDropdown vh = (ViewHolderDropdown) view.getTag();
        vh.tvGroupName.setText(groups.get(position).groupId + " (" + groups.get(position).faculty + ")");
        if (isActiveGroup(groups.get(position).groupId)) {
            vh.tvIsActive.setText(mContext.getString(R.string.active_group));
            vh.tvIsActive.setVisibility(View.VISIBLE);
        } else {
            vh.tvIsActive.setVisibility(View.GONE);
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
