package com.kfarst.apps.whispertweetnothings.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kfarst.apps.whispertweetnothings.fragments.HomeTimelineFragment;
import com.kfarst.apps.whispertweetnothings.fragments.MentionsTimelineFragment;

/**
 * Created by kfarst on 8/9/16.
 */
public class TimelineFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Home", "Mentions" };
    private Context context;

    public TimelineFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return HomeTimelineFragment.newInstance();
            default:
                return MentionsTimelineFragment.newInstance();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
