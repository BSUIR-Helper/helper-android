package ru.bsuirhelper.android.app.ui.adapter;

import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.app.db.entities.ScheduleLesson;

/**
 * Created by Grishechko on 04.02.2016.
 */
public class ScheduleOfDayAdapter extends PagerAdapter {

    private List<ScheduleLesson> mData;

    public ScheduleOfDayAdapter(List<ScheduleLesson> data) {
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Resources res = container.getContext().getResources();
        RecyclerView recyclerView = new RecyclerView(container.getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setPadding(res.getDimensionPixelOffset(R.dimen.activity_horizontal_margin), 0, res.getDimensionPixelOffset(R.dimen.activity_horizontal_margin), 0);
        recyclerView.setAdapter(new ScheduleAdapter(mData));
        recyclerView.setTag(position);
        container.addView(recyclerView);
        return recyclerView;
    }

}
