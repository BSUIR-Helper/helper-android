package ru.bsuirhelper.android.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ru.bsuirhelper.android.R;

/**
 * Created by vladislav on 4/20/15.
 */
public class DrawerMenuAdapter extends ArrayAdapter<String> {
    public static final int SCHEDULE_FRAGMENT = 1;
    public static final int ACTIVITY_SETTINGS = 2;

    public DrawerMenuAdapter(Context context, String[] menuItems) {
        super(context, R.layout.drawer_list_item, menuItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parentView) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.drawer_list_item, null);
            setViewHolder(convertView);
        }
        ViewHolder vh = (ViewHolder) convertView.getTag();
        vh.menuName.setText(getItem(position));
        switch (position + 1) {
            case SCHEDULE_FRAGMENT:
                vh.icon.setImageResource(R.drawable.ic_timetable);
                break;
            case ACTIVITY_SETTINGS:
                vh.icon.setImageResource(R.drawable.ic_settings);
                break;
        }
        return convertView;
    }

    class ViewHolder {
        ImageView icon;
        TextView menuName;
    }

    private void setViewHolder(View v) {
        ViewHolder vh = new ViewHolder();
        vh.icon = (ImageView) v.findViewById(R.id.imageview_itemicon);
        vh.menuName = (TextView) v.findViewById(R.id.textview_itemname);
        v.setTag(vh);
    }
}
