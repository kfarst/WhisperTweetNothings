package com.kfarst.apps.whispertweetnothings.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.kfarst.apps.whispertweetnothings.models.User;
import com.kfarst.apps.whispertweetnothings.support.ColoredSnackBar;
import com.kfarst.apps.whispertweetnothings.support.DividerItemDecoration;
import com.kfarst.apps.whispertweetnothings.support.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.PostStatusDialogListener {

    @BindView(R.id.lvTweets) RecyclerView lvTweets;

    private TwitterClient client = TwitterApplication.getRestClient();
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter adapter;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        setupViews();
        getCurrentUser();
        populateTimeline();
    }

    private void getCurrentUser() {
        client.getCurrentUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                currentUser = User.fromJSON(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
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
        ComposeTweetFragment composeDialog = ComposeTweetFragment.newInstance(currentUser);
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        composeDialog.show(fm, "fragment_compose_tweet");
    }

    @Override
    public void onFinishEditDialog(Tweet tweet) {
        client.postStatus(tweet, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                tweets.add(Tweet.fromJSON(response));
                adapter.notifyItemInserted(tweets.size() - 1);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String errorMessage = getErrorsFromResponse(errorResponse);

                if (!TextUtils.isEmpty(errorMessage)) {
                    Snackbar snackbar = Snackbar.make(lvTweets, errorMessage, Snackbar.LENGTH_SHORT);
                    ColoredSnackBar.alert(snackbar).show();
                }
            }

            public String getErrorsFromResponse(JSONObject errorResponse) {
                ArrayList<String> errorString = new ArrayList<String>();

                try {
                    JSONArray errors = errorResponse.getJSONArray("errors");

                    for (int i = 0; i < errors.length(); i++) {
                        errorString.add(String.valueOf(errors.getJSONObject(i).get("message")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return TextUtils.join(" ", errorString);
            }
        });
    }
}
