package com.kfarst.apps.whispertweetnothings.activities;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.kfarst.apps.whispertweetnothings.support.SmoothScrollLinearLayoutManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yalantis.taurus.PullToRefreshView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.PostStatusDialogListener, TweetsArrayAdapter.OnTweetClickListener {

    @BindView(R.id.lvTweets) RecyclerView lvTweets;
    @BindView(R.id.ivToolbarProfileImage) ImageView ivToolbarProfileImage;
    @BindView(R.id.pullToRefresh) PullToRefreshView pullToRefreshView;

    public static final int REFRESH_DELAY = 2000;

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

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_timeline);

        ButterKnife.bind(this);

        setupViews();
        getCurrentUser();
        populateTimeline(null);
    }

    private void getCurrentUser() {
        client.getCurrentUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                currentUser = User.fromJSON(response);

                Glide.with(ivToolbarProfileImage.getContext())
                        .load(currentUser.getProfileImageUrl())
                        .bitmapTransform(new CropCircleTransformation(ivToolbarProfileImage.getContext()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivToolbarProfileImage);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    private void setupViews() {
        lvTweets.setHasFixedSize(true);

        SmoothScrollLinearLayoutManager linearLayoutManager = new SmoothScrollLinearLayoutManager(this);
        lvTweets.setLayoutManager(linearLayoutManager);

        lvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                populateTimeline(tweets.get(tweets.size() - 1).getId());

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
                    }
                }, REFRESH_DELAY);
            }
        });
    }

    private void populateTimeline(final Long maxId) {
       client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
           @Override
           public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
               ArrayList<Tweet> list = Tweet.fromJSON(response);

               if (maxId == null) {
                   tweets.clear();
                   pullToRefreshView.setRefreshing(false);
               }

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
                tweets.add(0, Tweet.fromJSON(response));
                adapter.notifyItemInserted(0);
                lvTweets.scrollToPosition(0);
                Snackbar snackbar = Snackbar.make(lvTweets, "Status successfully tweeted.", Snackbar.LENGTH_SHORT);
                ColoredSnackBar.confirm(snackbar).show();
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

    @Override
    public void onTweetClick(final Tweet tweet) {
        Intent tweetIntent = new Intent(this, TweetActivity.class);
        tweetIntent.putExtra("tweet", Parcels.wrap(tweet));

        PendingIntent pendingIntent =
                TaskStackBuilder.create(this)
                        // add all of DetailsActivity's parents to the stack,
                        // followed by DetailsActivity itself
                        .addNextIntentWithParentStack(tweetIntent)
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(pendingIntent);

        startActivity(tweetIntent);
    }
}
