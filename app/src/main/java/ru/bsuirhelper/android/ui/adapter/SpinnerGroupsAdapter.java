package ru.bsuirhelper.android.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.List;

import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.core.ApplicationSettings;
import ru.bsuirhelper.android.core.models.StudentGroup;
import ru.bsuirhelper.android.ui.listener.OnDeleteScheduleListener;
import ru.bsuirhelper.android.ui.listener.OnEditScheduleListener;

/**
 * Created by Влад on 19.03.14.
 */
public class SpinnerGroupsAdapter extends BaseAdapter {
    private List<StudentGroup> groups;
    private Context mContext;
    private ApplicationSettings mSettings;
    private View.OnClickListener onClickListener;
    private OnDeleteScheduleListener onDeleteScheduleListener;
    private OnEditScheduleListener onEditScheduleListener;
    private int TAG_STUDENT_GROUP = "student_group".hashCode();

    public void setOnAddScheduleClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnDeleteScheduleListener(OnDeleteScheduleListener onDeleteScheduleListener) {
        this.onDeleteScheduleListener = onDeleteScheduleListener;
    }

    public void setOnEditScheduleListener(OnEditScheduleListener onEditScheduleListener) {
        this.onEditScheduleListener = onEditScheduleListener;
    }

    public SpinnerGroupsAdapter(Context context, List<StudentGroup> groups) {
        this.groups = groups;
        mContext = context;
        mSettings = ApplicationSettings.getInstance(mContext);
    }

    public void setGroups(List<StudentGroup> groups) {
        this.groups = groups;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return groups == null ? 0 : groups.size() + 1;
    }

    @Override
    public Object getItem(int i) {
        return groups.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.spinner_view_group, null);
            setViewHolder(view);
        }
        final ViewHolder vh = (ViewHolder) view.getTag();
        if(groups == null || groups.size() == 0) {
            //TODO Вынести в строковый ресурс
            vh.tvGroupName.setText("Добавить расписание");
        } else {
            vh.tvGroupName.setText(groups.get(position).getGroupName());
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup viewGroup) {
        if (groups.size() == position) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.view_text_dropdown_spinner, null);
            view.setOnClickListener(onClickListener);
            return view;
        }

        if (view == null || view.getTag() == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.view_dropdown_spinner, null);
            Logger.i(groups.get(position) + "");
            setViewHolderDropdown(view, groups.get(position));
        }

        ViewHolderDropdown vh = (ViewHolderDropdown) view.getTag();
        vh.tvGroupName.setText(groups.get(position).getGroupName());
        if (isActiveGroup(String.valueOf(groups.get(position).getId()))) {
            vh.tvIsActive.setText(mContext.getString(R.string.active_group));
            vh.tvIsActive.setVisibility(View.VISIBLE);
        } else {
            vh.tvIsActive.setVisibility(View.GONE);
        }
        return view;
    }

    private boolean isActiveGroup(String groupId) {
        String defaultGroupName = mSettings.getActiveGroup();
        return defaultGroupName != null && defaultGroupName.equals(groupId);
    }

    private void setViewHolder(View view) {
        ViewHolder vh = new ViewHolder(view);
        view.setTag(vh);
    }

    private void setViewHolderDropdown(View view, StudentGroup studentGroup) {
        ViewHolderDropdown vh = new ViewHolderDropdown(view, studentGroup);
        view.setTag(vh);
    }

    class ViewHolderDropdown {
        TextView tvGroupName;
        TextView tvIsActive;
        View btnEditSchedule;
        View btnDeleteSchedule;

        public ViewHolderDropdown(View view, final StudentGroup studentGroup) {

            view.setTag(TAG_STUDENT_GROUP, studentGroup);
            tvGroupName = (TextView) view.findViewById(R.id.tv_dropdown_groupname);
            tvIsActive = (TextView) view.findViewById(R.id.textview_dropdown_group_default);
            btnEditSchedule = view.findViewById(R.id.lv_edit_schedule);
            btnDeleteSchedule = view.findViewById(R.id.lv_delete_schedule);

            btnDeleteSchedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                 onDeleteScheduleListener.onDeleteSchedule(studentGroup);
                }
            });

            btnEditSchedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onEditScheduleListener != null) {
                        onEditScheduleListener.onEditScheduleListener(studentGroup);
                    }
                }
            });
        }
    }

    class ViewHolder {
        TextView tvGroupName;

        public ViewHolder(View view) {
            tvGroupName = (TextView) view.findViewById(R.id.textview_group_name);
        }
    }
}
