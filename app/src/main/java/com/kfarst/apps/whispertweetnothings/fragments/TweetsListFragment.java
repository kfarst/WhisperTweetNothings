package com.kfarst.apps.whispertweetnothings.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.whispertweetnothings.R;
import com.kfarst.apps.whispertweetnothings.activities.TimelineActivity;
import com.kfarst.apps.whispertweetnothings.activities.TweetActivity;
import com.kfarst.apps.whispertweetnothings.adapters.TweetsArrayAdapter;
import com.kfarst.apps.whispertweetnothings.api.TwitterApplication;
import com.kfarst.apps.whispertweetnothings.api.TwitterClient;
import com.kfarst.apps.whispertweetnothings.models.TimelineViewModel;
import com.kfarst.apps.whispertweetnothings.models.Tweet;
import com.kfarst.apps.whispertweetnothings.models.User;
import com.kfarst.apps.whispertweetnothings.support.DividerItemDecoration;
import com.kfarst.apps.whispertweetnothings.support.EndlessRecyclerViewScrollListener;
import com.kfarst.apps.whispertweetnothings.support.ItemClickSupport;
import com.kfarst.apps.whispertweetnothings.support.SmoothScrollLinearLayoutManager;
import com.yalantis.taurus.PullToRefreshView;

import org.parceler.Parcels;

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

    protected static final int REFRESH_DELAY = 2000;
    protected static final int REQUEST_CODE = 200;

    protected TwitterClient client = TwitterApplication.getRestClient();
    protected ArrayList<Tweet> tweets;
    protected TweetsArrayAdapter adapter;
    protected User currentUser;
    protected TimelineViewModel timelineViewModel = new TimelineViewModel();

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet_list, container, false);
        ButterKnife.bind(this, view);
        setupViews(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        populateTimeline(null);
    }

    protected void setupViews(View view) {
        lvTweets.setHasFixedSize(true);

        SmoothScrollLinearLayoutManager linearLayoutManager = new SmoothScrollLinearLayoutManager(view.getContext());
        lvTweets.setLayoutManager(linearLayoutManager);

        lvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                populateTimeline(tweets.get(tweets.size() - 1).getUid());

            }
        });

        lvTweets.addItemDecoration(new DividerItemDecoration(view.getContext()));

        tweets = new ArrayList<Tweet>();
        adapter = new TweetsArrayAdapter(tweets);
        adapter.setTweetReplyListener(tweet -> {
            TimelineActivity activity = (TimelineActivity) getActivity();
            activity.openComposeDialog(tweet);
        });
        lvTweets.setAdapter(adapter);

        ItemClickSupport.addTo(lvTweets).setOnItemClickListener((recyclerView, position, v) -> {
            Intent tweetIntent = new Intent(getActivity(), TweetActivity.class);
            tweetIntent.putExtra("tweet", Parcels.wrap(tweets.get(position)));

            // If offline do not allow user to reply to a tweet
            tweetIntent.putExtra("isOnline", timelineViewModel.isOnline.get());

            startActivityForResult(tweetIntent, REQUEST_CODE);
        });

        pullToRefreshView.setOnRefreshListener(() -> pullToRefreshView.postDelayed(() -> {
            populateTimeline(null);
            pullToRefreshView.setRefreshing(false);
        }, REFRESH_DELAY));
    }

    protected void appendTweets(List<Tweet> newTweets) {
        // add tweets to the adapter
        tweets.addAll(newTweets);
        adapter.notifyDataSetChanged();
    }

    public void prependTweet(Tweet newTweet) {
        // add tweets to the beginning of the adapter
        tweets.add(0, newTweet);
        adapter.notifyItemInserted(0);
        lvTweets.scrollToPosition(0);
    }

    // Abstract method to be overridden
    protected abstract void populateTimeline(Long maxId);
}
