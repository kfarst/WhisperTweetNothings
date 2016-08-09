package com.kfarst.apps.whispertweetnothings.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

import com.codepath.apps.whispertweetnothings.R;
import com.kfarst.apps.whispertweetnothings.adapters.TweetsArrayAdapter;
import com.kfarst.apps.whispertweetnothings.api.TwitterApplication;
import com.kfarst.apps.whispertweetnothings.api.TwitterClient;
import com.kfarst.apps.whispertweetnothings.models.TimelineViewModel;
import com.kfarst.apps.whispertweetnothings.models.Tweet;
import com.kfarst.apps.whispertweetnothings.models.User;
import com.kfarst.apps.whispertweetnothings.support.DividerItemDecoration;
import com.kfarst.apps.whispertweetnothings.support.EndlessRecyclerViewScrollListener;
import com.kfarst.apps.whispertweetnothings.support.SmoothScrollLinearLayoutManager;
import com.yalantis.taurus.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kfarst on 8/8/16.
 */
public abstract class TweetsListFragment extends Fragment {
    @BindView(R.id.lvTweets) RecyclerView lvTweets;
    @BindView(R.id.pullToRefresh) PullToRefreshView pullToRefreshView;

    public static final int REFRESH_DELAY = 2000;
    public static final int REQUEST_CODE = 200;

    protected TwitterClient client = TwitterApplication.getRestClient();
    protected ArrayList<Tweet> tweets;
    protected TweetsArrayAdapter adapter;
    protected User currentUser;
    protected TimelineViewModel timelineViewModel = new TimelineViewModel();

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        ButterKnife.bind(this);

        setupViews();
    }



    public void setupViews() {
        lvTweets.setHasFixedSize(true);

        SmoothScrollLinearLayoutManager linearLayoutManager = new SmoothScrollLinearLayoutManager(this);
        lvTweets.setLayoutManager(linearLayoutManager);

        lvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                populateTimeline(tweets.get(tweets.size() - 1).getUid());

            }
        });

        lvTweets.addItemDecoration(new DividerItemDecoration(this));

        tweets = new ArrayList<Tweet>();
        adapter = new TweetsArrayAdapter(tweets);
        adapter.setOnTweetClickListener(this);
        lvTweets.setAdapter(adapter);

        pullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        populateTimeline(null);
                        pullToRefreshView.setRefreshing(false);
                    }
                }, REFRESH_DELAY);
            }
        });
    }

    public void appendTweets(List<Tweet> tweets) {
        // add tweets to the adapter
    }

    // Abstract method to be overridden
    protected abstract void populateTimeline(long maxId);
}
