package com.kfarst.apps.whispertweetnothings.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.codepath.apps.whispertweetnothings.R;
import com.github.underscore.$;
import com.github.underscore.Function1;
import com.kfarst.apps.whispertweetnothings.adapters.TweetsArrayAdapter;
import com.kfarst.apps.whispertweetnothings.api.TwitterApplication;
import com.kfarst.apps.whispertweetnothings.api.TwitterClient;
import com.kfarst.apps.whispertweetnothings.fragments.ComposeTweetFragment;
import com.kfarst.apps.whispertweetnothings.models.Tweet;
import com.kfarst.apps.whispertweetnothings.support.DividerItemDecoration;
import com.kfarst.apps.whispertweetnothings.support.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.OnFragmentInteractionListener {

    @BindView(R.id.lvTweets) RecyclerView lvTweets;

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        setupViews();

        client = TwitterApplication.getRestClient();
        populateTimeline();
    }

    private void setupViews() {
        lvTweets.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        lvTweets.setLayoutManager(linearLayoutManager);

        lvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                populateTimeline();
            }
        });

        lvTweets.addItemDecoration(new DividerItemDecoration(this));

        tweets = new ArrayList<Tweet>();
        adapter = new TweetsArrayAdapter(tweets);
        lvTweets.setAdapter(adapter);
    }

    private void populateTimeline() {
        Long maxID = tweets.size() > 0 ? sortedTweetsById().get(0).getId() : null;

       client.getHomeTimeline(maxID, new JsonHttpResponseHandler() {
           @Override
           public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
               ArrayList<Tweet> list = Tweet.fromJSON(response);
               tweets.addAll(list);
               adapter.notifyDataSetChanged();
           }

           @Override
           public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
               Log.d("DEBUG", errorResponse.toString());
           }
       });
    }

    private List<Tweet> sortedTweetsById() {
        return $.sortBy(tweets, new Function1<Tweet, Long>() {
            public Long apply(Tweet tweet) {
                return tweet.getId();
            }
        });
    }

    @OnClick(R.id.fabCompose)
    public void openComposeDialog(View view) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetFragment composeDialog = ComposeTweetFragment.newInstance();
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        composeDialog.show(fm, "fragment_compose_tweet");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
