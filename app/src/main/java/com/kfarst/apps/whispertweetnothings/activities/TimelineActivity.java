package com.kfarst.apps.whispertweetnothings.activities;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.apps.whispertweetnothings.R;
import com.codepath.apps.whispertweetnothings.databinding.ActivityTimelineBinding;
import com.kfarst.apps.whispertweetnothings.adapters.TweetsArrayAdapter;
import com.kfarst.apps.whispertweetnothings.api.TwitterApplication;
import com.kfarst.apps.whispertweetnothings.api.TwitterClient;
import com.kfarst.apps.whispertweetnothings.fragments.ComposeTweetFragment;
import com.kfarst.apps.whispertweetnothings.models.TimelineViewModel;
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

import java.io.IOException;
import java.util.ArrayList;

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
    public static final int REQUEST_CODE = 200;

    private TwitterClient client = TwitterApplication.getRestClient();
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter adapter;
    private User currentUser;
    private TimelineViewModel timelineViewModel = new TimelineViewModel();
    private ActivityTimelineBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        binding.setTimelineViewModel(timelineViewModel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        // Custom toolbar for displaying rounded profile image
        getSupportActionBar().setCustomView(R.layout.actionbar_timeline);

        ButterKnife.bind(this);

        setupViews();
        getCurrentUser();
        populateTimeline(null);
    }

    private void getCurrentUser() {
        currentUser = User.byCurrentUser();

        // If currentUser is null we do not have the user persisted, fetch from the API
        if (currentUser == null) {
            client.getCurrentUser(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    currentUser = User.findOrCreateFromJSON(response);
                    // Need to set the user to currentUser for next DB query
                    currentUser.setCurrentUser(true);
                    currentUser.save();

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
        } else {
            // Fetch the profile image since the user is already persisted at this point
            Glide.with(ivToolbarProfileImage.getContext())
                    .load(currentUser.getProfileImageUrl())
                    .bitmapTransform(new CropCircleTransformation(ivToolbarProfileImage.getContext()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivToolbarProfileImage);
        }
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

    private void populateTimeline(final Long maxId) {
        // If not online and no tweets loaded, fetch them from the database
        if (!isOnline() && tweets.size() == 0) {
            tweets.addAll(Tweet.recentItems());
            adapter.notifyDataSetChanged();
            Snackbar offlineSnackbar = Snackbar.make(lvTweets, R.string.offline_warning_message, Snackbar.LENGTH_LONG);
            ColoredSnackBar.warning(offlineSnackbar).show();
        }

        client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Tweet> list = new ArrayList<Tweet>();

                // No maxId clears the DB of tweets and adds new tweets from the refreshed timeline
                // so a large number of tweets is not built up over time
                if (maxId == null) {
                    Tweet.deleteAll();
                    tweets.clear();
                    pullToRefreshView.setRefreshing(false);
                }

                tweets.addAll(Tweet.fromJSON(response));
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Snackbar errorSnackbar = Snackbar.make(lvTweets, R.string.offline_error_message, Snackbar.LENGTH_SHORT);
                ColoredSnackBar.alert(errorSnackbar).show();
            }
        });
    }

    @OnClick(R.id.fabCompose)
    public void openComposeDialog(View view) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetFragment composeDialog = ComposeTweetFragment.newInstance(null);
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        composeDialog.show(fm, "fragment_compose_tweet");
    }

    @Override
    public void onFinishEditDialog(Tweet tweet) {
        client.postStatus(tweet, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // New tweet has been composed, add to the timeline
                tweets.add(0, Tweet.fromJSON(response));
                adapter.notifyItemInserted(0);

                // Scroll to top of timeline to see new composed tweet
                lvTweets.scrollToPosition(0);

                Snackbar snackbar = Snackbar.make(lvTweets, R.string.tweet_success_message, Snackbar.LENGTH_SHORT);
                ColoredSnackBar.confirm(snackbar).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String errorMessage = getErrorsFromResponse(errorResponse);

                // If API returns errors, show them to the user
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

                // Concatenate errors into one string separated by a space
                return TextUtils.join(" ", errorString);
            }
        });
    }

    @Override
    public void onTweetClick(final Tweet tweet) {
        Intent tweetIntent = new Intent(this, TweetActivity.class);
        tweetIntent.putExtra("tweet", Parcels.wrap(tweet));

        // If offline do not allow user to reply to a tweet
        tweetIntent.putExtra("isOnline", timelineViewModel.isOnline.get());

        PendingIntent pendingIntent =
                TaskStackBuilder.create(this)
                        // add all of DetailsActivity's parents to the stack,
                        // followed by DetailsActivity itself
                        .addNextIntentWithParentStack(tweetIntent)
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(pendingIntent);

        startActivityForResult(tweetIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Tweet tweet = Parcels.unwrap(data.getExtras().getParcelable("tweet"));

            // New tweet is always created, so only add to timeline if it has been posted to the API
            if (tweet.getUid() != null) {
                tweets.add(0, tweet);
                adapter.notifyItemInserted(0);
                lvTweets.scrollToPosition(0);
            }
        }
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            timelineViewModel.isOnline.set(exitValue == 0);
            return timelineViewModel.isOnline.get();
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        timelineViewModel.isOnline.set(false);
        return timelineViewModel.isOnline.get();
    }
}
