package ru.bsuirhelper.android.app.ui.adapter;

import android.graphics.Rect;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.app.core.AndroidUtils;
import ru.bsuirhelper.android.app.db.entities.ScheduleLesson;

/**
 * Created by Grishechko on 03.02.2016.
 */
public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<ScheduleLesson> mData;

    public ScheduleAdapter(List<ScheduleLesson> data) {
        mData = data;
    }

    @Override
    public ScheduleAdapter.ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ScheduleAdapter.ScheduleViewHolder holder, int position) {
        ScheduleLesson lesson = mData.get(position);
        holder.tvLessonAuditory.setText(lesson.getAuditory());
        holder.tvLessonName.setText(lesson.getSubject());
        holder.tvLessonTeacher.setText(lesson.getEmployee().getShortName());
        holder.ivAvatar.setImageResource(R.drawable.no_photo);
        holder.tvLessonType.setText(lesson.getType());
        holder.tvLessonTime.setText(lesson.getTime());

        holder.tvLessonType.setBackgroundResource(getLessonTypeBackgound(lesson.getType()));
        holder.tvLessonTime.setBackgroundResource(getLessonTimeBackgound(lesson.getType()));
        if(position == 0) {
            holder.itemView.setPadding(0, AndroidUtils.dp(29), 0, 0);
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @DrawableRes
    private int getLessonTimeBackgound(@NonNull String type) {
        if (TextUtils.equals("лк", type)) {
            return R.drawable.green_rectangle;
        } else if (TextUtils.equals("пз", type)) {
            return R.drawable.orange_rectangle;
        } else if (TextUtils.equals("лр", type)) {
            return R.drawable.red_rectangle;
        }
        return R.drawable.green_rectangle;
    }

    @DrawableRes
    private int getLessonTypeBackgound(@NonNull String type) {
        if (TextUtils.equals("лк", type)) {
            return R.drawable.green_circle;
        } else if (TextUtils.equals("пз", type)) {
            return R.drawable.orange_circle;
        } else if (TextUtils.equals("лр", type)) {
            return R.drawable.red_circle;
        }
        return R.drawable.green_circle;
    }

    class ScheduleViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_lesson_name) TextView tvLessonName;
        @Bind(R.id.tv_lesson_teacher) TextView tvLessonTeacher;
        @Bind(R.id.tv_lesson_auditory) TextView tvLessonAuditory;
        @Bind(R.id.tv_lesson_type) TextView tvLessonType;
        @Bind(R.id.tv_lesson_time) TextView tvLessonTime;
        @Bind(R.id.iv_avatar) ImageView ivAvatar;

        public ScheduleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
