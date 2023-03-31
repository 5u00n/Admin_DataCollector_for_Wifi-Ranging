package com.projectopel.admindatacollector.UI.ViewData.DataProcessing;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.projectopel.admindatacollector.UI.ViewData.DataProcessFragments.ListFragment;
import com.projectopel.admindatacollector.UI.ViewData.DataProcessFragments.MapsFragment;

public class DataProcessingAdapter  extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public DataProcessingAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return new ListFragment();
            case 1:
                return new MapsFragment();
            default:
                return new ListFragment();
        }

    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
