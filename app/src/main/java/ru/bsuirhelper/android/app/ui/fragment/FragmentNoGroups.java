package ru.bsuirhelper.android.app.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.bsuirhelper.android.R;

/**
 * Created by Влад on 12.10.13.
 */
public class FragmentNoGroups extends Fragment{
    public static final String TITLE = "Расписание";

    public static FragmentNoGroups newInstance() {
        FragmentNoGroups fragmentNoGroups = new FragmentNoGroups();
        return fragmentNoGroups;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentContent = inflater.inflate(R.layout.activity_managerschedule, container, false);
        return fragmentContent;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
